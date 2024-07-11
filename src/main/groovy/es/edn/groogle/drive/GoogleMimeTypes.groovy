/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.drive

trait GoogleMimeTypes {

    String getFolderMimeType(){
        "application/vnd.google-apps.folder"
    }

    String getGoogleDocMimeType() {
        "application/vnd.google-apps.document"
    }

}