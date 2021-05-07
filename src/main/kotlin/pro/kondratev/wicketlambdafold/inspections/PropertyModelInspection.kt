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
import com.intellij.psi.impl.source.PsiClassReferenceType
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
                    throw showErrorAndThrow(project, "Unexpected element type")
                }
            }
        }

        private fun fixPropertyModel(project: Project, element: PsiExpression) {
            val iModelClass = resolveClass(IMODEL_INTERFACE_FQN, project, element)
                ?: throw showErrorAndThrow(project, "Can't resolve IModel interface")
            val args = element.children.find { it is PsiExpressionList } as PsiExpressionList
            val (modelArg, propNameArg) = args.expressions
            assert(propNameArg is PsiLiteralExpressionImpl)
            val modelArgType = GenericsUtil.getVariableTypeByExpressionType(modelArg.type) as PsiImmediateClassType
            if (
                modelArgType.resolve()?.equals(iModelClass) != true &&
                modelArgType.resolve()?.isInheritor(iModelClass, true) != true
            ) {
                throw showErrorAndThrow(project, "LambdaModel only supports IModel implementors as model parameter")
            }
            val genericParam = modelArgType.parameters[0]
            var modelObjectClass = PsiTypesUtil.getPsiClass(
                if (genericParam is PsiWildcardType) genericParam.bound else genericParam
            )!!
            var propName = PsiLiteralUtil.getStringLiteralContent(propNameArg as PsiLiteralExpressionImpl)!!
            var modelExpression = modelArg
            if (propName.contains(".")) {
                val propertiesChain = propName.split(".").toMutableList()
                propName = propertiesChain.last()
                for (prop in propertiesChain.dropLast(1)) {
                    val (returnClass, expression) = buildExpression(
                        modelObjectClass,
                        modelExpression,
                        prop,
                        true,
                        project,
                        element
                    )
                    modelExpression = expression
                    modelObjectClass = returnClass
                }
            }
            modelExpression =
                buildExpression(modelObjectClass, modelExpression, propName, false, project, element).second

            element.replace(modelExpression)
        }

        private fun buildExpression(
            modelObjectClass: PsiClass,
            modelArg: PsiExpression,
            propName: String,
            readOnly: Boolean,
            project: Project,
            element: PsiElement
        ): Pair<@Nullable PsiClass, @NotNull PsiExpression> {
            val getter: PsiMethod? = PropertyUtilBase.findPropertyGetter(
                modelObjectClass, propName, false, true
            )
            val setter: PsiMethod? = if (readOnly) null else PropertyUtilBase.findPropertySetter(
                modelObjectClass, propName, false, true
            )
            if (getter != null) {
                val factory = JavaPsiFacade.getElementFactory(project)
                val lambdaModelClass = importLambdaIfNeeded(project, element)
                val methodQualifierPrefix = modelObjectClass.qualifiedName + "::"

                val newArgs = when (modelArg) {
                    is PsiReferenceExpressionImpl -> {
                        mutableListOf(modelArg.qualifiedName, methodQualifierPrefix + getter.name)
                    }
                    is PsiMethodCallExpression -> {
                        mutableListOf(modelArg.text, methodQualifierPrefix + getter.name)
                    }
                    else -> {
                        throw showErrorAndThrow(project, "Unsupported model argument type")
                    }
                }

                val isMap = if (setter != null) {
                    newArgs.add(methodQualifierPrefix + setter.name)
                    false
                } else lambdaModelClass.allMethods.any() { psiMethod -> psiMethod.name == "map" }

                var returnModelClass = resolveClass(getter.returnType!!.canonicalText, project, element)
                if (returnModelClass == null) {
                    // The return type can't be resolved by FQN, this is probably some kind of generic
                    returnModelClass = (getter.returnType as PsiClassReferenceType).resolve()?.superClass
                }
                if (returnModelClass == null) {
                    throw showErrorAndThrow(project, "Can't resolve return class for getter " + getter.name)
                }
                return if (isMap) {
                    Pair(
                        returnModelClass, factory.createExpressionFromText(
                            newArgs[0] + ".map(" + newArgs[1] + ")", element
                        )
                    )
                } else {
                    Pair(
                        returnModelClass, factory.createExpressionFromText(
                            LAMBDA_MODEL_NAME + ".of(" + newArgs.joinToString<@NotNull String>(", ") + ")", element
                        )
                    )
                }
            } else {
                throw showErrorAndThrow(project, "Can't find getter for property " + propName)
            }
        }

        private fun importLambdaIfNeeded(project: Project, element: PsiElement): @NotNull PsiClass {
            val lambdaModelClass = resolveClass(LAMBDA_MODEL_FQN, project, element)!!
            ImportUtils.addImportIfNeeded(lambdaModelClass, element)
            return lambdaModelClass
        }

        private fun showErrorAndThrow(project: Project, message: String): IllegalStateException {
            val logger = Logger.getInstance(this.javaClass.simpleName)
            logger.warn(message)
            val statusBar = WindowManager.getInstance().getStatusBar(project)

            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, MessageType.WARNING, null)
                .setFadeoutTime(FADEOUT_TIME)
                .createBalloon()
                .show(
                    RelativePoint.getCenterOf(statusBar.component),
                    Balloon.Position.atRight
                )
            return IllegalStateException(message)
        }
    }

    private fun resolveClass(
        fqn: String,
        project: Project,
        element: PsiElement
    ): PsiClass? {
        val resolveHelper = JavaPsiFacade.getInstance(project).resolveHelper
        return resolveHelper.resolveReferencedClass(fqn, element)
    }
}
