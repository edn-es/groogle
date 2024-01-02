package es.edn.groogle

import groovy.transform.CompileStatic

import javax.sql.DataSource
import java.util.function.Consumer

@CompileStatic
interface SheetService extends Groogle.GroogleService{

    interface Cell{
        Cell set(Object n)
        Object get()
    }

    interface Range{
        Range clear()

        List<List<Object>> get()

        Range set(List<List<Object>> values)

    }

    interface AppendRange {

        AppendRange insert(Object[] values)

        AppendRange insert(List<Object> values)

    }

    interface AppendDataSource{

        AppendDataSource dataSource(DataSource dataSource)
        AppendDataSource query(String query)
        AppendDataSource range(String range)
    }

    interface Coordinates{
        Coordinates startRowIndex(int i)
        Coordinates startColIndex(int i)
        Coordinates endRowIndex(int i)
        Coordinates endColIndex(int i)
    }

    interface Borders extends Coordinates{
        Borders top(String style)
        Borders bottom(String style)
        Borders right(String style)
        Borders left(String style)
        Borders innerHorizontal(String style)
        Borders innerVertical(String style)
    }

    interface Unmerge extends Coordinates{
    }

    interface WithSheet{
        int getId()

        WithSheet hide()

        WithSheet show()

        WithSheet hideColumns(int start, int end)

        WithSheet showColumns(int start, int end)

        WithSheet duplicate(String name)

        WithSheet duplicate( String name,
                               @DelegatesTo(value = WithSheet, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet duplicate( String name, Consumer<WithSheet> consumer)

        Cell cell(String cell)

        WithSheet cell(String cell, Object value)

        WithSheet cell(String cell,
                      @DelegatesTo(value = Cell, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet cell(String row, Consumer<Cell>consumer)

        Range writeRange(String a1, String z99)

        WithSheet writeRange(String a1, String z99,
                             @DelegatesTo(value = Range, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet writeRange(String a1, String z99, Consumer<Range>consumer)

        WithSheet append(Consumer<AppendRange>consumer)

        WithSheet append(String a1, String z99, Consumer<AppendRange>consumer)

        WithSheet append(@DelegatesTo(value = AppendRange, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet append(String a1, String z99,
                         @DelegatesTo(value = AppendRange, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet fromDataSource(@DelegatesTo(value = AppendDataSource, strategy = Closure.DELEGATE_FIRST)Closure closure )

        WithSheet fromDataSource(Consumer<AppendDataSource> consumer)

        WithSheet borders(@DelegatesTo(value=Borders, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet unmerge(@DelegatesTo(value=Unmerge, strategy = Closure.DELEGATE_FIRST)Closure closure)
    }

    interface WithSpreadSheet {
        String getId()

        WithSheet findSheet( String name)

        WithSheet createSheet( String name)

        WithSheet createSheet( String name,
                               @DelegatesTo(value = WithSheet, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet createSheet( String name, Consumer<WithSheet> consumer)

        WithSpreadSheet removeSheet(WithSheet withSheet)

        WithSpreadSheet removeSheet(int id)

        Map<Integer,String> getSheets()

        WithSheet withSheet( String name,
                             @DelegatesTo(value = WithSheet, strategy = Closure.DELEGATE_FIRST)Closure closure)

        WithSheet withSheet( String name,
                             Consumer<WithSheet> consumer)
    }

    WithSpreadSheet createSpreadSheet(final String name)

    @Deprecated
    WithSpreadSheet findSpreedSheet(final String id)

    WithSpreadSheet findSpreadSheet(final String id)

    WithSpreadSheet createSpreadSheet(final String name,
                                   @DelegatesTo(value = WithSpreadSheet, strategy = Closure.DELEGATE_FIRST)Closure closure)

    WithSpreadSheet createSpreadSheet(final String name,
                                   Consumer<WithSpreadSheet> consumer)

    WithSpreadSheet findOrCreateSpreadSheet(final String id, final String name)

    WithSpreadSheet findOrCreateSpreadSheet(final String id, final String name,
                                            @DelegatesTo(value = WithSpreadSheet, strategy = Closure.DELEGATE_FIRST)Closure closure)

    WithSpreadSheet withSpreadSheet(final String id,
                                 @DelegatesTo(value = WithSpreadSheet, strategy = Closure.DELEGATE_FIRST)Closure closure)

    WithSpreadSheet withSpreadSheet(final String id, Consumer<WithSpreadSheet> consumer)
}
