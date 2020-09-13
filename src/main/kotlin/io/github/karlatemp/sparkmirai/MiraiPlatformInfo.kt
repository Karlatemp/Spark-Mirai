package io.github.karlatemp.sparkmirai

import me.lucko.spark.common.platform.AbstractPlatformInfo
import me.lucko.spark.common.platform.PlatformInfo
import net.mamoe.mirai.console.MiraiConsole

object MiraiPlatformInfo : AbstractPlatformInfo() {
    override fun getType(): PlatformInfo.Type {
        return PlatformInfo.Type.PROXY
    }

    override fun getName(): String = "Mirai-Console"

    override fun getVersion(): String = MiraiConsole.version.toString()

    override fun getMinecraftVersion(): String? = null
}