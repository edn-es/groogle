package es.edn.groogle

import es.edn.groogle.gmail.GroovyGmailService
import groovy.transform.CompileStatic

@CompileStatic
class GmailServiceBuilder {

    static GmailService build(){
        new GroovyGmailService()
    }
}
