package com.github.xjcyan1de.text

import com.github.xjcyan1de.text.extensions.localeOf
import java.net.URL
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Supplier
import kotlin.collections.HashMap

class Text(val content: String, vararg args: Pair<String, Any?>) {
    val replaces: List<Pair<String, () -> Any>> = args.map {
        it.first to {
            when (val value = it.second) {
                is Function0<*> -> value.invoke() ?: null.toString()
                is Callable<*> -> value.call() ?: null.toString()
                is Supplier<*> -> value.get() ?: null.toString()
                null -> null.toString()
                else -> value
            }
        }
    }

    operator fun get(any: Any) = get(localeOf(any))

    operator fun get(locale: Locale = Locale.getDefault()): String =
            translate(content, locale).replaceString(locale, replaces)

    override fun toString(): String = get()

    private fun String.replaceString(locale: Locale, replaces: Iterable<Pair<String, () -> Any>>): String {
        var str = this
        for (pair in replaces) {
            val value = pair.second.invoke()
            str = when (value) {
                is Text -> str.getPlaceholder(pair.first).replacePlaceholder(pair.first, value[locale])
                is URL -> str.getPlaceholder(pair.first).replacePlaceholder(pair.first, value.path)
                else -> str.getPlaceholder(pair.first).replacePlaceholder(pair.first, value.toString())
            }
        }
        return str
    }

    private fun String.getPlaceholder(oldValue: String) = this
            .replace("%$oldValue%", "{$oldValue}")
            .replace("%$oldValue", "{$oldValue}")
            .replace("\$$oldValue", "{$oldValue}")
            .replace("\$$oldValue\$", "{$oldValue}")

    private fun String.replacePlaceholder(oldValue: String, newValue: String) = this.replace("{$oldValue}", newValue)

    fun String.toComponents(
            replaces: Iterable<Pair<String, () -> Any>>
    ): List<Any> {
        var components: List<Pair<() -> Any, String?>> = listOf({ this@toComponents } to this)
        for (pair in replaces) {
            val newComponents = LinkedList<Pair<() -> Any, String?>>()
            for (component in components) {
                val stringValue = component.second
                if (stringValue != null) {
                    val split = stringValue.getPlaceholder(pair.first).splitPlaceholders(pair)
                    if (split != null) {
                        newComponents.addAll(split)
                    } else {
                        newComponents.add(component)
                    }
                } else {
                    newComponents.add(component)
                }
            }
            components = newComponents
        }
        val lambdaResults = HashMap<() -> Any, Any>()
        components.forEach {
            if (!lambdaResults.containsKey(it.first)) {
                if (it.second != null) {
                    lambdaResults[it.first] = it.second!!
                } else {
                    lambdaResults[it.first] = it.first()
                }
            }
        }
        return components.map {
            lambdaResults.getOrElse(it.first, it.first)
        }
    }

    private fun String.splitPlaceholders(replace: Pair<String, () -> Any>): List<Pair<() -> Any, String?>>? {
        val split = split("{${replace.first}}")
        return if (split.size <= 1) {
            null
        } else {
            val result = LinkedList<Pair<() -> Any, String?>>()
            split.forEachIndexed { index, s ->
                if (s.isNotEmpty()) {
                    result.add({ s } to s)
                }
                if (index != split.indices.last) {
                    result.add(replace.second to null)
                }
            }
            result
        }
    }

    companion object {
        fun translate(string: String, locale: Locale = Locale.getDefault()): String {
            val stringBuilder = StringBuilder()
            string.split(" ").forEach { word ->
                stringBuilder.append(
                        LocalizationManager.translate(word, locale) ?: word
                )
                stringBuilder.append(" ")
            }
            stringBuilder.setLength(stringBuilder.length - 1)
            return stringBuilder.toString()
        }
    }
}