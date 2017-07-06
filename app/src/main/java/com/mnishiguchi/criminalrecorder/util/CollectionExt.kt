package com.mnishiguchi.criminalrecorder.util

/**
 * Converts a map with a nullable values (a requirement of the map delegate)
 * to an array of pairs with non-nullable values.
 */
fun <K, V : Any> Map<K, V?>.toVarargArray(): Array<out Pair<K, V>> {
    return map({ Pair(it.key, it.value!!) }).toTypedArray()
}

/**
 * The function receives a predicate which gets an object of type T and returns a value of type R?.
 * The predicate can return null but our firstResult cannot.
 */
inline fun <T, R : Any> Iterable<T>.firstResult(predicate: (T) -> R?): R {

    for (element in this) {
        val result = predicate(element)
        if (result != null) return result
    }

    throw NoSuchElementException("No element matching predicate was found.")
}