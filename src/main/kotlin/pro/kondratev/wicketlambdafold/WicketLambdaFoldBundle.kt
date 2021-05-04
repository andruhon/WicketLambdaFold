package pro.kondratev.wicketlambdafold

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
public const val BUNDLE = "messages.WicketLambdaFoldBundle"

public const val LAMBDA_MODEL_FQN = "org.apache.wicket.model.LambdaModel"

public const val LAMBDA_MODEL_NAME = "LambdaModel"

public const val PROPERTY_MODEL_FQN = "org.apache.wicket.model.PropertyModel";

public const val PROPERTY_MODEL_NAME = "org.apache.wicket.model.PropertyModel";

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
