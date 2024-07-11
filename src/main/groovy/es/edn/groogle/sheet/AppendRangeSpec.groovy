/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.ValueRange
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic

@CompileStatic
class AppendRangeSpec implements SheetService.AppendRange{

    WithSpreadSheetSpec withSpreadSheetSpec
    String sheetName
    String from
    String to
    int collate = 100

    List<List<Object>> values = []

    @Override
    SheetService.AppendRange insert(Object[] values) {
        this.insert(values as List<Object>)
    }

    @Override
    SheetService.AppendRange insert(List<Object> values) {
        this.values.add(values)
        this
    }

    void execute(){
        values.collate(collate).each{ items->
            ValueRange valueRange = new ValueRange(values:items)
            String range = sheetName
            if( from != null )
                range = "$range!$from"
            if( to  != null)
                range = "$range:$to"
            if( from || to )
                valueRange.range = range

            withSpreadSheetSpec.service.spreadsheets().values()
                    .append(withSpreadSheetSpec.id,range,valueRange)
                    .setValueInputOption('USER_ENTERED')
                    .execute()
        }
        this

    }
}
