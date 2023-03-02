package io.github.bluishelvis.widgetlengthcounter

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory

class LCWidgetFactory : StatusBarEditorBasedWidgetFactory() {
  /**
   * @return Widget identifier. Used to store visibility settings.
   */
  override fun getId(): String = ID

  /**
   * @return Widget's display name. Used to refer a widget in UI,
   * e.g. for "Enable/disable &lt;display name>" action names
   * or for checkbox texts in settings.
   */
  override fun getDisplayName(): String = "File Lines:Length count"

  override fun createWidget(project: Project): StatusBarWidget = Main(project)

  override fun disposeWidget(widget: StatusBarWidget) {
    Disposer.dispose(widget)
  }

  companion object {
    const val ID = "bluish-elvis.widget-length-counter"
  }
}