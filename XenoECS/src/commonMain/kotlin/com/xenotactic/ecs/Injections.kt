package com.xenotactic.ecs

typealias Injections = TypedInjections<Any>

//class Injections {
//    val singletonComponents = mutableMapOf<KClass<*>, Any>()
//
//    /**
//     * Sets singleton or throws a SingletonInjectionAlreadyExistsException if it already exists.
//     */
//    fun <T: Any> setSingletonOrThrow(obj: T) {
//        val klazz = obj::class
//        if (singletonComponents.containsKey(klazz)) {
//            throw SingletonInjectionAlreadyExistsException {
//                "Singleton injection already exists: ${klazz}"
//            }
//        }
//        singletonComponents[klazz] = obj
//    }
//
//    inline fun <reified T: Any> getSingleton(): T {
//        return getSingletonOrNull<T>() ?:
//            throw SingletonInjectionDoesNotExistException {
//                "Singleton injection does not exist: ${T::class}"
//            }
//    }
//
//    inline fun <reified T: Any> getSingletonOrNull(): T? {
//        return singletonComponents[T::class] as T?
//    }
//}