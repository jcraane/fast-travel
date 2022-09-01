package com.github.jcraane.fasttravel.actions.identifier

class DoubleCharIdentifierGenerator(
    private val initialMajor: Char = start,
    private val initialMinor: Char = start,
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
