@file:JvmName("ChatComponentUtils")

package com.github.xjcyan1de.text.chatcomponent

import com.github.xjcyan1de.text.Text
import com.github.xjcyan1de.text.extensions.textOf
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import java.net.URL
import java.util.*

const val SECTION_CHAR = '\u00A7' // §
const val AMPERSAND_CHAR = '&'

operator fun TextComponent.invoke(block: ChatComponent.() -> Unit): TextComponent =
        ChatComponent(this, block)

fun String.toTextComponent(): TextComponent =
        if (contains("§")) TextComponent(*TextComponent.fromLegacyText(this)) else TextComponent(this)

fun String.colorize(): String = translateAlternateColorCodes(AMPERSAND_CHAR, SECTION_CHAR)
fun String.decolorize(): String = translateAlternateColorCodes(SECTION_CHAR, AMPERSAND_CHAR)

private fun String.translateAlternateColorCodes(from: Char, to: Char): String {
    val b = toCharArray()
    for (i in 0 until b.size - 1) {
        if (b[i] == from && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
            b[i] = to
            b[i + 1] = Character.toLowerCase(b[i + 1])
        }
    }
    return String(b)
}

@JvmOverloads
fun Text.toChatComponent(locale: Locale = Locale.getDefault(), urlHoverText: Text = textOf("chat.open_url")): BaseComponent =
        ChatComponent {
            var lastColorComponent: BaseComponent? = null
            Text.translate(content, locale).toComponents(replaces)
                    .forEach {
                        val baseComponent = when (it) {
                            is Text -> chatComponent(it.toChatComponent(locale))
                            is ChatComponent -> chatComponent(it)
                            is BaseComponent -> chatComponent(it)
                            is URL -> chatComponent(it.toString().let { url ->
                                when {
                                    url.startsWith("https://") -> url.substring(8)
                                    url.startsWith("http://") -> url.substring(7)
                                    else -> url
                                }
                            }) {
                                clickEvent(ClickEvent.Action.OPEN_URL, it.toString())
                                hoverEvent(urlHoverText[locale])
                            }
                            else -> chatComponent(it.toString())
                        }.apply {
                            val color = lastColorComponent
                            if (color != null) {
                                this.color = color.color
                                this.isBold = color.isBold
                                this.isItalic = color.isItalic
                                this.isObfuscated = color.isObfuscated
                                this.isStrikethrough = color.isStrikethrough
                                this.isUnderlined = color.isUnderlined
                            }
                        }
                        lastColorComponent = baseComponent.extra?.lastOrNull()
                    }
        }