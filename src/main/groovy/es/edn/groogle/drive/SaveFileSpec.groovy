package es.edn.groogle.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import es.edn.groogle.DriveService
import groovy.transform.CompileStatic

@CompileStatic
class SaveFileSpec implements DriveService.SaveAs{

    Drive service
    File file
    OutputStream outputStream

    @Override
    DriveService.SaveAs to(String path) {
        to( new java.io.File(path))
    }

    @Override
    DriveService.SaveAs to(java.io.File dir) {
        dir.parentFile.mkdirs()
        to(dir.newOutputStream())
    }

    @Override
    DriveService.SaveAs to(OutputStream outputStream) {
        this.outputStream=outputStream
        this
    }

    String format
    void setFile(File file){
        this.file = file
        String fileExtension = file.getFileExtension()
        switch( fileExtension ){
            case 'pdf':
                format = 'application/pdf'
                break
            case 'zip':
                format = 'application/zip'
                break
            default:
                format = 'text/plain'
        }
    }

    @Override
    DriveService.SaveAs exportAs(String format) {
        this.format = format
        this
    }

    @Override
    DriveService.SaveAs asPlainText() {
        this.format = 'text/plain'
        this
    }

    @Override
    DriveService.SaveAs asZip() {
        this.format = 'application/zip'
        this
    }

    @Override
    DriveService.SaveAs asPdf() {
        this.format = 'application/pdf'
        this
    }

    protected DriveService.SaveAs execute(){
        try {
            service.files().get(file.getId()).setSupportsAllDrives(true).executeMediaAndDownloadTo(outputStream)
        }catch(e){
            service.files().export(file.getId(), format).executeMediaAndDownloadTo(outputStream)
        }
        outputStream.flush()
        this
    }
}
