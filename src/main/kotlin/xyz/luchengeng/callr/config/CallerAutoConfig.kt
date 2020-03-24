package xyz.luchengeng.callr.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.luchengeng.callr.bean.HostPool

@Configuration
@EnableConfigurationProperties(CallerProps::class)
class CallerAutoConfig @Autowired constructor(val callerProps: CallerProps) {
    @Bean
    fun hostPool() = HostPool(callerProps.size,callerProps.rScriptPath,callerProps.rExecPath,callerProps.basePort)
}