package xyz.luchengeng.callr.bean

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.LinkedBlockingQueue
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

abstract class BaseHostPool(protected val initSize: Int, protected var basePort: Int, protected val rExecPath: String) {
    protected val hostQueue: LinkedBlockingQueue<BaseRHost> = LinkedBlockingQueue()

    @PostConstruct
    protected abstract fun initPool()

    @PreDestroy
    protected fun clearPool() {
        while (hostQueue.size != 0) {
            hostQueue.take().terminate()
        }
    }

    operator fun invoke(path: String, func: okhttp3.Request.Builder.() -> Unit): okhttp3.Response {
        val host = this.hostQueue.take()
        val request = okhttp3.Request.Builder()
                .url("http://127.0.0.1:${host.port}" + path)
        request.func()
        val response = okhttp3.OkHttpClient().newCall(request.build()).execute()
        this.hostQueue.add(host)
        return response
    }

    abstract class BaseRHost(val port: Int) {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        protected abstract val process: Process
        fun terminate() {
            logger.info("Destroying server at $port")
            process.destroyForcibly()
        }

        class StreamGobbler(private val ist: InputStream, val logger: (String?) -> Unit) : Thread() {
            override fun run() {
                try {
                    val isr = InputStreamReader(ist)
                    val br = BufferedReader(isr)
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        logger(line)
                    }
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }
        }
    }
}