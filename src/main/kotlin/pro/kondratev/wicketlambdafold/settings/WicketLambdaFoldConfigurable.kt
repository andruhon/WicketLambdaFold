package pro.kondratev.wicketlambdafold.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.UI
import com.intellij.util.ui.UI.PanelFactory.panel
import pro.kondratev.wicketlambdafold.WicketLambdaFoldBundle
import javax.swing.JComponent

/**
 * TODO add configuration if necessary
 * Can be enabled in plugin.xml
 */
class WicketLambdaFoldConfigurable : com.intellij.openapi.options.Configurable {

    override fun createComponent(): JComponent {
        return UI.PanelFactory.grid()
            .add(
                panel(ComboBox(SupportedWicketVersions.values()))
                    .withLabel(WicketLambdaFoldBundle.message("wicket.supported.version"))
            )
            .createPanel()
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): String {
        return WicketLambdaFoldBundle.message("name")
    }
}
