/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle

class GroogleCli {

    static void main(String[] args) {
        def shell = new GroovyShell()
        def script = shell.parse(new File(args[0]))
        script.run()
    }

}
