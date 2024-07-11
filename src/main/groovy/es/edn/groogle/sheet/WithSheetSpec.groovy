/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.sheet

import com.google.api.services.sheets.v4.model.DimensionProperties
import com.google.api.services.sheets.v4.model.DimensionRange
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.UpdateDimensionPropertiesRequest
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest
import es.edn.groogle.SheetService
import groovy.transform.CompileStatic

import java.util.function.Consumer

@CompileStatic
class WithSheetSpec implements SheetService.WithSheet{

    WithSpreadSheetSpec withSpreadSheetSpec
    int id
    String sheetName
    Sheet sheet

    @Override
    SheetService.WithSheet duplicate(String name) {
        withSpreadSheetSpec.duplicateSheet(id,name)
    }

    @Override
    SheetService.WithSheet duplicate(String name, @DelegatesTo(value = SheetService.WithSheet, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        withSpreadSheetSpec.duplicateSheet(id,name,closure)
    }

    @Override
    SheetService.WithSheet duplicate(String name, Consumer<SheetService.WithSheet> consumer) {
        withSpreadSheetSpec.duplicateSheet(id,name,consumer)
    }

    @Override
    SheetService.WithSheet hide() {
        SheetProperties sheetProperties = new SheetProperties(sheetId: id, hidden: true)
        UpdateSheetPropertiesRequest sheetRequest = new UpdateSheetPropertiesRequest(
                properties: sheetProperties,
                fields: 'hidden'
        )
        Request request = new Request(updateSheetProperties: sheetRequest)
        withSpreadSheetSpec.updateSpreadSheet(request)
        this
    }

    @Override
    SheetService.WithSheet show() {
        SheetProperties sheetProperties = new SheetProperties(sheetId: id, hidden: false)
        UpdateSheetPropertiesRequest sheetRequest = new UpdateSheetPropertiesRequest(
                properties: sheetProperties,
                fields: 'hidden'
        )
        Request request = new Request(updateSheetProperties: sheetRequest)
        withSpreadSheetSpec.updateSpreadSheet(request)
        this
    }

    @Override
    SheetService.WithSheet hideColumns(int start, int end) {
        UpdateDimensionPropertiesRequest sheetRequest = new UpdateDimensionPropertiesRequest(
                range: new DimensionRange(sheetId: id, dimension: 'COLUMNS', startIndex: start, endIndex: end),
                properties: new DimensionProperties(hiddenByUser: true),
                fields: 'hiddenByUser'
        )
        Request request = new Request(updateDimensionProperties: sheetRequest)
        withSpreadSheetSpec.updateSpreadSheet(request)
        this
    }

    @Override
    SheetService.WithSheet showColumns(int start, int end) {
        UpdateDimensionPropertiesRequest sheetRequest = new UpdateDimensionPropertiesRequest(
                range: new DimensionRange(sheetId: id, dimension: 'COLUMNS', startIndex: start, endIndex: end),
                properties: new DimensionProperties(hiddenByUser: false),
                fields: 'hiddenByUser'
        )
        Request request = new Request(updateDimensionProperties: sheetRequest)
        withSpreadSheetSpec.updateSpreadSheet(request)
        this
    }

    def getProperty(String propertyName){
        def metaProp = this.metaClass.getMetaProperty(propertyName)
        if( metaProp )
            return metaProp.getProperty(this)
        def matcher = propertyName =~ /([A-Z][a-z]*)([0-9]+)?/
        if(!matcher.find()){
            throw new MissingPropertyException(propertyName, WithSheetSpec)
        }
        return cell(propertyName).get()
    }

    void setProperty(String propertyName, Object newValue){
        def metaProp = this.metaClass.getMetaProperty(propertyName)
        if( metaProp ) {
            metaProp.setProperty(this, newValue)
            return
        }
        def matcher = propertyName =~ /([A-Z][a-z]*)([0-9]+)?/
        if(!matcher.find())
            throw new MissingPropertyException(propertyName)
        cell(propertyName).set(newValue)
    }


    @Override
    SheetService.WithSheet cell(String range, @DelegatesTo(value = SheetService.Cell, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        SheetService.Cell cellSpec = cell(range)
        if(closure){
            Closure clone = closure.rehydrate(cellSpec,closure.owner, closure.thisObject)
            clone()
        }
        this
    }

    @Override
    SheetService.WithSheet cell(String range, Consumer<SheetService.Cell> consumer) {
        SheetService.Cell cellSpec = cell(range)
        if(consumer){
            consumer.accept(cellSpec)
        }
        this
    }

    @Override
    SheetService.Cell cell(String cell) {
        new CellSpec(withSpreadSheetSpec: withSpreadSheetSpec,sheetName: sheetName, cell: cell)
    }

    @Override
    SheetService.WithSheet cell(String row, Object value) {
        cell(row).set(value)
        this
    }

    @Override
    SheetService.Range writeRange(String a1, String z99) {
        new RangeSpec(withSpreadSheetSpec: withSpreadSheetSpec,sheetName: sheetName, from:a1, to:z99)
    }

    @Override
    SheetService.WithSheet writeRange(String a1, String z99, @DelegatesTo(value = SheetService.Range, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        SheetService.Range range = writeRange(a1,z99)
        if(closure){
            Closure clone = closure.rehydrate(range, closure.owner, closure.thisObject)
            clone()
        }
        this
    }

    @Override
    SheetService.WithSheet writeRange(String a1, String z99, Consumer<SheetService.Range> consumer) {
        SheetService.Range range = writeRange(a1,z99)
        if(consumer){
            consumer.accept(range)
        }
        this
    }

    @Override
    SheetService.WithSheet append(@DelegatesTo(value = SheetService.AppendRange, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        append null, null, closure
    }

    @Override
    SheetService.WithSheet append(String a1, String z99,
                                  @DelegatesTo(value = SheetService.AppendRange, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        AppendRangeSpec batchRangeSpec = new AppendRangeSpec(withSpreadSheetSpec: withSpreadSheetSpec,
                sheetName: sheetName,
                from:a1,
                to:z99)
        Closure clone = closure.rehydrate(batchRangeSpec,closure.owner, closure.thisObject)
        clone()
        batchRangeSpec.execute()
        this
    }

    @Override
    SheetService.WithSheet append(Consumer<SheetService.AppendRange> consumer) {
        append null, null, consumer
    }

    @Override
    SheetService.WithSheet append(String a1, String z99, Consumer<SheetService.AppendRange> consumer) {
        AppendRangeSpec batchRangeSpec = new AppendRangeSpec(withSpreadSheetSpec: withSpreadSheetSpec,
                sheetName: sheetName,
                from:a1,
                to:z99)
        consumer.accept(batchRangeSpec)
        batchRangeSpec.execute()
        this
    }

    @Override
    SheetService.WithSheet fromDataSource(@DelegatesTo(value = SheetService.AppendDataSource, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        AppendDataSourceSpec spec = new AppendDataSourceSpec(withSheetSpec: this)
        Closure clone = closure.rehydrate(spec, closure.owner, closure.thisObject)
        clone()
        spec.execute()
        this
    }

    @Override
    SheetService.WithSheet fromDataSource(Consumer<SheetService.AppendDataSource> consumer) {
        AppendDataSourceSpec spec = new AppendDataSourceSpec(withSheetSpec: this)
        consumer.accept(spec)
        spec.execute()
        this
    }


    @Override
    SheetService.WithSheet borders(@DelegatesTo(value= SheetService.Borders, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        BordersSpec spec = new BordersSpec(withSpreadSheetSpec: withSpreadSheetSpec, id: id)
        Closure clone = closure.rehydrate(spec, closure.thisObject, closure.owner)
        clone()
        spec.execute()
        this
    }

    @Override
    SheetService.WithSheet merge(@DelegatesTo(value= SheetService.Merge, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        UnmergeSpec spec = new UnmergeSpec(withSpreadSheetSpec: withSpreadSheetSpec, id: id, toMerge: true)
        Closure clone = closure.rehydrate(spec, closure.thisObject, closure.owner)
        clone()
        spec.execute()
        this
    }

    @Override
    SheetService.WithSheet unmerge(@DelegatesTo(value= SheetService.Unmerge, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        UnmergeSpec spec = new UnmergeSpec(withSpreadSheetSpec: withSpreadSheetSpec, id: id, toMerge: false)
        Closure clone = closure.rehydrate(spec, closure.thisObject, closure.owner)
        clone()
        spec.execute()
        this
    }

    @Override
    SheetService.WithSheet lock(@DelegatesTo(value= SheetService.Lock, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        LockSpec spec = new LockSpec(withSheetSpec: this, id: id)
        Closure clone = closure.rehydrate(spec, closure.thisObject, closure.owner)
        clone()
        spec.execute()
        this
    }

}
