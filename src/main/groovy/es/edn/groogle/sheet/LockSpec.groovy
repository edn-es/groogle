package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.*
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic

@CompileStatic
class LockSpec implements SheetService.Lock{

    WithSheetSpec withSheetSpec
    Integer id
    String name

    Integer startRowIndex = null
    Integer startColIndex = null
    Integer endRowIndex = null
    Integer endColIndex = null
    List<GridRange>excludeList = []
    List<String> allowUsers = []

    @Override
    SheetService.Coordinates startRowIndex(int i) {
        this.startRowIndex = i
        this
    }

    @Override
    SheetService.Coordinates startColIndex(int i) {
        this.startColIndex = i
        this
    }

    @Override
    SheetService.Coordinates endRowIndex(int i) {
        this.endRowIndex = i
        this
    }

    @Override
    SheetService.Coordinates endColIndex(int i) {
        this.endColIndex = i
        this
    }

    @Override
    SheetService.Lock name(String name) {
        this.name = name
        this
    }

    @Override
    SheetService.Lock allowUser(String name) {
        this.allowUsers.add name
        return this
    }

    SheetService.Lock except(@DelegatesTo(SheetService.Coordinates) Closure closure){
        CoordinatesSpec coordinatesSpec = new CoordinatesSpec(id:id)
        Closure clone = closure.rehydrate(coordinatesSpec, closure.owner, closure.thisObject)
        clone.resolveStrategy = Closure.DELEGATE_FIRST
        clone()
        this.excludeList.add coordinatesSpec.gridRange
        this
    }

    GridRange getGridRange(){
        GridRange gridRange = new GridRange()
        if( id )
            gridRange.sheetId = id
        if( startRowIndex)
            gridRange.startRowIndex = startRowIndex
        if( startColIndex)
            gridRange.startColumnIndex = startColIndex
        if( endRowIndex)
            gridRange.endRowIndex = endRowIndex
        if( endColIndex)
            gridRange.endColumnIndex = endColIndex
        gridRange
    }

    protected LockSpec execute(){
        AddProtectedRangeRequest addProtectedRangeRequest = new AddProtectedRangeRequest(
                protectedRange: new ProtectedRange(
                        range: gridRange,
                        description: "$name",
                        unprotectedRanges: excludeList,
                        editors: new Editors(users: allowUsers)
                )
        )

        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest(requests: [
                //new Request(addNamedRange: addNamedRangeRequest),
                new Request(addProtectedRange: addProtectedRangeRequest)
        ])
        withSheetSpec.withSpreadSheetSpec.service.spreadsheets()
                .batchUpdate(withSheetSpec.withSpreadSheetSpec.id, batchUpdateSpreadsheetRequest)
                .execute()

        this
    }
}
