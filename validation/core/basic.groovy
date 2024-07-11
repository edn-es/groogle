/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */


import com.google.api.services.drive.DriveScopes
import es.edn.groogle.*
import com.google.api.services.sheets.v4.SheetsScopes

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
groogle.with{
    login()
    println "logged"
}
