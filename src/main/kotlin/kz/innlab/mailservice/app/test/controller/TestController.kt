package kz.innlab.mailservice.app.test.controller

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
@RefreshScope
class TestController {

    private var logger = LogManager.getLogger(TestController::class.java)

    @Value("\${test.name}")
    lateinit var test: String

    @GetMapping("/test")
    fun test(): String {
        println("Request test get!")
        logger.info("Making call to Mailservice ${test}")
        return test
    }
}
