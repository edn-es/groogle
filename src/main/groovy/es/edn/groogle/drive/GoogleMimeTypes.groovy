package es.edn.groogle.drive

trait GoogleMimeTypes {

    String getFolderMimeType(){
        "application/vnd.google-apps.folder"
    }

    String getGoogleDocMimeType() {
        "application/vnd.google-apps.document"
    }

}