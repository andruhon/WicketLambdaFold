package pro.kondratev.wicketlambdafold.language

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTreeUtil
import pro.kondratev.wicketlambdafold.LAMBDA_MODEL_FQN
import pro.kondratev.wicketlambdafold.LAMBDA_MODEL_NAME

/**
 * LambdaModel.of with model getter and setter should have exactly 3 params.
 * 2 params is read only and is already short enough.
 */
const val FOLDING_PARAMS_COUNT = 3

class LambdaModelFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, b: Boolean): Array<FoldingDescriptor> {
        val descriptors = ArrayList<FoldingDescriptor>()

        if (root !is PsiJavaFile) {
            // Plugin only supports java currently
            return descriptors.toTypedArray()
        }

        if (PsiTreeUtil.findChildrenOfType(root, PsiImportStatement::class.java).any
            {
                LAMBDA_MODEL_FQN == it.qualifiedName
            }
        ) {
            root.accept(object : JavaRecursiveElementWalkingVisitor() {

                override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                    if ("$LAMBDA_MODEL_NAME.of" == expression.methodExpression.qualifiedName) {
                        addLambdaModelFoldDescriptor(expression)
                    }
                    super.visitMethodCallExpression(expression)
                }

                private fun addLambdaModelFoldDescriptor(expression: PsiMethodCallExpression) {
                    val args = expression.argumentList.expressions
                    if (args.size != FOLDING_PARAMS_COUNT) {
                        return
                    }
                    val modelDef = args[0]
                    val getterDef = args[1]
                    val setterDef = args[2]
                    val getterStr = getterDef.text
                    val setterStr = setterDef.text
                    val modelDefType = modelDef.type
                    val hasGetterAndSetter = getterStr.contains(GET_PREFIX) &&
                        getterStr.replace(GET_PREFIX, "/") == setterStr.replace(SET_PREFIX, "/") ||
                        getterStr.contains(IS_PREFIX) &&
                        getterStr.replace(IS_PREFIX, "/") == setterStr.replace(SET_PREFIX, "/")

                    // First param is assignable to IModel and following two looks like getter and setter
                    if (modelDefType == null ||
                        !PsiType.getTypeByName(
                                "org.apache.wicket.model.IModel",
                                root.project,
                                root.resolveScope
                            ).isAssignableFrom(modelDefType) || !hasGetterAndSetter
                    ) {
                        return
                    }

                    descriptors.add(
                        FoldGetSetDescriptor(
                            expression,
                            TextRange(getterDef.textRange.startOffset, setterDef.textRange.endOffset),
                            getterStr
                        )
                    )
                }
            })
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(astNode: ASTNode): String {
        return "..."
    }

    override fun isCollapsedByDefault(astNode: ASTNode): Boolean {
        return true
    }

    internal class FoldGetSetDescriptor(
        expression: PsiMethodCallExpression,
        range: TextRange,
        private val getterStr: String
    ) : FoldingDescriptor(expression.node, range) {
        val prefix: String = if (getterStr.contains(IS_PREFIX)) IS_PREFIX else GET_PREFIX

        // Will show for >1 liners
        override fun getPlaceholderText(): String {
            return getterStr.replace(prefix, prefix + SET_SUFFIX)
        }
    }

    companion object {
        private const val GET_PREFIX = "::get"
        private const val IS_PREFIX = "::is"
        private const val SET_PREFIX = "::set"
        private const val SET_SUFFIX = "/set"
    }
}
