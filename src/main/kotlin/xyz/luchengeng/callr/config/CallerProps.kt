package xyz.luchengeng.callr.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "caller.pool")
class CallerProps {
    var size: Int = 4
    var rScriptPath: String = "main.R"
    var rExecPath: String = "Rscript.exe"
    var basePort = 7080
    var scriptSource: ScriptSource = ScriptSource.EXTERNAL
    var initScript: String? = null
    var api: String = "api.R"
}