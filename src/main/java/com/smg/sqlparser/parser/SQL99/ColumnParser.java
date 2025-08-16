package com.smg.sqlparser.parser.SQL99;

import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import com.smg.sqlparser.domain.sql.Column;
import com.smg.sqlparser.enums.DataType;

import java.util.List;
import java.util.stream.IntStream;

public final class ColumnParser {

    public static Column parse(ColumnDefinition colDef) {
        String name = colDef.getColumnName();
        DataType dataType = parseDataType(colDef);
        Long length = parseLength(colDef, dataType);
        
        boolean primaryKey = parsePrimaryKey(colDef);
        boolean nullable = !primaryKey && parseNullable(colDef);
        boolean unique = parseUnique(colDef);
        
        
        return new Column(name, dataType, length, nullable, primaryKey, unique );
    }
    
    /** Determines if column is primary key **/
    private static boolean parsePrimaryKey(ColumnDefinition colDef) {
        List<String> specs = colDef.getColumnSpecs();
        if (specs == null || specs.size() < 2) return false;
        
        return IntStream.range(0, specs.size() - 1)
            .anyMatch(i -> (specs.get(i) + " " + specs.get(i + 1))
                .equalsIgnoreCase("PRIMARY KEY"));
    }
    
    /** Removes length/precision from type and returns only base type */
    private static DataType parseDataType(ColumnDefinition colDef) {
        ColDataType colDataType = colDef.getColDataType();
        String colType = colDataType.toString().replaceAll("\\(.*\\)", "");
        colType = colType.trim();
        return DataType.valueOf(colType);
    }

    /** Extracts the column length if present, otherwise returns default from SQL99 enum */
    private static Long parseLength(ColumnDefinition colDef, DataType dataType) {
        String typeStr = colDef.getColDataType().toString();
        String match = typeStr.replaceAll(".*\\((.*?)\\).*", "$1");

        if (!match.equals(typeStr)) {
            try {
                return Long.parseLong(match.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return dataType.getDefaultLength() != null ? dataType.getDefaultLength().longValue() : null;
    }

    /** Determines if the column is nullable */
    private static boolean parseNullable(ColumnDefinition colDef) {
        List<String> specs = colDef.getColumnSpecs();
        if (specs == null || specs.isEmpty()) return true;
        
        List<String> cleanSpecs = specs.stream()
            .filter(s -> s != null && !s.trim().isEmpty())
            .map(String::toUpperCase)
            .toList();
        
        boolean hasNotNull = IntStream.range(0, cleanSpecs.size() - 1)
            .anyMatch(i -> cleanSpecs.get(i).equals("NOT") && cleanSpecs.get(i + 1).equals("NULL"));
		
		return !hasNotNull;
	}

    /** Determines if the column has UNIQUE constraint */
    private static boolean parseUnique(ColumnDefinition colDef) {
        List<String> specs = colDef.getColumnSpecs();
        return specs != null && specs.contains("UNIQUE");
    }
 
}