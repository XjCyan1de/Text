package com.github.xjcyan1de.text

import java.util.*

interface LocaleProvider<T> {
    operator fun get(value: T): Locale
}