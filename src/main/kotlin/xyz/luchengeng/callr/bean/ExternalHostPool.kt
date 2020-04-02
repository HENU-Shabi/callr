package xyz.luchengeng.callr.bean

import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader

class ExternalHostPool(initSize: Int,
                       private val rScriptPath: String,
                       rExecPath: String,
                       basePort: Int) : HostPool(initSize, basePort, rExecPath) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    inner class ExternalRHost(port: Int, rExec: String, rScriptPath: String) : HostPool.BaseRHost(port) {
        override val process: Process = ProcessBuilder().command(rExec, rScriptPath, port.toString()).start()

        init {
            val errorGobbler = StreamGobbler(process.errorStream, logger::info)
            errorGobbler.start()
            val outputGobbler = StreamGobbler(process.inputStream, logger::info)
            outputGobbler.start()
        }

        constructor(port: Int) : this(port, rExecPath, this@ExternalHostPool.rScriptPath)
    }

    override fun initPool() {
        repeat(initSize) {
            hostQueue.add(ExternalRHost(basePort))
            basePort++
        }
    }
}

internal fun unpack(initPath: String?, apiPath: String): Pair<File, File> =
        Pair(File.createTempFile("rPoolInit", ".R").apply {
            var mainR = InputStreamReader(ClassPathResource("lib.R").inputStream).readText() + "\n"
            if (initPath != null) mainR += InputStreamReader(ClassPathResource(initPath).inputStream).readText() + "\n"
            mainR += InputStreamReader(ClassPathResource("plumb.R").inputStream).readText() + "\n"
            FileWriter(this).apply {
                this.write(mainR)
                this.flush()
                this.close()
            }
        }, File.createTempFile("rPoolApi", ".R").apply {
            val apiR = InputStreamReader(ClassPathResource(apiPath).inputStream).readText()
            FileWriter(this).apply {
                this.write(apiR)
                this.flush()
                this.close()
            }
        })
