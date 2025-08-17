package com.smg.sqlparser.domain.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Schema {

    private String name;
    private Map<String, Table> tables = new HashMap<>();
    
    public Schema(String name) {
        this.name = name;
    }

    public void addTable(Table table) {
        tables.put(table.getName(), table);
    }

    public Collection<Table> getTablesValues() {
        return tables.values();
    }
    
    
}
