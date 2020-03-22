package com.github.xjcyan1de.text.chatcomponent

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

@ChatComponentMarker
class ChatComponent(val textComponent: TextComponent) {
    var color: ChatColor
        get() = textComponent.color
        set(value) {
            textComponent.color = value
        }
    var bold: Boolean
        get() = textComponent.isBold
        set(value) {
            textComponent.isBold = value
        }
    var italic: Boolean
        get() = textComponent.isItalic
        set(value) {
            textComponent.isItalic = value
        }
    var underlined: Boolean
        get() = textComponent.isUnderlined
        set(value) {
            textComponent.isUnderlined = value
        }
    var strikethrough: Boolean
        get() = textComponent.isStrikethrough
        set(value) {
            textComponent.isStrikethrough = value
        }
    var obfuscated: Boolean
        get() = textComponent.isObfuscated
        set(value) {
            textComponent.isObfuscated = value
        }

    @JvmOverloads
    fun chatComponent(text: String, block: ChatComponent.() -> Unit = {}): TextComponent {
        val component = text.toTextComponent()
        block(ChatComponent(component))
        return chatComponent(component)
    }

    fun chatComponent(chatComponent: ChatComponent): TextComponent =
            chatComponent(chatComponent.textComponent)

    fun <T : BaseComponent> chatComponent(baseComponent: T): T {
        textComponent.addExtra(baseComponent)
        return baseComponent
    }

    fun clickEvent(action: ClickEvent.Action, value: String) {
        textComponent.clickEvent = ClickEvent(action, value)
    }

    @JvmOverloads
    fun hoverEvent(
            text: String = "",
            block: ChatComponent.() -> Unit = {}
    ) = hoverEvent(HoverEvent.Action.SHOW_TEXT, text, block)

    @JvmOverloads
    fun hoverEvent(
            action: HoverEvent.Action,
            text: String = "",
            block: ChatComponent.() -> Unit = {}
    ) {
        textComponent.hoverEvent = HoverEvent(action, arrayOf(ChatComponent(text.toTextComponent(), block)))
    }

    operator fun String.unaryPlus() {
        textComponent.addExtra(this)
    }

    operator fun BaseComponent.unaryPlus() {
        textComponent.addExtra(this)
    }

    operator fun unaryPlus() {
        textComponent.addExtra(this.textComponent)
    }

    operator fun invoke(block: ChatComponent.() -> Unit): ChatComponent = apply(block)

    companion object {
        operator fun invoke(textComponent: TextComponent, block: ChatComponent.() -> Unit = {}) =
                ChatComponent(textComponent).apply(block).textComponent

        operator fun invoke(text: String = "", block: ChatComponent.() -> Unit = {}) =
                ChatComponent(text.toTextComponent()).apply(block).textComponent

        @JvmStatic
        fun of(string: String) = ChatComponent(string.toTextComponent())

        @JvmStatic
        fun toTextComponent(string: String) = string.toTextComponent()
    }
}