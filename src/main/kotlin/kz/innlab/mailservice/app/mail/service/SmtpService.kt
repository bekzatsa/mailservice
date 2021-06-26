package kz.innlab.mailservice.app.mail.service

import kz.innlab.mailservice.app.mail.model.MailMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import javax.mail.search.FlagTerm


@Service
class SmtpService {

    @Value("\${spring.mail.default-encoding}")
    private val mailDefaultEncoding: String? = null

    private var mailSender: JavaMailSenderImpl = JavaMailSenderImpl()

    constructor() {}

    fun setMailConfig(config: MutableMap<String, Any?>) {
        mailSender = JavaMailSenderImpl()
        if (config.containsKey("defaultEncoding") && config["defaultEncoding"] != null) {
            mailSender.defaultEncoding = config["defaultEncoding"].toString()
        } else {
            mailSender.defaultEncoding = mailDefaultEncoding
        }
        mailSender.host = config["host"].toString()
        mailSender.port = config["port"] as Int
        mailSender.username = config["username"].toString()
        mailSender.password = config["password"].toString()

        val javaMailProperties = Properties()
        javaMailProperties["mail.smtp.starttls.enable"] = config["smtpStartTls"].toString()
        javaMailProperties["mail.smtp.auth"] = config["smtpAuth"].toString()
        javaMailProperties["mail.transport.protocol"] = config["protocol"].toString()
        javaMailProperties["mail.debug"] = config["debug"].toString()
        mailSender.javaMailProperties = javaMailProperties
    }

    @Throws(AddressException::class, MessagingException::class, IOException::class)
    fun sendmail(mail: MailMessage) {
        val session: Session = Session.getInstance(mailSender.javaMailProperties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(mailSender.username, mailSender.password)
            }
        })

        val msg: MimeMessage = mailSender.createMimeMessage()
//        msg.setSession(session)
        val helper = MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name())
        helper.setFrom(mail.getFrom()!!)
        helper.setTo(mail.getTo()!!)
        helper.setText(mail.getContent()!!, true)
        helper.setSubject(mail.getSubject()!!)



        helper.setFrom(InternetAddress(mail.getFrom(), false))
        helper.setTo(InternetAddress.parse(mail.getTo()))
        helper.setText(mail.getContent()!!, true)
        helper.setSentDate(Date())

        val messageBodyPart = MimeBodyPart()
        messageBodyPart.setContent(mail.getContent(), "text/html")

        val multipart: Multipart = MimeMultipart()
        multipart.addBodyPart(messageBodyPart)
//        val attachPart = MimeBodyPart()
//        attachPart.attachFile("/Users/bekzat/Desktop/Портфолио/9-admin.png")
//        multipart.addBodyPart(attachPart)
        msg.setContent(multipart)

        mailSender.send(msg)
    }

}
