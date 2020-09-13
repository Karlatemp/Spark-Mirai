@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.github.karlatemp.sparkmirai

import kotlinx.coroutines.runBlocking
import net.kyori.text.Component
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function

class MiraiCommandSender(
    private val delegate: CommandSender
) : me.lucko.spark.common.command.sender.CommandSender {
    override fun getName(): String = delegate.name

    override fun getUniqueId(): UUID? = null

    var cachedMessages: ArrayList<String>? = null
    private val lock = AtomicBoolean()
    private fun lock() {
        @Suppress("ControlFlowWithEmptyBody")
        while (!lock.compareAndSet(false, true));
    }

    override fun sendMessage(message: Component) {
        runBlocking {
            lock()
            val cache = cachedMessages
            if (cache == null) {
                lock.set(false)
                delegate.sendMessage(message.toPlain())
            } else {
                cache.add(message.toPlain())
                lock.set(false)
            }
        }
    }

    fun release() {
        lock()
        val cache = cachedMessages
        if (cache != null && cache.isNotEmpty()) {
            val msg = cache.joinToString("\n")
            cache.clear()
            lock.set(false)
            runBlocking { delegate.sendMessage(msg) }
        } else {
            lock.set(false)
        }
    }

    override fun hasPermission(permission: String?): Boolean {
        return true // Only permission "spark"
    }
}