package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import es.edn.groogle.SheetService
import es.edn.groogle.SheetService.WithSheet
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
class WithSpreadSheetSpec implements SheetService.WithSpreadSheet{

    Sheets service
    Spreadsheet spreadsheet

    @Override
    String getId(){
        spreadsheet.spreadsheetId
    }

    @Override
    Map<Integer,String> getSheets() {
        spreadsheet.getSheets().inject([:], { map, sheet->
            map[sheet.getProperties().sheetId] = sheet.getProperties().title
            map
        }) as Map<Integer, String>
    }

    void updateSpreadSheet( Request request ) {
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest(requests: [request])
        BatchUpdateSpreadsheetResponse response = service.spreadsheets()
                .batchUpdate(id, batchUpdateSpreadsheetRequest)
                .execute()
        spreadsheet = service.spreadsheets().get(id).execute()
    }

    @Override
    WithSheet findSheet(String name) {
        Sheet sheet = spreadsheet.getSheets().find{ it.getProperties().title == name}
        assert sheet
        new WithSheetSpec(withSpreadSheetSpec: this, id:sheet.getProperties().getSheetId(), sheetName: name, sheet:sheet)
    }

    @Override
    SheetService.WithSheet createSheet(String name) {
        AddSheetRequest addSheetRequest = new AddSheetRequest(properties: new SheetProperties(title:name))
        Request request = new Request(addSheet: addSheetRequest)
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest(requests: [request])
        BatchUpdateSpreadsheetResponse response = service.spreadsheets()
                .batchUpdate(id, batchUpdateSpreadsheetRequest)
                .execute()
        int sheetId = response.getReplies()[0].getAddSheet().getProperties().getSheetId()
        spreadsheet = service.spreadsheets().get(id).execute()
        new WithSheetSpec(withSpreadSheetSpec: this, id:sheetId, sheetName: name)
    }

    @Override
    WithSheet createSheet(String name, @DelegatesTo(value = WithSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        WithSheet withSheet = createSheet(name)
        if(closure) {
            Closure clone = closure.rehydrate(withSheet, closure.owner, closure.thisObject)
            clone()
        }
        withSheet
    }

    @Override
    WithSheet createSheet(String name, Consumer<WithSheet> consumer) {
        WithSheet withSheet = createSheet(name)
        if(consumer) {
            consumer.accept(withSheet)
        }
        withSheet
    }

    @Override
    WithSheet withSheet(String name, @DelegatesTo(value = WithSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        WithSheet withSheet = findSheet(name)
        if(closure) {
            Closure clone = closure.rehydrate(withSheet, closure.owner, closure.thisObject)
            clone()
        }
        withSheet
    }

    @Override
    WithSheet withSheet(String name, Consumer<WithSheet> consumer) {
        WithSheet withSheet = findSheet(name)
        if(consumer) {
            consumer.accept(withSheet)
        }
        withSheet
    }

    @Override
    WithSpreadSheetSpec removeSheet(WithSheet withSheet){
        removeSheet(withSheet.id)
    }

    @Override
    WithSpreadSheetSpec removeSheet(int sheetId){
        DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest(sheetId: sheetId)
        updateSpreadSheet(new Request(deleteSheet: deleteSheetRequest))
        this
    }

    SheetService.WithSheet duplicateSheet(int sourceId, String name) {
        DuplicateSheetRequest duplicateSheetRequest = new DuplicateSheetRequest(newSheetName: name, sourceSheetId: sourceId)
        Request request = new Request(duplicateSheet: duplicateSheetRequest)
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest(requests: [request])
        BatchUpdateSpreadsheetResponse response = service.spreadsheets()
                .batchUpdate(id, batchUpdateSpreadsheetRequest)
                .execute()
        int duplicateId = response.getReplies()[0].getDuplicateSheet().getProperties().getSheetId()
        spreadsheet = service.spreadsheets().get(id).execute()
        new WithSheetSpec(withSpreadSheetSpec: this, id:duplicateId, sheetName: name)
    }

    SheetService.WithSheet duplicateSheet(int sourceId,
                                     String name,
                                     @DelegatesTo(value = SheetService.WithSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        SheetService.WithSheet withSheet = duplicateSheet(sourceId, name)
        if( closure ){
            Closure clone = closure.rehydrate(withSheet,closure.owner,closure.thisObject)
            clone()
        }
        withSheet
    }

    SheetService.WithSheet duplicateSheet(int sourceId,
                                     String name,
                                     Consumer<WithSheet> consumer) {
        SheetService.WithSheet withSheet = duplicateSheet(sourceId, name)
        if( consumer ){
            consumer.accept(withSheet)
        }
        withSheet
    }
}
