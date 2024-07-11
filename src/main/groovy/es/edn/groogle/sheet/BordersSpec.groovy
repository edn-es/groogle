/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse
import com.google.api.services.sheets.v4.model.Border
import com.google.api.services.sheets.v4.model.GridRange
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.UpdateBordersRequest
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic

@CompileStatic
class BordersSpec implements SheetService.Borders{

    WithSpreadSheetSpec withSpreadSheetSpec
    int id
    String styleTop, styleBottom, styleLeft, styleRight, styleInnerHor, styleInnerVert

    Integer startRowIndex = null
    Integer startColIndex = null
    Integer endRowIndex = null
    Integer endColIndex = null

    @Override
    SheetService.Borders top(String style) {
        this.styleTop = style
        this
    }

    @Override
    SheetService.Borders bottom(String style) {
        this.styleBottom = style
        this
    }

    @Override
    SheetService.Borders right(String style) {
        this.styleRight = style
        this
    }

    @Override
    SheetService.Borders left(String style) {
        this.styleLeft = style
        this
    }

    @Override
    SheetService.Borders innerHorizontal(String style) {
        this.styleInnerHor = style
        this
    }

    @Override
    SheetService.Borders innerVertical(String style) {
        this.styleInnerVert = style
        this
    }

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

    protected SheetService.Borders execute(){
        UpdateBordersRequest updateBordersRequest = new UpdateBordersRequest()
        if( styleTop ){
            updateBordersRequest.setTop(new Border(style: styleTop))
        }
        if( styleBottom){
            updateBordersRequest.setBottom(new Border(style: styleBottom))
        }
        if( styleLeft ){
            updateBordersRequest.setLeft(new Border(style: styleLeft))
        }
        if( styleRight ){
            updateBordersRequest.setRight(new Border(style: styleRight))
        }
        if( styleInnerHor ){
            updateBordersRequest.setInnerHorizontal(new Border(style: styleInnerHor))
        }
        if( styleInnerVert ){
            updateBordersRequest.setInnerVertical(new Border(style: styleInnerVert))
        }
        GridRange gridRange = new GridRange(sheetId: id)
        if( startRowIndex)
            gridRange.startRowIndex = startRowIndex
        if( startColIndex)
            gridRange.startColumnIndex = startColIndex
        if( endRowIndex)
            gridRange.endRowIndex = endRowIndex
        if( endColIndex)
            gridRange.endColumnIndex = endColIndex

        updateBordersRequest.setRange(gridRange)
        Request request = new Request(updateBorders: updateBordersRequest)
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest(requests: [request])
        BatchUpdateSpreadsheetResponse response = withSpreadSheetSpec.service.spreadsheets()
                .batchUpdate(withSpreadSheetSpec.id, batchUpdateSpreadsheetRequest)
                .execute()

        this
    }
}
