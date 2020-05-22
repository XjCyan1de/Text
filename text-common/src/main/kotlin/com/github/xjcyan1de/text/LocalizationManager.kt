package com.github.xjcyan1de.text

import java.io.Closeable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object LocalizationManager : Closeable {
    private val dictionary = HashMap<Locale, HashMap<String, String>>()
    private val dictionaryProviders = HashMap<Locale, MutableList<DictionaryProvider>>()
    private val localeProviders = HashMap<Class<*>, LocaleProvider<*>>()

    fun translate(string: String, locale: Locale) =
            dictionary.getOrPut(locale) { HashMap() }[string] ?: dictionaryProviders[locale]?.let {
                var result: String? = null
                for (provider in it) {
                    val value = provider[string]
                    if (value != null) {
                        result = value
                        break
                    }
                }
                result
            }

    fun addDictionary(locale: Locale, map: Map<String, String>) =
            dictionary.getOrPut(locale) { HashMap() }.putAll(map)

    fun registerDictionary(locale: Locale, provider: DictionaryProvider) =
            dictionaryProviders.getOrPut(locale) { ArrayList() }.add(provider)

    fun registerDictionary(locale: Locale, provider: (String) -> String?) =
            registerDictionary(locale, object : DictionaryProvider {
                override fun get(key: String): String? = provider(key)
            })

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