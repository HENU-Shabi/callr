package xyz.luchengeng.callr.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.luchengeng.callr.bean.BaseHostPool
import xyz.luchengeng.callr.bean.ClasspathHostPool
import xyz.luchengeng.callr.bean.ExternalHostPool
import xyz.luchengeng.callr.bean.unpack

@Configuration
@ConditionalOnProperty(prefix = "caller.pool", name = ["scriptSource"])
@EnableConfigurationProperties(CallerProps::class)
class CallerAutoConfig @Autowired constructor(val callerProps: CallerProps) {
    @Bean
    fun hostPool(): BaseHostPool {
        return when (callerProps.scriptSource) {
            ScriptSource.EXTERNAL -> ExternalHostPool(callerProps.size, callerProps.rScriptPath, callerProps.rExecPath, callerProps.basePort)
            ScriptSource.CLASSPATH -> {
                val (init, api) = unpack(callerProps.initScript, callerProps.api)
                ClasspathHostPool(callerProps.size, init.absolutePath, api.absolutePath, callerProps.rExecPath, callerProps.basePort)
            }
        }
    }
}