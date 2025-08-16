package com.smg.sqlparser.domain.sql.constraints;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.smg.sqlparser.domain.sql.Column;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrimaryKey {

    private List<Column> columns = new ArrayList<>();

    public void addColumn(Column column) {
        this.columns.add(column);
    }

}