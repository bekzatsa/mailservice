package kz.innlab.mailservice.app.mail.model

import java.sql.Timestamp
import java.util.*

class MailMessage {
    private var id: String? = null
    private var from: String? = null
    private var to: String? = null
    private var cc: String? = null
    private var bcc: String? = null
    private var subject: String? = null
    private var content: String? = null
    private var plain: String? = null
    private var attachments: Array<MutableMap<String, String>> = arrayOf()
    private var model: MutableMap<String, String> = mutableMapOf()
    private var date: Timestamp = Timestamp(System.currentTimeMillis())

    constructor() {}

    fun getId() = this.id
    fun setId(id: String?) {
        this.id = id
    }

    fun getFrom() = this.from
    fun setFrom(from: String) {
        this.from = from
    }

    fun getTo() = this.to
    fun setTo(to: String?) {
        this.to = to
    }

    fun getCc() = this.cc
    fun setCc(cc: String?) {
        this.cc = cc
    }

    fun getBcc() = this.bcc
    fun setBcc(bcc: String?) {
        this.bcc = bcc
    }

    fun getSubject() = this.subject
    fun setSubject(subject: String?) {
        this.subject = subject
    }

    fun getContent() = this.content
    fun setContent(content: String?) {
        this.content = content
    }

    fun getPlain() = this.plain
    fun setPlain(plain: String?) {
        this.plain = plain
    }

    fun getAttachments() = this.attachments
    fun setAttachments(attachments: Array<MutableMap<String, String>>) {
        this.attachments = attachments
    }

    fun getModel() = this.model
    fun setModel(model: MutableMap<String, String>) {
        this.model = model
    }
}
