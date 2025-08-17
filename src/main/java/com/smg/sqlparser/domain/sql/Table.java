package com.smg.sqlparser.domain.sql;

import lombok.Data;
import lombok.ToString;
import com.smg.sqlparser.domain.sql.constraints.ForeignKey;
import com.smg.sqlparser.domain.sql.constraints.PrimaryKey;
import com.smg.sqlparser.domain.sql.constraints.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    
    
    public Table getFilteredCopy(Set<String> selectedTables) {
        Table filtered = new Table(this.name);
        
        // Copiar columnas válidas
        for (Column col : this.columns) {
            boolean skip = foreignKeys.stream().anyMatch(fk ->
                fk.getSourceColumns().contains(col) &&
                    (fk.getTargetTable() == null || !selectedTables.contains(fk.getTargetTable().getName()))
            );
            
            if (!skip) {
                filtered.addColumn(col);
            }
        }
        
        // Copiar PK solo si sus columnas existen todavía
        if (primaryKey != null) {
            List<Column> pkCols = primaryKey.getColumns().stream()
                .filter(filtered.getColumns()::contains)
                .toList();
            
            if (!pkCols.isEmpty()) {
                PrimaryKey pk = new PrimaryKey();
                pk.setColumns(pkCols);
                filtered.setPrimaryKey(pk);
            }
        }
        
        // Copiar constraints únicas que apliquen
        for (Unique uc : uniqueConstraints) {
            if (filtered.getColumns().stream().anyMatch(c -> uc.getColumnNames().contains(c.getName()))) {
                filtered.getUniqueConstraints().add(uc);
            }
        }
        
        // Copiar FKs que apunten a tablas seleccionadas
        for (ForeignKey fk : foreignKeys) {
            if (fk.getTargetTable() != null && selectedTables.contains(fk.getTargetTable().getName())) {
                filtered.getForeignKeys().add(fk);
            }
        }
        
        return filtered;
    }
    
    public String toCreateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(name).append(" (\n");
        
        List<String> columnDefs = new ArrayList<>();
        
        for (Column col : columns) {
            StringBuilder colDef = new StringBuilder();
            colDef.append(col.getName()).append(" ").append(col.getType());
            
            if (col.getLength() != null && col.getLength() > 0) {
                colDef.append("(").append(col.getLength()).append(")");
            }
            
            if (!col.isNullable()) {
                colDef.append(" NOT NULL");
            }
            
            if (col.isUnique()) {
                colDef.append(" UNIQUE");
            }
            
            columnDefs.add(colDef.toString());
        }
        
        if (primaryKey != null && !primaryKey.getColumns().isEmpty()) {
            String pkCols = primaryKey.getColumns().stream()
                .map(Column::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
            columnDefs.add("PRIMARY KEY (" + pkCols + ")");
        }
        
        for (Unique uc : uniqueConstraints) {
            String cols = String.join(", ", uc.getColumnNames());
            String ucName = (uc.getName() != null && !uc.getName().isBlank())
                ? uc.getName()
                : "UQ_" + name + "_" + String.join("_", uc.getColumnNames());
            columnDefs.add("CONSTRAINT " + ucName + " UNIQUE (" + cols + ")");
        }
        
        for (ForeignKey fk : foreignKeys) {
            String sourceCols = fk.getSourceColumns().stream()
                .map(Column::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
            String targetCols = fk.getTargetColumns().stream()
                .map(Column::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
            columnDefs.add(
                "FOREIGN KEY (" + sourceCols + ") REFERENCES " +
                    fk.getTargetTable().getName() + " (" + targetCols + ")"
            );
        }
        
        sb.append("  ").append(String.join(",\n  ", columnDefs));
        sb.append("\n);");
        
        return sb.toString();
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
