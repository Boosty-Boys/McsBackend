package com.boostyboys.mcs.di

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.singleton

/**
 * Shortcut for binding singletons to the same type.
 */
inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}

inline fun <reified T : Any> DI.MainBuilder.bindFactory(crossinline callback: (DI) -> T) {
    bind<T>() with factory { callback(this@factory.di) }
}
