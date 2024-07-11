/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle

import es.edn.groogle.gmail.GroovyGmailService
import groovy.transform.CompileStatic

@CompileStatic
class GmailServiceBuilder {

    static GmailService build(){
        new GroovyGmailService()
    }
}
