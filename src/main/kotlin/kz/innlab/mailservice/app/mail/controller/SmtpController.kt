package kz.innlab.mailservice.app.mail.controller

import kz.innlab.mailservice.app.mail.model.MailMessage
import kz.innlab.mailservice.app.mail.service.SmtpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/smtp")
class SmtpController {

    @Autowired
    lateinit var smtpService: SmtpService

    @GetMapping("/send")
    fun sendEmail(): String {
        var mail = MailMessage()
        mail.setFrom("info@mdsp.kz")
        mail.setTo("bekzat.saylaubay@gmail.com")
        mail.setSubject("Test")
        mail.setContent("<b>Test content</b>")
        var mailConfig: MutableMap<String, Any?> = mutableMapOf(
            "host" to "smtp.yandex.ru",
            "port" to 465,
            "username" to "info@mdsp.kz",
            "password" to "qkkytopxenlgcqwg",
            "smtpStartTls" to "true",
            "smtpAuth" to "true",
            "protocol" to "smtps",
            "debug" to "true"
        )
        smtpService.setMailConfig(mailConfig)
        smtpService.sendmail(mail)
        return "Email sent successfully"
    }
}
