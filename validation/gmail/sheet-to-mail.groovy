/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */


import com.google.api.services.gmail.GmailScopes
import com.google.api.services.sheets.v4.SheetsScopes
import es.edn.groogle.*

@Grab("es.edn:groogle:latest") //relace "latest" with last published version
@GrabConfig(systemClassLoader=true)

groogle = GroogleBuilder.build {
    withOAuthCredentials {
        applicationName 'test-gmail-sheet'
        scopes GmailScopes.MAIL_GOOGLE_COM, SheetsScopes.SPREADSHEETS
        usingCredentials "client_secret.json"
        storeCredentials true
    }
}

groogle.with {
    service(SheetService).with {
        withSpreadSheet "1yZ8wpbDUH-c3uZJLHSTGTa2DZ-kyxdQJs934uULNMA0", {
            withSheet "Tareas", {

                def message = """Hi
                this is the status of the task 
                $B4 = $C4
                """.strip()

                service(GmailService).with {
                    sendEmail {
                        from "me"
                        to args[0]
                        subject args[1]
                        body message
                    }
                }
            }
        }
    }
}


