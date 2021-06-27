package kz.innlab.mailservice.app.mail.service

import kz.innlab.mailservice.app.mail.model.MailMessage
import org.apache.commons.io.IOUtils
import org.apache.commons.io.output.CountingOutputStream
import org.apache.commons.io.output.NullOutputStream
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.mail.*
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.search.FlagTerm


@Service
class ReceiveService {

    constructor() {}

    fun receiveEmail(
        host: String,
        storeType: String,
        protocol: String,
        user: String,
        password: String,
        setFlag: Boolean = false
    ): Array<MailMessage> {
        var result: Array<MailMessage> = arrayOf()
        try {
            //1) get the session object
            val properties = Properties()
            properties["mail.store.protocol"] = protocol
            properties["mail.mime.encodeparameters"] = "false"
            properties["mail.mime.allowutf8"] = "false"
            //Session emailSession = Session.getDefaultInstance(properties);
            val emailSession = Session.getDefaultInstance(properties,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(user, password)
                    }
                })
            //2) create the POP3 store object and connect with the pop server
            val emailStore = emailSession.getStore(protocol)
            emailStore.connect(host, user, password)


            //3) create the folder object and open it
            val emailFolder = emailStore.getFolder("INBOX")
            val uf = emailFolder as UIDFolder
            if (setFlag) {
                emailFolder.open(Folder.READ_WRITE)
            } else {
                emailFolder.open(Folder.READ_ONLY)
            }

            //UnSeen messages flag
            val searchTerm = FlagTerm(Flags(Flags.Flag.SEEN), false)
            //4) retrieve the messages from the folder in an array and print it
            val messages = emailFolder.search(searchTerm)
//            val messages = emailFolder.messages
            var totalMessageSize: Long = 0
            for (message in messages) {
                val mailMessage = MailMessage()

                mailMessage.setId(uf.getUID(message).toString())
                mailMessage.setFrom(message.from.joinToString(", "))
                if (message.getRecipients(Message.RecipientType.TO) != null) {
                    mailMessage.setTo(message.getRecipients(Message.RecipientType.TO).joinToString(", "))
                }
                if (message.getRecipients(Message.RecipientType.CC) != null) {
                    mailMessage.setCc(message.getRecipients(Message.RecipientType.CC).joinToString(", "))
                }
                if (message.getRecipients(Message.RecipientType.BCC) != null) {
                    mailMessage.setBcc(message.getRecipients(Message.RecipientType.BCC).joinToString(", "))
                }

                mailMessage.setSubject(message.subject)
                mailMessage.setContent(getTextFromMessage(message))
                if (message.isMimeType("text/plain")) {
                    mailMessage.setPlain(message.content.toString())
                }
                mailMessage.setAttachments(getAttachmentFromMessage(message))
                if (setFlag) {
                    message.setFlag(Flags.Flag.SEEN, true)
                }

                result = result.plus(mailMessage)
                totalMessageSize += getReliableSize(message as MimeMessage)
                if (totalMessageSize >= 10485760) {
                    break
                }
            }

            //5) close the store and folder objects
            emailFolder.close(false)
            emailStore.close()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    @Throws(MessagingException::class, IOException::class)
    fun getTextFromMessage(message: Message): String? {
        var result = ""
        if (message.isMimeType("text/plain")) {
            result = message.content.toString()
        } else if (message.isMimeType("multipart/*")) {
            val mimeMultipart = message.content as MimeMultipart
            result = getTextFromMimeMultipart(mimeMultipart)
        }
        return result
    }

    @Throws(MessagingException::class, IOException::class)
    fun getTextFromMimeMultipart(
        mimeMultipart: MimeMultipart
    ): String {
        var result = ""
        val count = mimeMultipart.count
        for (i in 0 until count) {
            val bodyPart = mimeMultipart.getBodyPart(i)
            if (bodyPart.isMimeType("text/plain")) {
                result = """
                $result
                ${bodyPart.content}
                """.trimIndent()
                break // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                val html = bodyPart.content as String
                result = """
                $result
                ${org.jsoup.Jsoup.parse(html).html()}
                """.trimIndent()
            } else if (bodyPart.content is MimeMultipart) {
                result += getTextFromMimeMultipart(bodyPart.content as MimeMultipart)
            }
        }
        return result
    }

    @Throws(MessagingException::class, IOException::class)
    fun getAttachmentFromMessage(message: Message): Array<MutableMap<String, String>> {
        var result: Array<MutableMap<String, String>> = arrayOf()
        if (message.isMimeType("multipart/*")) {
            val mimeMultipart = message.content as MimeMultipart
            val count = mimeMultipart.count
            for (i in 0 until count) {
                val bodyPart = mimeMultipart.getBodyPart(i)
                if (Part.ATTACHMENT.equals(bodyPart.disposition, true)) {
                    val input: InputStream = bodyPart.inputStream

                    val bytes: ByteArray = IOUtils.toByteArray(input)
                    val encoded = Base64.getEncoder().encodeToString(bytes)
                    result = result.plus(mutableMapOf(
                        "filename" to bodyPart.fileName,
                        "base64" to encoded
                    ))

                }
            }
        }
        return result
    }

    @Throws(IOException::class, MessagingException::class)
    fun getReliableSize(m: MimeMessage): Long {
        CountingOutputStream(NullOutputStream()).use { out ->
            m.writeTo(out)
            return out.byteCount
        }
    }


}
