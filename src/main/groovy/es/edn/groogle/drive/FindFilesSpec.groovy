/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import es.edn.groogle.DriveService
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
class FindFilesSpec implements DriveService.EachFile{

    Drive service

    private List<String>corpora
    private Boolean includeTeamDriveItems = null
    private int batchSize = 20
    private String orderBy
    private String nextPageToken
    private List filters = []

    @Override
    DriveService.FindFiles findIn(String... str) {
        corpora.clear()
        corpora.addAll(str)
        this
    }

    @Override
    DriveService.FindFiles  includeTeamDriveItems(Boolean b) {
        includeTeamDriveItems = b
        this
    }

    @Override
    DriveService.FindFiles  batchSize(int size) {
        batchSize=size
        this
    }

    @Override
    DriveService.FindFiles  orderBy(String orderBy) {
        this.orderBy = orderBy
        this
    }

    @Override
    DriveService.FindFiles  nameEq(String str) {
        filters.add "name = '$str'"
        this
    }

    @Override
    DriveService.FindFiles nameContains(String str) {
        filters.add "name contains '$str'"
        this
    }

    @Override
    DriveService.FindFiles  parentId(String str) {
        filters.add "parents in '$str'"
        this
    }

    Consumer<DriveService.WithFile> withFileConsumer

    @Override
    DriveService.EachFile  eachFile(final @DelegatesTo(value=DriveService.WithFile,strategy = Closure.DELEGATE_FIRST) Closure closure) {
        final Closure clone = closure.clone() as Closure
        withFileConsumer = new Consumer<DriveService.WithFile>(){
            @Override
            void accept(DriveService.WithFile withFile) {
                clone.delegate = withFile
                clone.resolveStrategy = Closure.DELEGATE_FIRST
                clone()
            }
        }
        this
    }

    protected int execute(){
        if( !withFileConsumer )
            return -1

        Drive.Files.List request = service.files().list()
                .setPageSize(batchSize)
                .setFields('nextPageToken,files(*)')
        if(corpora)
            request.setCorpora(corpora.join(','))
        if(includeTeamDriveItems != null)
            request.includeTeamDriveItems=includeTeamDriveItems
        if(orderBy)
            request.orderBy=orderBy
        if( filters.size())
            request.q = filters.join(' and ')

        int count=0
        while( true ){
            def response = request.execute()
            response.files.each{ File file->
                WithFileSpec withFileSpec = new WithFileSpec(service: service, file: file)
                withFileConsumer.accept(withFileSpec)
                count++
            }
            if( !response.nextPagetToken )
                break
            request.pageToken = response.nextPagetToken
        }
        count
    }
}
