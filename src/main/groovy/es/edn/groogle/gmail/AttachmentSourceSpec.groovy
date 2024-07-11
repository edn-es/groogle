/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.gmail

import es.edn.groogle.GmailService
import groovy.io.FileType
import groovy.transform.CompileStatic

@CompileStatic
class AttachmentSourceSpec implements GmailService.AttachmentSource{

    SendMailSpec sendMailSpec
    String dir
    String criteria
    List<String> files = []

    @Override
    GmailService.AttachmentSource dir(String dirPath, String criteria = null) {
        this.dir = dirPath
        this.criteria = criteria
        this
    }

    @Override
    GmailService.AttachmentSource file(String file) {
        this.files.add file
        this
    }

    @Override
    GmailService.AttachmentSource file(File file) {
        this.file(file.absolutePath)
    }

    List<File> attachments(){
        List<File> attachments = []
        files.each{
            attachments.add new File(it)
        }

        if (criteria){
            new File(dir).eachFileMatch(FileType.ANY, ~/${criteria}/) { attachment->
                attachments.add attachment
            }
        }
        if (dir){
            new File(dir).eachFile{ attachment->
                attachments.add attachment
            }
        }

        attachments
    }
}
