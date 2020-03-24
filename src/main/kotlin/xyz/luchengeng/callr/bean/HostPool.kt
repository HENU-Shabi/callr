package xyz.luchengeng.callr.bean

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    var hostCreationRequest : Int = 0
    inner class RHost(val port : Int,val rExec : String,val rScriptPath : String,var usageFlag : Int){
        val process : Process = ProcessBuilder().command(rExec,rScriptPath,port.toString()).start()
        init{
            val errorGobbler = StreamGobbler(process.getErrorStream())
            errorGobbler.start()
            val outputGobbler = StreamGobbler(process.getInputStream())
            outputGobbler.start()
        }
        fun terminate(){
            println("Destroying server at $port")
            process.destroyForcibly()
        }
        constructor(port : Int) :this(port, rExecPath, this@HostPool.rScriptPath, usageFlag = 0)
    }

    class StreamGobbler(ist: InputStream) : Thread() {
        var ist: InputStream
        override fun run() {
            try {
                val isr = InputStreamReader(ist)
                val br = BufferedReader(isr)
                var line: String? = null
                while (br.readLine().also { line = it } != null) println(line)
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }

        // reads everything from is until empty.
        init {
            this.ist= ist
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
    private fun creationLoop()=GlobalScope.launch {
        while(true){
            repeat(this@HostPool.hostCreationRequest){
                hostQueue.add(RHost(portFetch()))
            }
            hostCreationRequest = 0
            delay(10)
        }
    }

    private fun destructionLoop()=GlobalScope.launch {
        while(true){
            var idleCount = 0
            hostQueue.forEach{
                if(it.usageFlag == 0){
                    idleCount++
                }
                it.usageFlag = 0
            }
            repeat(idleCount){
                portRecycle(hostQueue.take())
            }
            delay(1000)
        }
    }

    private fun serverListening(host: String = "localhost", port: Int): Boolean {
        var s: Socket? = null
        return try {
            s = Socket(host, port)
            true
        } catch (e: Exception) {
            false
        } finally {
            if (s != null) try {
                s.close()
            } catch (e: Exception) {
            }
        }
    }

    private fun portFetch() : Int = this.freePorts.run {
        if(this.size != 0){
            this.removeAt(0)
        }else{
            basePort++
            if(!serverListening(port = basePort))basePort else portFetch()
        }
    }

    private fun portRecycle(host : RHost){
        host.terminate()
        GlobalScope.launch {
            delay(100)
            if(!serverListening("localhost",host.port))freePorts.add(host.port)
        }
    }
}