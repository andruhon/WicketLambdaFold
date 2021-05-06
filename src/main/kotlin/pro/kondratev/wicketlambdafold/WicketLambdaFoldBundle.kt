package pro.kondratev.wicketlambdafold

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
const val BUNDLE = "messages.WicketLambdaFoldBundle"

const val LAMBDA_MODEL_FQN = "org.apache.wicket.model.LambdaModel"

const val IMODEL_INTERFACE_FQN = "org.apache.wicket.model.IModel"

const val LAMBDA_MODEL_NAME = "LambdaModel"

const val PROPERTY_MODEL_FQN = "org.apache.wicket.model.PropertyModel"

object WicketLambdaFoldBundle : AbstractBundle(BUNDLE) {

    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("SpreadOperator")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)
}
