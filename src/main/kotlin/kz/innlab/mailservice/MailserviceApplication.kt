package kz.innlab.mailservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class MailserviceApplication

fun main(args: Array<String>) {
	runApplication<MailserviceApplication>(*args)
}
