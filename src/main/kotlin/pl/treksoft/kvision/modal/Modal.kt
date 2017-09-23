package pl.treksoft.kvision.modal

import com.github.snabbdom.VNode
import org.w3c.dom.CustomEvent
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.Root
import pl.treksoft.kvision.core.Widget
import pl.treksoft.kvision.helpers.CloseIcon
import pl.treksoft.kvision.html.Button
import pl.treksoft.kvision.html.TAG
import pl.treksoft.kvision.html.Tag
import pl.treksoft.kvision.snabbdom.StringBoolPair
import pl.treksoft.kvision.snabbdom.StringPair
import pl.treksoft.kvision.snabbdom.obj

enum class MODALSIZE(val className: String) {
    LARGE("modal-lg"),
    SMALL("modal-sm")
}

@Suppress("TooManyFunctions")
open class Modal(caption: String? = null, closeButton: Boolean = true,
                 size: MODALSIZE? = null, animation: Boolean = true, val escape: Boolean = true,
                 classes: Set<String> = setOf()) : Container(classes) {
    var caption
        get() = captionTag.text
        set(value) {
            captionTag.text = value
            checkHeaderVisibility()
        }
    var closeButton
        get() = closeIcon.visible
        set(value) {
            closeIcon.visible = value
            checkHeaderVisibility()
        }
    var size
        get() = dialog.size
        set(value) {
            dialog.size = value
        }
    var animation = animation
        set(value) {
            field = value
            refresh()
        }

    protected val dialog = ModalDialog(size)
    protected val header = Container(setOf("modal-header"))
    protected val closeIcon = CloseIcon()
    protected val captionTag = Tag(TAG.H4, caption, classes = setOf("modal-title"))
    protected val body = Container(setOf("modal-body"))
    protected val footer = Container(setOf("modal-footer"))

    init {
        this.hide()
        this.role = "dialog"
        this.addInternal(dialog)
        val content = Container(setOf("modal-content"))
        dialog.role = "document"
        dialog.add(content)
        closeIcon.visible = closeButton
        closeIcon.setEventListener {
            click = {
                hide()
            }
        }
        header.add(closeIcon)
        header.add(captionTag)
        checkHeaderVisibility()
        content.add(header)
        content.add(body)
        content.add(footer)
        val root = Root.getLastRoot()
        if (root != null) {
            root.addModal(this)
        } else {
            println("At least one Root object is required to create a modal!")
        }
    }

    private fun checkHeaderVisibility() {
        if (!closeButton && caption == null) {
            header.hide()
        } else {
            header.show()
        }
    }

    override fun add(child: Widget): Container {
        body.add(child)
        return this
    }

    override fun addAll(children: List<Widget>): Container {
        body.addAll(children)
        return this
    }

    open fun addButton(button: Button): Modal {
        footer.add(button)
        return this
    }

    open fun removeButton(button: Button): Modal {
        footer.remove(button)
        return this
    }

    open fun removeButtonAt(index: Int): Modal {
        footer.removeAt(index)
        return this
    }

    open fun removeAllButtons(): Modal {
        footer.removeAll()
        return this
    }

    override fun getSnAttrs(): List<StringPair> {
        val pr = super.getSnAttrs().toMutableList()
        pr.add("tabindex" to "-1")
        return pr
    }

    override fun getSnClass(): List<StringBoolPair> {
        val cl = super.getSnClass().toMutableList()
        cl.add("modal" to true)
        if (animation) {
            cl.add("fade" to true)
        }
        return cl
    }

    @Suppress("UnsafeCastFromDynamic")
    override fun afterInsert(node: VNode) {
        getElementJQueryD()?.modal(obj {
            keyboard = escape
            backdrop = if (escape) "true" else "static"
        })
        this.getElementJQuery()?.on("show.bs.modal", { e, _ ->
            val event = CustomEvent("showBsModal", obj({ detail = e }))
            this.getElement()?.dispatchEvent(event)
        })
        this.getElementJQuery()?.on("shown.bs.modal", { e, _ ->
            val event = CustomEvent("shownBsModal", obj({ detail = e }))
            this.getElement()?.dispatchEvent(event)
        })
        this.getElementJQuery()?.on("hide.bs.modal", { e, _ ->
            val event = CustomEvent("hideBsModal", obj({ detail = e }))
            this.getElement()?.dispatchEvent(event)
        })
        this.getElementJQuery()?.on("hidden.bs.modal", { e, _ ->
            this.visible = false
            val event = CustomEvent("hiddenBsModal", obj({ detail = e }))
            this.getElement()?.dispatchEvent(event)
        })
    }

    override fun hide(): Widget {
        if (visible) hideInternal()
        return super.hide()
    }

    open fun toggle() {
        if (visible)
            hide()
        else
            show()
    }

    @Suppress("UnsafeCastFromDynamic")
    private fun showInternal() {
        getElementJQueryD()?.modal("show")
    }

    @Suppress("UnsafeCastFromDynamic")
    private fun hideInternal() {
        getElementJQueryD()?.modal("hide")
    }
}

open class ModalDialog(size: MODALSIZE?) : Container(setOf("modal-dialog")) {
    var size = size
        set(value) {
            field = value
            refresh()
        }

    override fun getSnClass(): List<StringBoolPair> {
        val cl = super.getSnClass().toMutableList()
        if (size != null) {
            cl.add(size?.className.orEmpty() to true)
        }
        return cl
    }
}
