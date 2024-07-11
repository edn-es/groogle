/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.drive


import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.DriveList
import com.google.api.services.drive.model.File
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter
import es.edn.groogle.DriveService
import es.edn.groogle.core.InternalService
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
class GroovyDriveService implements InternalService, DriveService, GoogleMimeTypes{

    Drive service

    @Override
    void configure(JsonFactory jsonFactory, HttpTransport httpTransport, Credentials credentials, String applicationName ) {
        def http = new HttpCredentialsAdapter(credentials)
        this.service = new Drive.Builder(httpTransport, jsonFactory, http)
                .setApplicationName(applicationName)
                .build();
    }

    @Override
    WithFile findFile(String fileId) {
        File file = service.files().get(fileId).setSupportsAllDrives(true).setFields("*").execute()
        if(!file)
            return null
        new WithFileSpec(service: service, file:file)
    }

    void withFile(final String fileId, final @DelegatesTo(value = WithFile, strategy = Closure.DELEGATE_FIRST)Closure closure){
        Consumer<DriveService.WithFile> consumer = new Consumer<DriveService.WithFile>() {
            @Override
            void accept(DriveService.WithFile withFile) {
                final Closure clone = closure.rehydrate(withFile,closure.owner,closure.thisObject)
                clone.resolveStrategy=Closure.DELEGATE_FIRST
                clone()
            }
        }
        withFile(fileId, consumer)
    }

    void withFile(final String fileId, final Consumer<DriveService.WithFile> consumer ){
        File file = service.files().get(fileId).setSupportsAllDrives(true).setFields("*").execute()
        WithFileSpec spec = new WithFileSpec(file: file, service: service)
        consumer.accept(spec)
    }


    @Override
    int findFiles(@DelegatesTo(value = EachFile, strategy = Closure.DELEGATE_FIRST)Closure closure) {
        FindFilesSpec spec = new FindFilesSpec(service: service)
        Closure clone = closure.clone() as Closure
        clone.delegate=spec
        clone()
        spec.execute()
    }

    @Override
    int findFiles(final Consumer<FindFiles> findFiles, final Consumer<WithFile> consumer) {
        FindFilesSpec spec = new FindFilesSpec(service: service,withFileConsumer: consumer)
        findFiles.accept(spec)
        spec.execute()
    }

    @Override
    int allFiles(Consumer<WithFile> consumer) {
        FindFilesSpec spec = new FindFilesSpec(service: service,withFileConsumer: consumer)
        spec.execute()
    }

    @Override
    WithFile upload(java.io.File content) {
        upload(content,"root")
    }

    @Override
    WithFile upload(java.io.File content, String folderId) {
        upload {
            fromFile content
            intoFolder folderId
        }
    }

    @Override
    WithFile upload(String path) {
        upload(path,"root")
    }

    @Override
    WithFile upload(String path, String folderId) {
        upload(new java.io.File(path), folderId)
    }

    @Override
    WithFile upload(@DelegatesTo(value=CreateFile, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        CreateFileSpec spec = new CreateFileSpec(service: service)
        Closure clone = closure.rehydrate(spec,closure.owner,closure.thisObject)
        clone.resolveStrategy=Closure.DELEGATE_FIRST
        clone()
        File file = spec.execute()
        if(!file)
            return null
        new WithFileSpec(service: service, file:file)
    }

    @Override
    WithFile upload(Consumer<CreateFile> consumer) {
        CreateFileSpec spec = new CreateFileSpec(service: service)
        consumer.accept(spec)
        File file = spec.execute()
        if(!file)
            return null
        new WithFileSpec(service: service, file:file)
    }

    @Override
    WithFolder createFolder(String name) {
        createFolder(name, "root")
    }

    @Override
    WithFolder createFolder(String name, String parentId) {
        File metadata = new File(name: name, parents: [parentId], mimeType: folderMimeType)
        File file = service.files().create(metadata).setFields("id,parents").execute()
        if(!file)
            return null
        new WithFolderSpec(folder: file, service: service, groovyDriveService: this)
    }

    @Override
    WithFolder findFolder(String folderId) {
        WithFile withFile= findFile(folderId)
        new WithFolderSpec(folder: withFile.file, service: service, groovyDriveService: this)
    }

    @Override
    DriveService withFolder(String folderId, @DelegatesTo(value=WithFolder, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        WithFolder withFolder = findFolder(folderId)
        assert withFolder
        Closure clone = closure.rehydrate(withFolder, closure.owner, closure.thisObject)
        clone()
        this
    }

    @Override
    DriveService withFolder(String folderId, Consumer<WithFolder> consumer) {
        WithFolder withFolder = findFolder(folderId)
        assert withFolder
        consumer.accept(withFolder)
        this
    }

    @Override
    String createSharedFolder(String name) {
        String uuid = UUID.randomUUID().toString()
        createSharedFolder(name, uuid)
    }

    @Override
    String createSharedFolder(String name, String UUID) {
        com.google.api.services.drive.model.Drive metadata = new com.google.api.services.drive.model.Drive(
                name: name
        )
        service.drives().create(UUID, metadata).execute().id
    }

    @Override
    void deleteSharedFolder(String name) {
        service.drives().delete(name).execute()
    }

    @Override
    Map<String, String> listSharedFolders() {
        DriveList list = service.drives().list().execute()
        Map<String, String> ret = [:]
        list.getDrives().inject(ret, { Map<String, String> map, com.google.api.services.drive.model.Drive drive->
            map[drive.getId()] = drive.getName()
            map
        })
        ret
    }
}
