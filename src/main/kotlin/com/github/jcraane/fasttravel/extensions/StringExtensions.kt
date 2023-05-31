package com.github.jcraane.fasttravel.extensions

/**
 * Returns the indices of all occurrences of string in this String or empty list if no occurances are found.,
 */
fun String.allIndicesOf(string: String): List<Int> {
    if (string.isEmpty()) return emptyList()

    var index = this.indexOf(string)
    val indices = mutableListOf<Int>()
    while (index != -1) {
        indices += index
        index = this.indexOf(string, startIndex = index + 1)
    }

    return indices
}
