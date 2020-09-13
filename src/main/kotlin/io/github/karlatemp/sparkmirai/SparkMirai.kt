package io.github.karlatemp.sparkmirai

import com.google.auto.service.AutoService
import me.lucko.spark.common.SparkPlatform
import me.lucko.spark.common.SparkPlugin
import me.lucko.spark.common.platform.PlatformInfo
import net.mamoe.mirai.console.command.AbstractCommand
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.message.data.MessageChain
import java.lang.ref.WeakReference
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream

@AutoService(JvmPlugin::class)
object SparkMirai : KotlinPlugin(
    JvmPluginDescriptionBuilder("spark", "1.4.3")
        .id("spark.spark.spark").build()
), SparkPlugin {
    private lateinit var platform: SparkPlatform

    override fun onEnable() {
        platform = SparkPlatform(this)
        platform.enable()
        object : AbstractCommand(
            owner = this,
            names = arrayOf("spark")
        ) {
            override val usage: String
                get() = "/spark"

            override suspend fun CommandSender.onCommand(args: MessageChain) {
                val aArgs = args.flatMap {
                    val cont = it.contentToString()
                    if (cont.isBlank()) emptyList() else cont.split(' ')
                }.toTypedArray()
                val cmd = MiraiCommandSender(this)
                if (this@onCommand is UserCommandSender) {
                    cmd.cachedMessages = ArrayList(16)
                    AutoFlush.flushQueue.add(WeakReference(cmd))
                }
                platform.executeCommand(cmd, aArgs)
                cmd.release()
            }

//            override val permission: Permission by lazy {
//                PermissionService.INSTANCE[PermissionId("", "")]!!
//            }
        }.register(true)
    }

    override fun onDisable() {
        services.shutdown()
        platform.disable()
    }

    override fun getCommandName(): String = "spark"

    override fun getSendersWithPermission(permission: String?):
            Stream<out me.lucko.spark.common.command.sender.CommandSender> {
        return Stream.of(MiraiCommandSender(ConsoleCommandSender))
    }

    private val services = Executors.newScheduledThreadPool(5, object : ThreadFactory {
        val counter = AtomicInteger()
        override fun newThread(r: Runnable): Thread {
            return Thread(r, "Spark Service #" + counter.getAndIncrement())
        }
    })

    override fun executeAsync(task: Runnable) {
        services.execute(task)
    }

    override fun getPluginDirectory(): Path = dataFolderPath

    override fun getVersion(): String {
        return description.version.toString()
    }

    override fun getPlatformInfo(): PlatformInfo = MiraiPlatformInfo
}
