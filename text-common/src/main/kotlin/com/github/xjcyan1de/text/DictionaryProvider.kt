package com.github.xjcyan1de.text

interface DictionaryProvider {
    operator fun get(key: String): String?
}