package com.github.jcraane.fasttravel.actions.identifier

/**
 * Generator which uses double char identifiers starting and aa and increasing to zz. Identifiers are consecutive: aa, ab, ac, etc.
 * When az is reached, the next identifier is ba.
 */
class DoubleCharIdentifierGenerator(
    initialMajor: Char = start,
    initialMinor: Char = start,
) : IdentifierGenerator {
    // Start identifier = aa
    private var currentMajor = initialMajor
    private var currentMinor = initialMinor

    override fun hasNext(): Boolean = "$currentMajor$currentMinor" != end

    override fun next(): String {
        val identifier = "$currentMajor$currentMinor"
        if (currentMinor != 'z') {
            currentMinor++
        } else {
            currentMinor = start
            currentMajor++
        }
        return identifier
    }

    companion object {
        private const val start = 'a'
        private const val endChar = 'z'
        private const val end = "$endChar$endChar"
    }
}
