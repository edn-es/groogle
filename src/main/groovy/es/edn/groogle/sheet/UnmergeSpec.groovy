/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.*
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic

@CompileStatic
class UnmergeSpec implements SheetService.Unmerge{

    WithSpreadSheetSpec withSpreadSheetSpec
    int id
    boolean toMerge

    Integer startRowIndex = null
    Integer startColIndex = null
    Integer endRowIndex = null
    Integer endColIndex = null

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

    protected SheetService.Unmerge execute(){
        GridRange gridRange = new GridRange(sheetId: id)
        if( startRowIndex)
            gridRange.startRowIndex = startRowIndex
        if( startColIndex)
            gridRange.startColumnIndex = startColIndex
        if( endRowIndex)
            gridRange.endRowIndex = endRowIndex
        if( endColIndex)
            gridRange.endColumnIndex = endColIndex

        Request request
        if( toMerge ){
            MergeCellsRequest mergeCellsRequest = new MergeCellsRequest(range:gridRange)
            request = new Request(mergeCells: mergeCellsRequest)
        }else{
            UnmergeCellsRequest unmergeCellsRequest = new UnmergeCellsRequest(range:gridRange)
            request = new Request(unmergeCells: unmergeCellsRequest)
        }
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest(requests: [request])
        BatchUpdateSpreadsheetResponse response = withSpreadSheetSpec.service.spreadsheets()
                .batchUpdate(withSpreadSheetSpec.id, batchUpdateSpreadsheetRequest)
                .execute()
        this
    }
}
