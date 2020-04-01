package xyz.luchengeng.callr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
internal class CallrApplication

internal fun main(args: Array<String>) {
    runApplication<CallrApplication>(*args)
}
