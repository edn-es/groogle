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
import groovy.transform.CompileStatic
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.ListLabelsResponse

import java.text.SimpleDateFormat


@CompileStatic
class FilterMessageSpec implements GmailService.FilterMessage{

    String only
    Gmail service
    String userId = "me"


    List<String> filter = []
    List<String> labelNames = []

    @Override
    GmailService.FilterMessage from(String from) {
        filter.add "from:$from".toString()
        this
    }

    @Override
    GmailService.FilterMessage to(String to) {
        filter.add "to:$to".toString()
        this
    }

    @Override
    GmailService.FilterMessage unRead(boolean unRead){
        if (unRead)
            filter.add "is:unread"
        this
    }

    @Override
    GmailService.FilterMessage labelNames(String labelIds){
        this.labelNames = labelIds.split(",") as List
        this
    }
    @Override
    GmailService.FilterMessage subject(String subject){
        filter.add "subject:$subject".toString()
        this
    }
    @Override
    GmailService.FilterMessage body(String body){
        filter.add "\"$body\"".toString()
        this
    }

    @Override
    GmailService.FilterMessage after(String dayAfter){
        filter.add "after: $dayAfter".toString()
        this
    }
    @Override
    GmailService.FilterMessage before(String dayBefore){
        filter.add "before: $dayBefore".toString()
        this
    }

    @Override
    GmailService.FilterMessage after(Date dayAfter){
        filter.add "after: ${new SimpleDateFormat('yyyy/MM/dd').format(dayAfter)}".toString()
        this
    }
    @Override
    GmailService.FilterMessage before(Date dayBefore){
        filter.add "after: ${new SimpleDateFormat('yyyy/MM/dd').format(dayBefore)}".toString()
        this
    }

    @Override
    GmailService.FilterMessage inChat(boolean inChat = false){
        if (inChat){
            filter.add "-in:chats"
        }
        this
    }
    @Override
    GmailService.FilterMessage larger(String larger){
        filter.add "larger: ${larger}".toString()
        this
    }
    @Override
    GmailService.FilterMessage smaller(String smaller){
        filter.add "smaller: ${smaller}".toString()
        this
    }

    @Override
    GmailService.FilterMessage hasAttachments(boolean hasAttachments){
        if (hasAttachments)
            filter.add "has:attachment"
        this
    }

    List<Label> allLabels()throws IOException {
        ListLabelsResponse listResponse = service.users().labels().list(userId).execute()
        List<Label> labels = listResponse.getLabels().findAll{labelNames.contains(it.name)}
        labels
    }

    String query(){
        filter.join(' ')
    }

    String pageToken

    boolean hasMoreMessages(){
        pageToken == null || pageToken != "-1"
    }

    List<Message> nextMessages(){
        Gmail.Users.Messages.List request = initializeRequest(query())
        ListMessagesResponse response = request.execute()
        List<Message> messages = response.getMessages()
        if( response.nextPageToken)
            pageToken = response.nextPageToken
        else
            pageToken = "-1"
        messages
    }

    Gmail.Users.Messages.List initializeRequest(String query){

        Gmail.Users.Messages.List request = service.users().messages().list(userId)

        request.setQ(query)

        if (labelNames){
            List<Label> labels = allLabels()
            request.setLabelIds(labels*.id as List<String>)
        }

        if( pageToken && pageToken != "-1")
            request.setPageToken(pageToken)

        request
    }

}
