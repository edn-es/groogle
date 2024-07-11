package es.edn.groogle.gmail

import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter
import es.edn.groogle.GmailService
import com.google.api.services.gmail.Gmail
import es.edn.groogle.core.InternalService

import java.util.function.Consumer

class GroovyGmailService implements InternalService, GmailService{

    Gmail service

    @Override
    void configure(JsonFactory jsonFactory, HttpTransport httpTransport, Credentials credentials, String applicationName ) {
        def http = new HttpCredentialsAdapter(credentials)
        this.service = new Gmail.Builder(httpTransport, jsonFactory, http)
                .setApplicationName(applicationName)
                .build()
    }

    @Override
    GmailService sendEmail(@DelegatesTo(value=SendMail, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        SendMailSpec spec = new SendMailSpec(service: service)
        Closure clone = closure.rehydrate(spec, closure.owner, closure.thisObject)
        clone()
        spec.sendMail()
        this
    }

    @Override
    GmailService sendEmail(Consumer<SendMail> consumer) {
        SendMailSpec spec = new SendMailSpec(service: service)
        consumer.accept(spec)
        spec.sendMail()
        this
    }
    @Override
    GmailService eachMessage(
            @DelegatesTo(value=FilterMessage, strategy = Closure.DELEGATE_FIRST) Closure filterMessagesClosure,
            @DelegatesTo(value=WithMessage, strategy = Closure.DELEGATE_FIRST) Closure withMessage) {
        FilterMessageSpec eachMessageSpec = new FilterMessageSpec(service: service)
        Closure cloneFind = filterMessagesClosure.rehydrate(eachMessageSpec,filterMessagesClosure.owner, filterMessagesClosure.thisObject)
        cloneFind()

        while( eachMessageSpec.hasMoreMessages() ){
            eachMessageSpec.nextMessages().each {
                WithMessageSpec withMessageSpec = new WithMessageSpec(service: service, message: it)
                Closure clone = withMessage.rehydrate(withMessageSpec, withMessage.owner, withMessage.thisObject)
                clone()
            }
        }
        this
    }

    @Override
    GmailService eachMessage(Consumer<FilterMessage> filterMessagesConsumer, Consumer<WithMessage> withMessage) {
        throw new RuntimeException("Not implemented yet")
    }
}
