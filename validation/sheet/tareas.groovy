/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */


import com.google.api.services.sheets.v4.SheetsScopes
import es.edn.groogle.*
import com.google.api.services.drive.DriveScopes

@Grab("es.edn:groogle:latest") //relace "latest" with last published version
@GrabConfig(systemClassLoader=true)

groogle = GroogleBuilder.build {
    withOAuthCredentials {
        applicationName 'test'
        scopes DriveScopes.DRIVE, SheetsScopes.SPREADSHEETS
        usingCredentials "client_secret.json"
        storeCredentials true
    }
}

groogle.with {
    service(SheetService).with {
        withSpreadSheet "1yZ8wpbDUH-c3uZJLHSTGTa2DZ-kyxdQJs934uULNMA0", {
            withSheet "Tareas", {
                println B4 + "=" + C4
            }
        }
    }
}


