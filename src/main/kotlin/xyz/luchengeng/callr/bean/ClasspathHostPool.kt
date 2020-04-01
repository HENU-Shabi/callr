package xyz.luchengeng.callr.bean

class ClasspathHostPool(initSize: Int,
                        private val mainR: String,
                        private val apiR: String,
                        rExecPath: String,
                        basePort: Int) : BaseHostPool(initSize, basePort, rExecPath) {
    override fun initPool() {
        repeat(initSize) {
            hostQueue.add(ClasspathRHost(basePort, mainR, apiR, rExecPath))
            basePort++
        }
    }

    class ClasspathRHost(port: Int, mainR: String, apiR: String, rExecPath: String) : BaseHostPool.BaseRHost(port) {
        override val process: Process = ProcessBuilder().command(rExecPath, mainR, port.toString(), apiR).start()
    }
}