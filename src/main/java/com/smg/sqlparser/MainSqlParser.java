package com.smg.sqlparser;


import com.smg.sqlparser.domain.sql.Schema;
import com.smg.sqlparser.domain.sql.Table;
import com.smg.sqlparser.parser.SQL99.SqlSchemaParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MainSqlParser {



    public static void main(String[] args) throws Exception {
        String filePathStr = "C:\\Users\\joseb\\Desktop\\SMG-SmartMockGenerator\\src\\main\\resources\\models\\hr\\struct\\HR_struct.sql";
        String schemaName = "HR";
        String separatedTablesInput = "regions, deparments,";
        process( Paths.get(filePathStr),schemaName, separatedTablesInput);
    }

    public static void process(Path sqlStructureFile, String schemaName, String separatedTablesInput) throws Exception {

        Schema schema = SqlSchemaParser.parseSchemaFromFile(sqlStructureFile, schemaName);

        List<String> identifiedTables = schema.getTablesValues().stream()
                .map(Table::getName)
                .toList();

        System.out.println("🔎 Founded tables: ");
        identifiedTables.forEach( tableName -> System.out.println( "    📃 " + tableName) );


        List<String> tablesSelected = Arrays.stream(separatedTablesInput.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .toList();

        List<Table> filteredTables = schema.getTablesValues().stream()
            .filter(table -> tablesSelected.contains(table.getName().toLowerCase()))
            .toList();

            filteredTables.forEach(table -> System.out.println(table.getAsciiTable()));

            // SchemaGraph schemaGraph = new SchemaGraph();
            // Graph graph = schemaGraph.buildGraphFromSchema(schema);
            // List<String> endpointsNodes = schemaGraph.findFullRoutes(graph, tablesSelected);
            // endpointsNodes.forEach(System.out::println);

    }

}