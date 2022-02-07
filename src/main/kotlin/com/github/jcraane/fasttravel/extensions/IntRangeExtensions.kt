package com.github.jcraane.fasttravel.extensions

fun IntRange.within(other: IntRange): Boolean {
    return (this.first >= other.first
            && this.last <= other.last
            )
}
