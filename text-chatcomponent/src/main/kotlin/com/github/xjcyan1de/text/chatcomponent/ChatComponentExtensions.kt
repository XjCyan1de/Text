package com.github.xjcyan1de.text.chatcomponent

import java.util.*

fun String.toItemStackString(): Pair<String, List<String>> {
    val text = this.split("(::|\n|\r)".toRegex()).toTypedArray()
    return if (text.isNotEmpty()) {
        val displayName = if (text[0].startsWith("§")) text[0] else "§f" + text[0]
        val lore: List<String> = if (text.size <= 1) {
            emptyList()
        } else {
            LinkedList<String>().apply {
                for (i in 1 until text.size) {
                    add(if (text[i].startsWith("§")) text[i] else "§7" + text[i])
                }
            }
        }
        displayName to lore
    } else {
        "" to emptyList()
    }
}

