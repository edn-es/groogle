/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.GridRange
import es.edn.groogle.SheetService

class CoordinatesSpec implements SheetService.Coordinates{
    Integer id
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
}
