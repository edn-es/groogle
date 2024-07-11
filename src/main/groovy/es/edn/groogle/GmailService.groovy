package es.edn.groogle

import com.google.api.services.gmail.model.Message
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
interface GmailService extends Groogle.GroogleService{


    interface SendMail {
        SendMail to(String to)
        SendMail from(String from)
        SendMail body(String body)
        SendMail format(String format)
        SendMail subject(String subject)

        SendMail attachments(Consumer<AttachmentSource> consumer )

        SendMail attachments(@DelegatesTo(value=AttachmentSource, strategy = Closure.DELEGATE_FIRST)Closure closure )

    }

    GmailService sendEmail ( Consumer<SendMail> consumer )

    GmailService sendEmail ( @DelegatesTo(value=SendMail, strategy = Closure.DELEGATE_FIRST)Closure closure )

    GmailService eachMessage(Consumer<FilterMessage> filterMessagesConsumer, Consumer<WithMessage> withMessageConsumer)

    GmailService eachMessage(
            @DelegatesTo(value=FilterMessage, strategy = Closure.DELEGATE_FIRST)Closure filterMessagesClosure,
            @DelegatesTo(value=WithMessage, strategy = Closure.DELEGATE_FIRST)Closure withMessageClosure
    )

    interface AttachmentSource{
        AttachmentSource dir(String dirPath, String criteria)
        AttachmentSource file(File file)
        AttachmentSource file(String file)
    }

    interface FilterMessage {
        FilterMessage from(String from)
        FilterMessage to(String to)
        FilterMessage unRead(boolean unRead)
        FilterMessage labelNames(String labelIds)
        FilterMessage subject(String subject)
        FilterMessage body(String body)
        FilterMessage after(String dayAfter)
        FilterMessage before(String dayBefore)
        FilterMessage after(Date dayAfter)
        FilterMessage before(Date dayBefore)
        FilterMessage inChat(boolean inChat)
        FilterMessage larger(String larger)
        FilterMessage smaller(String smaller)
        FilterMessage hasAttachments(boolean hasAttachments)

    }

    interface WithMessage{
        Message getMessage()
        WithMessage downloadAttachments(String localPath)
        WithMessage downloadAttachmentsIfExtension(String localPath, String extension)
        WithMessage delete()
        String getBody()
    }
}
