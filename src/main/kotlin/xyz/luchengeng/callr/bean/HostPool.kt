package xyz.luchengeng.callr.bean

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

class HostPool(private val initSize : Int,
               private val rScriptPath : String,
               private val rExecPath : String ,
               private var basePort : Int) {
    private val freePorts = mutableListOf<Int>()
    private val logger = LoggerFactory.getLogger(this::class.java)
    var hostCreationRequest : Int = 0
    inner class RHost(val port : Int, rExec : String, rScriptPath : String){
        private val process : Process = ProcessBuilder().command(rExec,rScriptPath,port.toString()).start()
        init{
            val errorGobbler = StreamGobbler(process.errorStream){
                logger.warn(it)
            }
            errorGobbler.start()
            val outputGobbler = StreamGobbler(process.inputStream){
                logger.info(it)
            }
            outputGobbler.start()
        }
        fun terminate(){
            logger.info("Destroying server at $port")
            process.destroyForcibly()
        }
        constructor(port : Int) :this(port, rExecPath, this@HostPool.rScriptPath)
    }

    class StreamGobbler(private val ist: InputStream, val logger : (String?)->Unit) : Thread() {

        override fun run() {
            try {
                val isr = InputStreamReader(ist)
                val br = BufferedReader(isr)
                var line: String? = null
                while (br.readLine().also { line = it } != null){
                    logger(line)
                }
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }
    }
    private val hostQueue : LinkedBlockingQueue<RHost> = LinkedBlockingQueue()
    @PostConstruct
    private fun initPool(){
        repeat(initSize){
            hostQueue.add(RHost(basePort))
            basePort++
        }
    }
    @PreDestroy
    private fun clearPool(){
        while (hostQueue.size != 0){
            hostQueue.take().terminate()
        }
    }

    operator fun invoke(path : String,func : okhttp3.Request.Builder.(  )-> Unit ) : okhttp3.Response {
        val host = this.hostQueue.take()
        val request = okhttp3.Request.Builder()
                .url("http://127.0.0.1:${host.port}"+path)
        request.func()
        val response = okhttp3.OkHttpClient().newCall(request.build()).execute()
        this.hostQueue.add(host)
        return response
    }
}