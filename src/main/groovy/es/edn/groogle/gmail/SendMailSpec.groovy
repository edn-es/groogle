/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.gmail

import es.edn.groogle.GmailService
import groovy.transform.CompileStatic
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message

import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import java.util.function.Consumer


@CompileStatic
class SendMailSpec implements GmailService.SendMail{

    Gmail service
    String to
    String from
    String body
    String format = 'text/plain'
    String subject
    List<File> attachments = null
    @Override
    GmailService.SendMail to(String to) {
        this.to = to
        this
    }

    @Override
    GmailService.SendMail from(String from) {
        this.from = from
        this
    }

    @Override
    GmailService.SendMail body(String body) {
        this.body = body
        this
    }

    @Override
    GmailService.SendMail format(String format) {
        this.format = format
        this
    }
    @Override
    GmailService.SendMail subject(String subject) {
        this.subject = subject
        this
    }

    @Override
    GmailService.SendMail attachments(Consumer<GmailService.AttachmentSource> consumer) {
        AttachmentSourceSpec spec = new AttachmentSourceSpec(sendMailSpec: this)
        consumer.accept(spec)
        this.attachments = spec.attachments()
        createEmail()
        this
    }

    @Override
    GmailService.SendMail attachments(@DelegatesTo(value = GmailService.AttachmentSource, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        AttachmentSourceSpec spec = new AttachmentSourceSpec(sendMailSpec: this)
        Closure clone = closure.rehydrate(spec, closure.owner, closure.thisObject)
        clone()
        this.attachments = spec.attachments()
        this
    }

    GmailService.SendMail sendMail(){
        sendMessage(createEmail())
        this
    }

    Message sendMessage(MimeMessage emailContent)throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent)
        service.users().messages().send("me", message).execute()
        message
    }

    Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        Message message = new Message()
        emailContent.writeTo(buffer)
        byte[] bytes = buffer.toByteArray()
        message.raw = Base64.getUrlEncoder().encodeToString(bytes)
        message
    }

    MimeMessage createEmail() throws MessagingException, IOException {

        MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties(), null))
        email.from = new InternetAddress(from)
        email.addRecipient(javax.mail.Message.RecipientType.TO,new InternetAddress(to))
        email.subject = subject
        email.setContent(obtainAttachments())

        email
    }
    Multipart obtainAttachments(){
        MimeBodyPart mimeBodyPart = new MimeBodyPart()
        mimeBodyPart.setContent(body, format)
        Multipart multipart = new MimeMultipart()
        multipart.addBodyPart(mimeBodyPart)
        if (attachments){
            attachments.each{attachment->
                mimeBodyPart = new MimeBodyPart()
                DataSource source = new FileDataSource(attachment)
                mimeBodyPart.setDataHandler(new DataHandler(source))
                mimeBodyPart.setFileName(attachment.name)
                multipart.addBodyPart(mimeBodyPart)
            }
        }
        multipart
    }

}
