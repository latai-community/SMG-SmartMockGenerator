package com.smg.sqlparser.domain.sql.constraints;

import lombok.Data;

import java.util.List;

@Data
public class Unique {

    private String name;
    private List<String> columnNames;

    public Unique(List<String> columnNames) {
        this.columnNames = columnNames;
    }

}