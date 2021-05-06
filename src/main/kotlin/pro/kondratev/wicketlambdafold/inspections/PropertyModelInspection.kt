package pro.kondratev.wicketlambdafold.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiImmediateClassType
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl
import com.intellij.psi.util.PropertyUtilBase
import com.intellij.psi.util.PsiLiteralUtil
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.PsiUtil
import com.intellij.ui.awt.RelativePoint
import com.siyeh.ig.psiutils.ImportUtils
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import pro.kondratev.wicketlambdafold.*

const val FADEOUT_TIME = 7500L

class PropertyModelInspection : AbstractBaseJavaLocalInspectionTool() {

    private val descriptionTemplate = WicketLambdaFoldBundle.message("PropertyModelInspection")

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return if (!PsiUtil.isLanguageLevel8OrHigher(holder.file)) {
            PsiElementVisitor.EMPTY_VISITOR
        } else object : JavaElementVisitor() {
            override fun visitExpression(expression: PsiExpression?) {
                when (expression) {
                    is PsiNewExpression -> if (PROPERTY_MODEL_FQN == expression.classReference?.qualifiedName) {
                        holder.registerProblem(expression, descriptionTemplate, PropertyModelLocalQuickFix())
                    }
                    is PsiMethodCallExpression -> {
                        val q = expression.methodExpression.qualifier
                        if (q is PsiReferenceExpression && PROPERTY_MODEL_FQN == q.qualifiedName) {
                            holder.registerProblem(expression, descriptionTemplate, PropertyModelLocalQuickFix())
                        }
                    }
                }
                super.visitExpression(expression)
            }
        }
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    internal inner class PropertyModelLocalQuickFix : LocalQuickFix {

        override fun getFamilyName(): String {
            return WicketLambdaFoldBundle.message("PropertyModelInspection.PropertyModelLocalQuickFix")
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val element = descriptor.psiElement
            when (element) {
                is PsiNewExpression -> fixPropertyModel(project, element)
                is PsiMethodCallExpression -> fixPropertyModel(project, element)
                else -> {
                    warning(project, "Unexpected element type")
                    return
                }
            }
        }

        private fun fixPropertyModel(project: Project, element: PsiExpression) {
            val factory = JavaPsiFacade.getElementFactory(project)
            val lambdaModelClass = importLambdaIfNeeded(project, element)
            val iModelClass = resolveClass(IMODEL_INTERFACE_FQN, project, element)
            val args = element.children.find { it is PsiExpressionList } as PsiExpressionList
            val (modelArg, propNameArg) = args.expressions
            assert(propNameArg is PsiLiteralExpressionImpl)
            val modelArgType = GenericsUtil.getVariableTypeByExpressionType(modelArg.type) as PsiImmediateClassType
            if (
                modelArgType.resolve()?.equals(iModelClass) != true &&
                modelArgType.resolve()?.isInheritor(iModelClass, true) != true
            ) {
                throw IllegalStateException("LambdaModel only supports IModel implementors as model parameter")
            }
            val genericParam = modelArgType.parameters[0]
            val modelObjectClass = PsiTypesUtil.getPsiClass(
                if (genericParam is PsiWildcardType) genericParam.bound else genericParam
            )!!
            val propName = PsiLiteralUtil.getStringLiteralContent(propNameArg as PsiLiteralExpressionImpl)!!
            val getter: PsiMethod? = PropertyUtilBase.findPropertyGetter(
                modelObjectClass, propName, false, true
            )
            val setter: PsiMethod? = PropertyUtilBase.findPropertySetter(
                modelObjectClass, propName, false, true
            )
            if (getter != null) {
                val methodQualifierPrefix = modelObjectClass.qualifiedName + "::"

                val newArgs = when (modelArg) {
                    is PsiReferenceExpressionImpl -> {
                        mutableListOf(modelArg.qualifiedName, methodQualifierPrefix + getter.name)
                    }
                    is PsiMethodCallExpression -> {
                        mutableListOf(modelArg.text, methodQualifierPrefix + getter.name)
                    }
                    else -> {
                        throw IllegalStateException("Unsupported model argument type")
                    }
                }

                val isMap = if (setter != null) {
                    newArgs.add(methodQualifierPrefix + setter.name)
                    false
                } else lambdaModelClass.allMethods.any() { psiMethod -> psiMethod.name == "map" }
                val lambdaModelExpression = if (isMap) {
                    factory.createExpressionFromText(
                        newArgs[0] + ".map(" + newArgs[1] + ")", element
                    )
                } else {
                    factory.createExpressionFromText(
                        LAMBDA_MODEL_NAME + ".of(" + newArgs.joinToString(", ") + ")", element
                    )
                }

                element.replace(lambdaModelExpression)
            } else {
                warning(project, "Can't find getter for property " + propName)
            }
        }

        private fun importLambdaIfNeeded(project: Project, element: PsiElement): @NotNull PsiClass {
            val lambdaModelClass = resolveClass(LAMBDA_MODEL_FQN, project, element)
            ImportUtils.addImportIfNeeded(lambdaModelClass, element)
            return lambdaModelClass
        }

        private fun warning(project: Project, message: String) {
            val logger = Logger.getInstance(this.javaClass.simpleName)
            logger.warn(message)
            val statusBar = WindowManager.getInstance().getStatusBar(project)

            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, MessageType.WARNING, null)
                .setFadeoutTime(FADEOUT_TIME)
                .createBalloon()
                .show(
                    RelativePoint.getCenterOf(statusBar.component),
                    Balloon.Position.atRight)
        }
    }

    private fun resolveClass(
        fqn: String,
        project: Project,
        element: PsiElement
    ): @Nullable PsiClass {
        val resolveHelper = JavaPsiFacade.getInstance(project).resolveHelper
        return resolveHelper.resolveReferencedClass(fqn, element)!!
    }
}
