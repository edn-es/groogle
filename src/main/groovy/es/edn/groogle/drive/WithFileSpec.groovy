package es.edn.groogle.drive

import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import es.edn.groogle.DriveService
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
class WithFileSpec implements DriveService.WithFile, GoogleMimeTypes{

    Drive service
    File file

    @Override
    String getId() {
        file.id
    }

    @Override
    File getFile() {
        file
    }

    @Override
    DriveService.WithFile download(Consumer<DriveService.SaveAs> consumer) {
        SaveFileSpec spec = new SaveFileSpec(file: file,service: service)
        consumer.accept(spec)
        spec.execute()
        this
    }

    @Override
    DriveService.WithFile download(
            @DelegatesTo(value=DriveService.SaveAs,strategy = Closure.DELEGATE_FIRST) Closure closure) {
        SaveFileSpec spec = new SaveFileSpec(file:file,service: service)
        Closure clone = closure.rehydrate(spec,closure.owner,closure.thisObject)
        clone.resolveStrategy = Closure.DELEGATE_FIRST
        clone()
        spec.execute()
        this
    }

    @Override
    byte[] getByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        download{
            to bos
        }
        bos.toByteArray()
    }

    @Override
    String getText() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        download{
            to bos asPlainText()
        }
        String ret = new String(bos.toByteArray())
        ret
    }

    @Override
    DriveService.WithFile moveToFolder(File folder) {
        assert folder.mimeType==folderMimeType
        Drive.Files.Update update = service.files().update(file.getId(),null)
        update.addParents=folder.id
        if(file.getParents())
            update.removeParents=file.getParents().join(',')
        file = update.setSupportsAllDrives(true).execute()
        this
    }

    @Override
    DriveService.WithFile moveToFolder(String folderId) {
        File fileFolder = service.files().get(folderId).setSupportsAllDrives(true).execute()
        assert fileFolder != null
        moveToFolder(fileFolder)
        this
    }

    @Override
    DriveService.WithFile removeFromDrive() {
        service.files().delete(file.getId()).execute()
        file=null
        this
    }

    @Override
    DriveService.WithFile updateFromFile(java.io.File dir) {
        updateFrom(dir.newInputStream())
    }

    @Override
    DriveService.WithFile updateFromFile(String path) {
        updateFromFile(new java.io.File(path))
    }

    @Override
    DriveService.WithFile updateFrom(InputStream inputStream) {
        InputStreamContent inputStreamContent= new InputStreamContent(file.getMimeType(),inputStream)
        service.files().update(file.getId(),null,inputStreamContent).execute()
        this
    }

    @Override
    DriveService.WithFile updateContent(byte[] bytes) {
        updateFrom(new ByteArrayInputStream(bytes))
    }

    @Override
    DriveService.WithFile updateContent(String path) {
        updateContent(path.bytes)
    }

    @Override
    DriveService.WithFile rename(String newName) {
        File metadata = new File(name:newName)
        service.files().update(file.getId(),metadata, null).execute()
        this
    }

    @Override
    DriveService.WithFile permissions(
            @DelegatesTo(value=DriveService.Permissions,strategy = Closure.DELEGATE_FIRST) Closure closure) {
        PermissionsSpec spec = new PermissionsSpec(file: file, service: service)
        spec.init()
        Closure clone = closure.rehydrate(spec,closure.owner,closure.thisObject)
        clone.resolveStrategy = Closure.DELEGATE_FIRST
        clone()
        spec.execute()
        this
    }

    @Override
    DriveService.WithFile permissions(Consumer<DriveService.Permissions> consumer) {
        PermissionsSpec spec = new PermissionsSpec(file: file, service: service)
        spec.init()
        consumer.accept(spec)
        spec.execute()
        this
    }
}
