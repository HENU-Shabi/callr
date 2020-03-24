package xyz.luchengeng.callr.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CallerProps::class)
class CallerAutoConfig {

}