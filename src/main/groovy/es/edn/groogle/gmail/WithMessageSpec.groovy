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
import es.edn.groogle.GmailService
import groovy.transform.CompileStatic

import javax.activation.DataHandler
import javax.mail.BodyPart
import javax.mail.MessagingException
import javax.mail.Multipart
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
    String getBody(){
        MimeMessage mimeMessage = getMimeMessage(userId, message.id as String)
        mimeMessage.content
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
            File out = new File(root, data.name)
            IOUtils.copy(data.inputStream, out.newObjectOutputStream())
        }
    }

    void deleteMessage(){
        service.users().threads().delete(this.userId, message.id.toString()).execute()
    }
}
