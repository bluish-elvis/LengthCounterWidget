package io.github.bluishelvis.widgetlengthcounter

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class LCWidgetFactory:StatusBarWidgetFactory {
	/**
	 * @return Widget identifier. Used to store visibility settings.
	 */
	override fun getId():String = ID

	/**
	 * @return Widget's display name. Used to refer a widget in UI,
	 * e.g. for "Enable/disable &lt;display name>" action names
	 * or for checkbox texts in settings.
	 */
	override fun getDisplayName():String = "File Lines:Length count"
	/**
	 * Returns availability of widget.
	 *
	 *
	 * `False` means that IDE won't try to create a widget or will dispose it on [com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager.updateWidget] call.
	 *
	 *
	 * E.g. `false` can be returned for
	 *
	 *  * notifications widget if Event log is shown as a tool window
	 *  * memory indicator widget if it is disabled in the appearance settings
	 *  * git widget if there are no git repos in a project
	 *
	 *
	 *
	 * Whenever availability is changed, you need to call [com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager.updateWidget]
	 * explicitly to get status bar updated.
	 */
	override fun isAvailable(project:Project):Boolean = true

	override fun createWidget(project:Project):StatusBarWidget = Main(project)

	override fun disposeWidget(widget:StatusBarWidget) {
		Disposer.dispose(widget)
	}
	/**
	 * @return Returns whether the widget can be enabled on the given status bar right now.
	 * Status bar's context menu with enable/disable action depends on the result of this method.
	 *
	 *
	 * It's better to have this method aligned with [com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup.WidgetState.HIDDEN],
	 * whenever state is `HIDDEN`, this method should return `false`.
	 * Otherwise, enabling widget via context menu will not have any visual effect.
	 *
	 *
	 * E.g. [com.intellij.openapi.wm.impl.status.EditorBasedWidget] are available if editor is opened in a frame that given status bar is attached to
	 *
	 *
	 * For creating editor based widgets see also [com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory]
	 */
	override fun canBeEnabledOn(statusBar:StatusBar):Boolean = true

	companion object {
		const val ID = "bluish-elvis.widget-length-counter"
	}
}
