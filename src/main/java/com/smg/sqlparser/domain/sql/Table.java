package com.smg.sqlparser.domain.sql;

import lombok.Data;
import lombok.ToString;
import com.smg.sqlparser.domain.sql.constraints.ForeignKey;
import com.smg.sqlparser.domain.sql.constraints.PrimaryKey;
import com.smg.sqlparser.domain.sql.constraints.Unique;

import java.util.ArrayList;
import java.util.List;

@Data
public class Table {
    
    private String name;
    private List<Column> columns = new ArrayList<>();
    private PrimaryKey primaryKey;
    @ToString.Exclude
    private List<ForeignKey> foreignKeys = new ArrayList<>();
    @ToString.Exclude
    private List<Unique> uniqueConstraints = new ArrayList<>();
    
    public Table(String name) {
        this.name = name;
    }
    
    public void addColumn(Column column) {
        columns.add(column);
    }
    
    public void addForeignKey(ForeignKey foreignKey) {
        foreignKeys.add(foreignKey);
    }
    
    public boolean isColumnUnique(String columnName) {
        for (Unique uc : uniqueConstraints) {
            if (uc.getColumnNames().contains(columnName)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isColumnPrimary(String columnName) {
        return primaryKey != null && primaryKey.getColumns().stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(columnName));
    }
    
    private String mark(boolean condition) {
        return condition ? "X" : " ";
    }
    
    public String getAsciiTable() {
        int colWidthName = "Column".length();
        int colWidthType = "Type".length();
        int colWidthLength = "Length".length();
        int colWidthNullable = "Nullable".length();
        int colWidthUnique = "Unique".length();
        int colWidthPrimary = "Primary".length();
        
        // Calcular anchos máximos
        for (Column col : columns) {
            colWidthName = Math.max(colWidthName, col.getName().length());
            String cleanType = col.getType().toString();
            colWidthType = Math.max(colWidthType, cleanType.length());
            String lengthStr = col.getLength() != null ? col.getLength().toString() : "";
            colWidthLength = Math.max(colWidthLength, lengthStr.length());
        }
        
        // Separador de columnas
        String lineSeparator = "+"
            + "-".repeat(colWidthName + 2) + "+"
            + "-".repeat(colWidthType + 2) + "+"
            + "-".repeat(colWidthLength + 2) + "+"
            + "-".repeat(colWidthNullable + 2) + "+"
            + "-".repeat(colWidthUnique + 2) + "+"
            + "-".repeat(colWidthPrimary + 2) + "\n";
        
        // Calcular ancho total
        int totalWidth = lineSeparator.length() - 1;
        
        StringBuilder sb = new StringBuilder();
        
        // Borde superior
        sb.append("+").append("-".repeat(totalWidth - 2)).append("+\n");
        
        // Nombre centrado
        String upperName = name.toUpperCase();
        int paddingTotal = totalWidth - 2 - upperName.length();
        int paddingLeft = paddingTotal / 2;
        int paddingRight = paddingTotal - paddingLeft;
        sb.append("|").append(" ".repeat(paddingLeft)).append(upperName)
            .append(" ".repeat(paddingRight)).append("|\n");
        
        // Encabezado
        sb.append(lineSeparator);
        String format = "| %-" + colWidthName + "s | %-"
            + colWidthType + "s | %-"
            + colWidthLength + "s | %-"
            + colWidthNullable + "s | %-"
            + colWidthUnique + "s | %-"
            + colWidthPrimary + "s |\n";
        sb.append(String.format(
            format,
            "Column", "Type", "Length", "Nullable", "Unique", "Primary"
        ));
        sb.append(lineSeparator);
        
        for (Column column : columns) {
            String cleanType = column.getType().toString();
            String lengthStr = column.getLength() != null ? column.getLength().toString() : "";
            sb.append(String.format(
                format,
                column.getName(),
                cleanType,
                lengthStr,
                mark(column.isNullable()),
                mark(isColumnUnique(column.getName())),
                mark(isColumnPrimary(column.getName()))
            ));
        }
        
        sb.append(lineSeparator);
        return sb.toString();
    }
}
