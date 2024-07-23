/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */


import com.google.api.services.gmail.GmailScopes
import es.edn.groogle.*

@Grab("es.edn:groogle:latest") //relace "latest" with last published version
@GrabConfig(systemClassLoader=true)

groogle = GroogleBuilder.build {
    withOAuthCredentials {
        applicationName 'test-gmail'
        scopes GmailScopes.MAIL_GOOGLE_COM
        usingCredentials "client_secret.json"
        storeCredentials true
    }
}

groogle.with {
    service(GmailService).with {
        eachMessage({
            subject "test"
        }, {
            println subject
        })
    }
}


