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

groogle = GroogleBuilder.build {
    withOAuthCredentials {
        applicationName 'test-drive'
        scopes DriveScopes.DRIVE
        usingCredentials "client_secret.json"
        storeCredentials true
    }
}

groogle.with {
    service(DriveService).with {
        findFiles {
            eachFile {
                println "$id = $file.name"
            }
        }
    }
}