/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle

import es.edn.groogle.drive.GroovyDriveService
import groovy.transform.CompileStatic

@CompileStatic
class DriveServiceBuilder {

    static DriveService build(){
        new GroovyDriveService()
    }
}
