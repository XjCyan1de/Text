package com.github.xjcyan1de.text

import java.io.Closeable
import java.util.*
import kotlin.collections.HashMap

object LocalizationManager : Closeable {
    private val dictionary: HashMap<Locale, HashMap<String, String>> = HashMap()
    private val localeProviders: HashMap<Class<*>, LocaleProvider<*>> = HashMap()

    fun translate(string: String, locale: Locale) =
            dictionary.getOrPut(locale) { HashMap() }[string]

    fun addDictionary(locale: Locale, map: Map<String, String>) =
            dictionary.getOrPut(locale) { HashMap() }.putAll(map)

    @Suppress("UNCHECKED_CAST")
    fun <T> getLocale(clazz: Class<T>, value: T): Locale? = (localeProviders[clazz] as? LocaleProvider<T>)?.get(value)

    @Suppress("UNCHECKED_CAST")
    fun getLocale(value: Any): Locale? = (localeProviders[value::class.java] as? LocaleProvider<Any>)?.get(value)

    @Suppress("UNCHECKED_CAST")
    fun <T> getLocaleProvider(clazz: Class<T>): LocaleProvider<T> = localeProviders[clazz] as LocaleProvider<T>

    fun <T> registerLocaleProvider(clazz: Class<T>, localeProvider: LocaleProvider<T>) {
        localeProviders[clazz] = localeProvider
    }

    inline fun <reified T> registerLocaleProvider(localeProvider: LocaleProvider<T>) =
            registerLocaleProvider(T::class.java, localeProvider)

    @Suppress("UNCHECKED_CAST")
    fun <T> unregisterLocaleProvider(clazz: Class<T>): LocaleProvider<T>? =
            localeProviders.remove(clazz) as LocaleProvider<T>?

    inline fun <reified T> unregisterLocaleProvider(): LocaleProvider<T>? =
            unregisterLocaleProvider(T::class.java)

    override fun close() {
        dictionary.clear()
        localeProviders.clear()
    }
}