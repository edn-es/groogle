package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.ValueRange
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

@CompileStatic
class CellSpec implements SheetService.Cell{

    WithSpreadSheetSpec withSpreadSheetSpec
    String sheetName
    String cell

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    Object get() {
        def response = withSpreadSheetSpec.service.spreadsheets().values()
                .get(withSpreadSheetSpec.id,"$sheetName!$cell:$cell")
                .setMajorDimension('ROWS')
                .setValueRenderOption('FORMATTED_VALUE')
                .execute().values()
        if( response.size() != 3)
            return null
        if( !response[2].size() )
            return null
        if( !response[2][0].size() )
            return null
        response[2][0][0]
    }

    @Override
    SheetService.Cell set(Object n) {
        ValueRange valueRange = new ValueRange(range:"$sheetName!$cell:$cell",
                majorDimension:'ROWS',
                values:[[n]])
        withSpreadSheetSpec.service.spreadsheets().values()
                .update(withSpreadSheetSpec.id,"$sheetName!$cell:$cell",valueRange)
                .setValueInputOption('USER_ENTERED')
                .execute()
        this
    }

    Object asType(Class clazz) {
        if (clazz == Number) {
            Object kk = get()
            return get() as Double
        }
        if (clazz == String) {
            return get() as String
        }
        throw new RuntimeException()
    }
}
