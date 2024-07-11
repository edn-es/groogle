/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet

import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter
import es.edn.groogle.SheetService
import es.edn.groogle.core.InternalService
import groovy.transform.CompileStatic

import javax.annotation.Nullable
import java.util.function.Consumer

@CompileStatic
class GroovySheetService implements InternalService, SheetService{

    Sheets service

    @Override
    void configure(JsonFactory jsonFactory, HttpTransport httpTransport, Credentials credentials, String applicationName) {
        def http = new HttpCredentialsAdapter(credentials)
        service = new Sheets.Builder(httpTransport, jsonFactory, http).setApplicationName(applicationName).build()
    }

    @Deprecated
    @Override
    WithSpreadSheet findSpreedSheet(String id) {
        findSpreadSheet(id)
    }

    WithSpreadSheet findSpreadSheet(String id) {
        try {
            Spreadsheet spreadsheet = service.spreadsheets().get(id).setFields("*").execute()
            WithSpreadSheetSpec withSheet = new WithSpreadSheetSpec(service:service,spreadsheet:spreadsheet)
            withSheet
        }catch( Exception e){
            null
        }
    }

    @Override
    WithSpreadSheet createSpreadSheet(String name) {
        SpreadsheetProperties properties = new SpreadsheetProperties(title: name)
        Spreadsheet spreadsheet = new Spreadsheet(properties:properties)
        spreadsheet = service.spreadsheets().create(spreadsheet).setFields("*").execute()
        WithSpreadSheetSpec withSheet = new WithSpreadSheetSpec(service:service,spreadsheet:spreadsheet)
        withSheet
    }

    @Override
    WithSpreadSheet createSpreadSheet(String name, Consumer<WithSpreadSheet> consumer) {
        WithSpreadSheet withSpreadSheet = createSpreadSheet(name)
        if(consumer) {
            consumer.accept(withSpreadSheet)
        }
        withSpreadSheet
    }

    @Override
    WithSpreadSheet createSpreadSheet(String name,
                                      @DelegatesTo(value = WithSpreadSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        WithSpreadSheet withSpreadSheet = createSpreadSheet(name)
        if(closure) {
            Closure clone = closure.rehydrate(withSpreadSheet, closure.owner, closure.thisObject)
            clone.resolveStrategy = Closure.DELEGATE_FIRST
            clone()
        }
        withSpreadSheet
    }

    @Override
    WithSpreadSheet withSpreadSheet(String id, Consumer<WithSpreadSheet> consumer) {
        WithSpreadSheet withSpreadSheet = findSpreadSheet(id)
        assert withSpreadSheet
        consumer.accept(withSpreadSheet)
        withSpreadSheet
    }

    @Override
    WithSpreadSheet withSpreadSheet(String id,
                                 @DelegatesTo(value = WithSpreadSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        WithSpreadSheet withSpreadSheet = findSpreadSheet(id)
        assert withSpreadSheet
        Closure clone = closure.rehydrate(withSpreadSheet,closure.owner,closure.thisObject)
        clone.resolveStrategy = Closure.DELEGATE_FIRST
        clone()
        withSpreadSheet
    }

    @Override
    WithSpreadSheet findOrCreateSpreadSheet(String id, String name) {
        findOrCreateSpreadSheet(id, name, null)
    }

    @Override
    WithSpreadSheet findOrCreateSpreadSheet(String id, String name, @Nullable @DelegatesTo(value = WithSpreadSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        WithSpreadSheet withSpreadSheet = findSpreadSheet(id) ?: createSpreadSheet(name)
        assert withSpreadSheet
        if( closure ){
            Closure clone = closure.rehydrate(withSpreadSheet,closure.owner,closure.thisObject)
            clone.resolveStrategy = Closure.DELEGATE_FIRST
            clone()
        }
        withSpreadSheet
    }
}
