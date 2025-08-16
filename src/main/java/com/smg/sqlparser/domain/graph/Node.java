package com.smg.sqlparser.domain.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.smg.sqlparser.domain.sql.Table;

@Getter
@Setter
@AllArgsConstructor
public class Node {

    private final Table table;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return table.getName().equalsIgnoreCase(node.table.getName());
    }

    @Override
    public int hashCode() {
        return table.getName().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return table.getName();
    }
}