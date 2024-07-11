/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle

import com.google.api.services.drive.model.File
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
interface DriveService extends Groogle.GroogleService{

    interface SaveAs{
        File getFile()

        SaveAs to(String path)
        SaveAs to(java.io.File dir)
        SaveAs to(OutputStream outputStream)

        SaveAs exportAs(String format)
        SaveAs asPlainText()
        SaveAs asZip()
        SaveAs asPdf()
    }

    interface Permissions{
        Permissions usersAsReader(String ...users)
        Permissions usersAsWriter(String ...users)
        Permissions usersAsOrganizer(String ...users)

        Permissions domainAsReader(String domain)
        Permissions domainAsWriter(String domain)
        Permissions domainAsOrganizer(String domain)

        Permissions groupsAsReader(String ...grps)
        Permissions groupsAsWriter(String ...grps)
        Permissions groupsAsOrganizer(String ...grps)

        Permissions anyoneAsReader()
        Permissions anyoneAsWriter()

        Permissions strick(boolean stricked)

    }

    interface WithFile{

        String getId()

        File getFile()

        WithFile download(final Consumer<SaveAs> consumer)
        WithFile download(final @DelegatesTo(value=SaveAs,strategy = Closure.DELEGATE_FIRST)Closure closure)

        byte[] getByteArray()
        String getText()

        WithFile updateContent(String content)
        WithFile updateContent(byte[] bytes)

        WithFile updateFromFile(String path)
        WithFile updateFromFile(java.io.File dir)
        WithFile updateFrom(InputStream outputStream)

        WithFile rename(String newName)

        WithFile moveToFolder(String folder)
        WithFile moveToFolder(File folder)

        WithFile removeFromDrive()

        WithFile permissions(final @DelegatesTo(value=Permissions,strategy = Closure.DELEGATE_FIRST)Closure closure)
        WithFile permissions(final Consumer<Permissions>consumer)
    }

    interface FindFiles {
        FindFiles findIn(String... str)

        FindFiles includeTeamDriveItems(Boolean b)

        FindFiles batchSize(int size)

        FindFiles orderBy(String orderBy)

        FindFiles nameEq(String str)

        FindFiles nameStartWith(String str)

        FindFiles parentId(String str)
    }

    interface EachFile extends FindFiles{
        EachFile eachFile(final@DelegatesTo(value=WithFile,strategy = Closure.DELEGATE_FIRST)Closure withFile)
    }

    void withFile(final String fileId, final @DelegatesTo(value = WithFile, strategy = Closure.DELEGATE_FIRST)Closure closure)

    void withFile(final String fileId, final Consumer<WithFile> consumer )

    int findFiles(final @DelegatesTo(value = EachFile, strategy = Closure.DELEGATE_FIRST)Closure closure)

    int findFiles(final Consumer<FindFiles> findFiles, final Consumer<WithFile> consumer )

    int allFiles(final Consumer<WithFile> consumer )


    interface CreateFile{
        CreateFile intoFolder(String folderId)

        CreateFile fromFile(String path)
        CreateFile fromFile(java.io.File path)

        CreateFile withName(String name)

        CreateFile fromContent(String content)
        CreateFile fromContent(ByteArrayInputStream content, String format)

        CreateFile asGoogleDoc()
    }

    WithFile upload(final String path, String folderId)

    WithFile upload(final String path)

    WithFile upload(final java.io.File content, String folderId)

    WithFile upload(final java.io.File content)

    WithFile upload(final Consumer<CreateFile>consumer)

    WithFile upload(final @DelegatesTo(value=CreateFile, strategy = Closure.DELEGATE_FIRST)Closure closure)

    WithFile findFile(final String fileId)


    interface WithFolder{
        String getId()


        WithFolder downloadTo(String path)
        WithFolder downloadTo(java.io.File dir)

        WithFolder uploadFrom(String path)
        WithFolder uploadFrom(java.io.File dir)

        WithFolder removeFromDrive()
    }

    WithFolder createFolder(final String name);

    WithFolder createFolder(final String name, final String parentId);

    WithFolder findFolder(final String folderId)

    DriveService withFolder(final String folderId, final Consumer<WithFolder> consumer)

    DriveService withFolder(final String folderId,
                            final @DelegatesTo(value=WithFolder, strategy = Closure.DELEGATE_FIRST)Closure closure)

    String createSharedFolder (final String name);
    String createSharedFolder (final String name, final String UUID);

    void deleteSharedFolder(final String name);

    Map<String,String> listSharedFolders();
}
