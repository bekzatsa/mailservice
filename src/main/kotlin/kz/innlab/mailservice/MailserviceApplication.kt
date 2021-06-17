package kz.innlab.mailservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MailserviceApplication

fun main(args: Array<String>) {
	runApplication<MailserviceApplication>(*args)
}
