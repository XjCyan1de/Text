package com.github.xjcyan1de.text.extensions

import com.github.xjcyan1de.text.LocalizationManager
import com.github.xjcyan1de.text.Text
import java.util.*

fun textOf(content: String, vararg args: Pair<String, Any?>) = Text(content, *args)

fun localeOf(value: Any) = LocalizationManager.getLocale(value) ?: Locale.getDefault()