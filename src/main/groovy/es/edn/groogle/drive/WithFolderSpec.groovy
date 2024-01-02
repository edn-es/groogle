package es.edn.groogle.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import es.edn.groogle.DriveService
import groovy.io.FileType
import groovy.transform.CompileStatic

@CompileStatic
class WithFolderSpec implements DriveService.WithFolder, GoogleMimeTypes{

    GroovyDriveService groovyDriveService
    Drive service
    File folder

    @Override
    String getId() {
        folder.id
    }

    DriveService.WithFolder downloadTo(final String path){
        downloadTo(new java.io.File(path))
    }

    DriveService.WithFolder downloadTo(final java.io.File dir){
        dir.mkdirs()
        FindFilesSpec findFilesSpec = new FindFilesSpec(service: service)
        findFilesSpec.parentId(id)
        findFilesSpec.eachFile {
            final String folderName = "$dir.absolutePath/$file.name"
            if( file.getMimeType() == folderMimeType ){
                WithFolderSpec subfoder = new WithFolderSpec(groovyDriveService: groovyDriveService,service: service,folder: file)
                subfoder.downloadTo( folderName  )
            }else{
                java.io.File f = new java.io.File( dir,"$file.name")
                f << byteArray
            }
        }
        findFilesSpec.execute()
        this
    }

    DriveService.WithFolder uploadFrom(final String path){
        uploadFrom(new java.io.File(path))
    }

    DriveService.WithFolder uploadFrom(final java.io.File dir){
        final String folderId = id
        dir.eachFile(FileType.FILES) {
            groovyDriveService.upload(it, folderId)
        }
        dir.eachFile(FileType.DIRECTORIES){
            groovyDriveService
                    .createFolder(it.name, folderId)
                    .uploadFrom(it)
        }
        this
    }

    DriveService.WithFolder removeFromDrive(){
        groovyDriveService.withFile(id,{
            removeFromDrive()
        })
        this
    }

}
