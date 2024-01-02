package es.edn.groogle.drive


import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import es.edn.groogle.DriveService
import groovy.transform.CompileStatic

@CompileStatic
class CreateFileSpec implements DriveService.CreateFile, GoogleMimeTypes{

    Drive service
    InputStream content
    boolean direct = true
    String parent

    @Override
    DriveService.CreateFile intoFolder(String folderId) {
        this.parent = folderId
        this
    }

    @Override
    DriveService.CreateFile fromFile(String path) {
        fromFile( new java.io.File(path) )
    }

    @Override
    DriveService.CreateFile fromFile(java.io.File path) {
        direct = path.size() < 5 * 1024 * 1000
        content = path.newInputStream()
        withName( path.name )
        this
    }

    String fileName
    @Override
    DriveService.CreateFile withName(String name) {
        this.fileName = name
        mimeType = URLConnection.fileNameMap.getContentTypeFor(fileName)
        this
    }

    @Override
    DriveService.CreateFile fromContent(String content) {
        fromContent(new ByteArrayInputStream(content.bytes), "plain/text")
    }

    @Override
    DriveService.CreateFile fromContent(ByteArrayInputStream content, String format) {
        this.content = content
        this.mimeType = format
        this
    }

    @Override
    DriveService.CreateFile asGoogleDoc() {
        this.mimeType = getGoogleDocMimeType()
        this
    }

    String mimeType
    protected File execute(){
        File metadata = new File(name: fileName, parents: [parent])
        InputStreamContent content = new InputStreamContent(mimeType, content)
        service.files().create(metadata,content).setFields("id,parents").setSupportsAllDrives(true).execute()
    }
}
