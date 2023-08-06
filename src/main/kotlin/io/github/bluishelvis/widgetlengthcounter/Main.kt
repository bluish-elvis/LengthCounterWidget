package io.github.bluishelvis.widgetlengthcounter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.openapi.wm.impl.status.PositionPanel.DISABLE_FOR_EDITOR
import com.intellij.util.Alarm
import com.intellij.util.Consumer
import com.intellij.util.ui.update.MergingUpdateQueue
import com.intellij.util.ui.update.Update
import java.awt.Component
import java.awt.KeyboardFocusManager
import java.awt.event.MouseEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class Main(project:Project):EditorBasedWidget(project), StatusBarWidget.Multiframe, StatusBarWidget.TextPresentation,
	BulkAwareDocumentListener.Simple, PropertyChangeListener {

	private var myAlarm:Alarm? = null
	private var myQueue:MergingUpdateQueue? = null
//    private var myCountTask: CodePointCountTask? = null

	private var myText:@NlsContexts.Label String? = null
	private var myTip:@NlsContexts.Label String? = null

	override fun ID():String = LCWidgetFactory.ID
	override fun copy():StatusBarWidget = Main(project)
	override fun getPresentation():StatusBarWidget.WidgetPresentation = this

	override fun getText():String = myText ?: ""
	override fun getTooltipText():String? = myTip

	override fun getAlignment():Float = Component.CENTER_ALIGNMENT

	override fun getClickConsumer():Consumer<MouseEvent>? = null

	private fun getText(editor:Editor):@NlsContexts.Label String =
		if(!editor.isDisposed&&!myAlarm!!.isDisposed) {
			//${lines}Lines ${chars}chars
			//${lines}L ${chars}c
			val lines = editor.document.lineCount
			val chars = editor.document.textLength
			"${lines}L ${Companion.SEPARATOR} ${chars}c"
		} else ""

	private fun updatePosition(editor:Editor?) {
		myQueue!!.queue(Update.create(this) {
			val empty = editor==null||DISABLE_FOR_EDITOR.isIn(editor)
			if(!empty&&!isOurEditor(editor)) return@create
			val newText = if(empty) "" else getText(editor!!)
			if(newText==myText) return@create
			myText = newText
			if(myStatusBar!=null) {
				myStatusBar.updateWidget(ID())
			}
		})
	}

	private val nowComponent:Component?
		get() = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
			?: IdeFocusManager.getInstance(myProject).let {fMan ->
				fMan.lastFocusedIdeWindow?.let {fMan.getLastFocusedFor(it)}
			}

	private val nowEditor:Editor?
		get() = nowComponent.let {component ->
			(if(component is EditorComponentImpl) component.editor else editor).let {
				if(it!=null&&!it.isDisposed) it else null
			}
		}

	override fun install(statusBar:StatusBar) {
		super.install(statusBar)
		myAlarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
		myQueue = MergingUpdateQueue(LCWidgetFactory.ID, 100, true, null, this)
		val multiCaster = EditorFactory.getInstance().eventMulticaster
		multiCaster.addDocumentListener(this, this)
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(SWING_FOCUS_OWNER_PROPERTY, this)
		Disposer.register(
			this
		) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removePropertyChangeListener(SWING_FOCUS_OWNER_PROPERTY, this)
		}
	}

	override fun afterDocumentChange(document:Document) {
		EditorFactory.getInstance().editors(document)
			.filter {nowComponent==it.contentComponent}
			.findFirst()
			.ifPresent(this::updatePosition)
	}

	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source
	 * and the property that has changed.
	 */
	override fun propertyChange(evt:PropertyChangeEvent?) {
		updatePosition(nowEditor)
	}

	companion object {
		private const val SEPARATOR = ":"
	}
}
