package xyz.luchengeng.callr.bean

import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ExternalHostPool(initSize: Int,
                       private val rScriptPath: String,
                       rExecPath: String,
                       basePort: Int) : BaseHostPool(initSize, basePort, rExecPath) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    inner class ExternalRHost(port: Int, rExec: String, rScriptPath: String) : BaseHostPool.BaseRHost(port) {
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

fun unpack(initPath: String?, apiPath: String): Pair<File, File> =
        Pair(File.createTempFile("rPoolInit", ".R").apply {
            var mainR = FileReader(ClassPathResource("lib.R").file).readText() + "\n"
            if (initPath != null) mainR += FileReader(ClassPathResource(initPath).file).readText() + "\n"
            mainR += FileReader(ClassPathResource("plumb.R").file).readText() + "\n"
            FileWriter(this).apply {
                this.write(mainR)
                this.flush()
                this.close()
            }
        }, File.createTempFile("rPoolApi", ".R").apply {
            val apiR = FileReader(ClassPathResource(apiPath).file).readText()
            FileWriter(this).apply {
                this.write(apiR)
                this.flush()
                this.close()
            }
        })
