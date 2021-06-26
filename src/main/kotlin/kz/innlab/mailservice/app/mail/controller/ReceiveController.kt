package kz.innlab.mailservice.app.mail.controller

import kz.innlab.mailservice.app.mail.model.MailMessage
import kz.innlab.mailservice.app.mail.service.ReceiveService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/receive")
class ReceiveController {

    @Autowired
    lateinit var receiveService: ReceiveService

    @PostMapping("/messages")
    fun getMailMessages(@Valid @RequestBody mailConfig: MutableMap<String, String>): Array<MailMessage> {
        println("Get Mail Messages")
        return receiveService.receiveEmail(
            mailConfig["host"].toString(),
            mailConfig["storeType"].toString(),
            mailConfig["protocol"].toString(),
            mailConfig["username"].toString(),
            mailConfig["password"].toString(),
            mailConfig["setFlag"].toString().toBoolean()
        )
    }
}
