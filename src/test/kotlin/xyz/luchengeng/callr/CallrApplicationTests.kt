package xyz.luchengeng.callr

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.luchengeng.callr.config.CallerProps

@SpringBootTest
class CallrApplicationTests @Autowired constructor(val callerProps: CallerProps) {
    @Test
    fun loadTest() {

    }
}
