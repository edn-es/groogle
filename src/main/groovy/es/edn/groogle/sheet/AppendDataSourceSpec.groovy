package es.edn.groogle.sheet

import es.edn.groogle.SheetService
import groovy.sql.Sql
import groovy.transform.CompileStatic

import javax.sql.DataSource

@CompileStatic
class AppendDataSourceSpec implements SheetService.AppendDataSource{

    WithSheetSpec withSheetSpec
    private DataSource dataSource
    private String query
    private String range

    @Override
    SheetService.AppendDataSource dataSource(DataSource dataSource) {
        this.dataSource = dataSource
        this
    }

    @Override
    SheetService.AppendDataSource query(String query) {
        this.query = query
        this
    }

    @Override
    SheetService.AppendDataSource range(String range) {
        this.range = range
        this
    }

    void execute(){
        Sql sql = new Sql(dataSource)
        withSheetSpec.append {
            sql.eachRow query, { row ->
                List values = []
                for(int i =0; i<row.metaData.columnCount; i++){
                    values.add row[row.metaData.getColumnName(i+1)]
                }
                insert values
            }
        }

    }
}
