/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet


import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.UpdateBordersRequest
import com.google.api.services.sheets.v4.model.ValueRange
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic

@CompileStatic
class RangeSpec implements SheetService.Range{

    WithSpreadSheetSpec withSpreadSheetSpec
    String sheetName
    String from
    String to

    @Override
    SheetService.Range clear() {
        ClearValuesRequest request = new ClearValuesRequest()
        withSpreadSheetSpec.service.spreadsheets().values().clear(withSpreadSheetSpec.id,
                "$sheetName!$from:$to".toString(),request)
                .execute()
        this
    }

    @Override
    List<List<Object>> get() {
        withSpreadSheetSpec.service.spreadsheets()
                .values().get(withSpreadSheetSpec.id,"$sheetName!$from:$to")
                .setMajorDimension('ROWS')
                .setValueRenderOption('FORMULA')
                .execute().getValues()
    }

    @Override
    List<List<Object>> getValues() {
        withSpreadSheetSpec.service.spreadsheets()
                .values().get(withSpreadSheetSpec.id,"$sheetName!$from:$to")
                .setMajorDimension('ROWS')
                .setValueRenderOption('FORMATTED_VALUE')
                .execute().getValues()
    }

    @Override
    SheetService.Range set(List<List<Object>> values) {
        ValueRange valueRange = new ValueRange(range:"$sheetName!$from:$to",
                majorDimension:'ROWS',
                values:values)
        withSpreadSheetSpec.service.spreadsheets().values()
                .update(withSpreadSheetSpec.id,"$sheetName!$from:$to",valueRange)
                .setValueInputOption('USER_ENTERED')
                .execute()
        this

    }

}
