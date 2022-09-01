package com.github.jcraane.fasttravel.actions.identifier

/*
* Generator which just uses a-z,A-Z and 0-9 as identifiers for the interesting content found in text. Using one-char has the limitation
* of only 62 identifiers.
 */
class OneCharIdentifierGenerator : IdentifierGenerator {
    private var index = 0

    override fun hasNext(): Boolean = index < identifiers.size

    override fun next(): String {
        val identifier = identifiers[index]
        index++
        return identifier
    }

    companion object {
        private val numbers = (0..9).toList().map { it.toString() }
        private val upperCase = ('A'..'Z').toList().map { it.toString() }
        private val lowerCase = ('a'..'z').toList().map { it.toString() }
        val identifiers = lowerCase + upperCase + numbers
    }
}
