package pro.kondratev.wicketlambdafold.settings

import org.jetbrains.annotations.PropertyKey
import pro.kondratev.wicketlambdafold.BUNDLE
import pro.kondratev.wicketlambdafold.WicketLambdaFoldBundle

enum class SupportedWicketVersions(@PropertyKey(resourceBundle = BUNDLE) private val key: String) {

    WICKET_7("wicket.version.7"),
    WICKET_8("wicket.version.8"),
    WICKET_9("wicket.version.9");

    override fun toString(): String = WicketLambdaFoldBundle.message(key)
}
