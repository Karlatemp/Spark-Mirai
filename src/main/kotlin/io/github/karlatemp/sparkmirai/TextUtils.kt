package io.github.karlatemp.sparkmirai

import net.kyori.text.Component
import net.kyori.text.TextComponent
import net.kyori.text.TranslatableComponent

fun Component.toPlain(): String = StringBuilder().also {
    toPlain(this@toPlain, it)
}.toString()

private fun toPlain(c: Component, builder: StringBuilder) {
    when (c) {
        is TextComponent -> {
            builder.append(c.content())
        }
        is TranslatableComponent -> {
            builder.append(c.key())
        }
    }
    c.children().forEach { toPlain(it, builder) }
}
fun a(){
    Int.MAX_VALUE
    Long.MAX_VALUE
}