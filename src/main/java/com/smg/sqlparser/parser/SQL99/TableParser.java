package com.smg.sqlparser.parser.SQL99;

import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import com.smg.sqlparser.domain.sql.Column;
import com.smg.sqlparser.domain.sql.Schema;
import com.smg.sqlparser.domain.sql.Table;
import com.smg.sqlparser.domain.sql.constraints.ForeignKey;
import com.smg.sqlparser.domain.sql.constraints.PrimaryKey;

import java.util.Collections;
import java.util.List;

/**
 * Provides functionality to parse a JSqlParser {@link CreateTable} statement into a domain {@link Table} object.
 */
public class TableParser {
    
    /** Parses a table to entities **/
    public static Table parseTableWithoutForeignKeys(CreateTable createTable) {
        String tableName = createTable.getTable().getName();
        Table table = new Table(tableName);
        
        if (createTable.getColumnDefinitions() != null) {
            createTable.getColumnDefinitions().forEach(cd -> {
                Column column = ColumnParser.parse(cd);
                if (column.isPrimaryKey()) addPrimaryKey(table, column);
                table.addColumn(column);
            });
        }
        return table;
    }
    
    
    /** Set primary key to table **/
    private static void addPrimaryKey(Table table, Column column) {
        PrimaryKey pk = new PrimaryKey();
        pk.addColumn(column);
        table.setPrimaryKey(pk);
    }
    
    /** Add foreign keys to table **/
    public static void addForeignKeys(Table table, CreateTable createTable, Schema schema) {
        List<ForeignKeyIndex> foreignKeys = getForeignIndexes(createTable.getIndexes());
        
        foreignKeys.forEach(fkStatement -> {
            String targetTableName = fkStatement.getTable().getName();
            Table targetTable = schema.getTables().get(targetTableName);
            
            List<String> sourceColumnNames = fkStatement.getColumns().stream()
                .map(Index.ColumnParams::getColumnName)
                .toList();
            List<String> targetColumnNames = fkStatement.getReferencedColumnNames();
            
            if (targetTable != null) {
                ForeignKey fk = new ForeignKey();
                fk.setSourceTable(table);
                fk.setTargetTable(targetTable);
                
                // Map source columns
                sourceColumnNames.forEach(srcColName -> {
                    table.getColumns().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(srcColName))
                        .findFirst()
                        .ifPresent(fk::addSourceColumn);
                });
                
                // Map target columns
                targetColumnNames.forEach(tgtColName -> {
                    targetTable.getColumns().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(tgtColName))
                        .findFirst()
                        .ifPresent(fk::addTargetColumn);
                });
                
                table.addForeignKey(fk);
            } else {
                System.err.println("⚠ Tabla destino no encontrada: " + targetTableName);
            }
        });
    }
    
    /** Return the filtered foreign indexes **/
    public static List<ForeignKeyIndex> getForeignIndexes(List<Index> indexes) {
        if (indexes == null || indexes.isEmpty()) {
            return Collections.emptyList();
        }
        
        return indexes.stream()
            .filter(index -> index instanceof ForeignKeyIndex)
            .map(index -> (ForeignKeyIndex) index)
            .toList();
    }
    
}