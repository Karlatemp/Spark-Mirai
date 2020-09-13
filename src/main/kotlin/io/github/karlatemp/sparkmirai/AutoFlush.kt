package io.github.karlatemp.sparkmirai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedQueue

object AutoFlush {
    val flushQueue = ConcurrentLinkedQueue<WeakReference<MiraiCommandSender>>()

    init {
        SparkMirai.launch(Dispatchers.IO) {
            while (true) {
                delay(3000)
                val iterator = flushQueue.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    val sender = next.get()
                    if (sender == null) {
                        iterator.remove()
                        continue
                    }
                    sender.release()
                    delay(100)
                }
            }
        }
    }
}