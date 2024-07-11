/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle

import es.edn.groogle.sheet.GroovySheetService
import groovy.transform.CompileStatic

@CompileStatic
class SheetServiceBuilder {

    static SheetService build(){
        new GroovySheetService()
    }
}
