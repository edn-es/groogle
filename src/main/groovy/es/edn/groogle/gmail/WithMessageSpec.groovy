/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.gmail

import com.google.api.client.util.IOUtils
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.MessagePart
import com.google.api.services.gmail.model.MessagePartHeader
import com.google.api.services.gmail.model.ModifyMessageRequest
import com.google.api.services.gmail.model.ModifyThreadRequest
import com.google.common.io.ByteStreams
import es.edn.groogle.GmailService
import groovy.transform.CompileStatic

import javax.activation.DataHandler
import javax.mail.BodyPart
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.Part
import javax.mail.Session
import javax.mail.internet.MimeMessage

@CompileStatic
class WithMessageSpec implements GmailService.WithMessage{

    Gmail service
    String userId = 'me'

    Message message

    @Override
    GmailService.WithMessage downloadAttachments(String path) {
        downloadAttachmentsIfExtension(path, null)
    }

    @Override
    GmailService.WithMessage downloadAttachmentsIfExtension(String localPath, String extension) {
        downloadFiles(localPath, extension)
        this
    }

    @Override
    GmailService.WithMessage delete(){
        deleteMessage()
        this
    }

    @Override
    void markAsReaded() {
        readed()
    }

    @Override
    String getBody() {
        MimeMessage mimeMessage = getMimeMessage(userId, message.id as String)
        getPartText(mimeMessage)
    }

    private Message headers

    @Override
    String getSubject() {
        getHeaders().find{ "subject" == it.get("name")?.toString()?.toLowerCase()}?.get("value")
    }

    @Override
    List<MessagePartHeader> getHeaders() {
        if( !headers ) {
            headers = service.users().messages().get(userId, message.id as String).setFields("payload/headers").execute()
        }
        def payload = headers.get("payload") as MessagePart
        def headers = payload.get("headers") as List<MessagePartHeader>
        headers
    }

    MimeMessage getMimeMessage(String userId, String messageId)
            throws IOException, MessagingException {
        Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute()
        byte[] emailBytes = Base64.getUrlDecoder().decode(message.getRaw())
        Properties props = new Properties()
        Session session = Session.getDefaultInstance(props, null)
        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes))

        return email
    }

    List<DataHandler> messagesData(String filterExtension){
        List<DataHandler> data = []
        MimeMessage mimeMessage =  getMimeMessage(this.userId, message.id.toString())
        mimeMessage.content.each { Multipart attachment->
            (0..attachment.getCount()-1).each{index->
                BodyPart bodyPart = attachment.getBodyPart(index)
                if (bodyPart){
                    String disposition = bodyPart.disposition
                    if (disposition){
                        DataHandler handler = bodyPart.dataHandler
                        if (filterExtension){
                            String extension = handler.name.substring(handler.name.lastIndexOf("."))
                            if (extension in filterExtension.split(",")){
                                data << handler
                            }
                        }else{
                            data << handler
                        }
                    }
                }
            }
        }
        data
    }

    void downloadFiles(String path, String extension){
        File root = new File(path)
        root.mkdirs()
        messagesData(extension).each {DataHandler data->
            FileOutputStream out = new FileOutputStream(new File(root, data.name))
            ByteStreams.copy(data.inputStream, out)
            out.close()
        }
    }

    void deleteMessage(){
        service.users().threads().delete(this.userId, message.id.toString()).execute()
    }

    String getPartText(Part p) throws MessagingException, IOException{
        if( p.isMimeType("text/*"))
            return p.content
        if( p.isMimeType("multipart/*")){
            var multipart = p.content as Multipart
            return (0..multipart.count-1).collect {getPartText(multipart.getBodyPart(it))}.join("\n")
        }
        return null
    }

    void readed(){
        ModifyMessageRequest request = new ModifyMessageRequest(removeLabelIds: ['UNREAD'])
        service.users().messages().modify(this.userId, message.id.toString(), request).execute()
    }
}
