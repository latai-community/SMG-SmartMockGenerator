package com.smg.diagrams;

import com.smg.schemas.entities.Schema;
import com.smg.schemas.entities.Table;
import com.smg.schemas.entities.Column;
import com.smg.schemas.entities.ForeignKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * DiagramGenerator is responsible for creating text-based entity-relationship diagrams
 * in formats like Mermaid or DBML from the Schema object.
 */
public class DiagramGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DiagramGenerator.class);
	
	/**
	 * Generates a Mermaid ERD diagram from a Schema object and writes it to a file.
	 *
	 * @param schema The Schema object to visualize.
	 * @param outputFilePath The path of the output file (.mmd).
	 * @throws IOException if there is an error writing the file.
	 */
	public void generateMermaidDiagram(Schema schema, String outputFilePath) throws IOException {
		LOGGER.info("Generating Mermaid ERD for schema '{}' to file: {}", schema.getName(), outputFilePath);
		
		try (FileWriter writer = new FileWriter(outputFilePath)) {
			writer.write("erDiagram\n");
			
			for (Table table : schema.getTables()) {
				writer.write("  " + table.getName() + " {\n");
				for (Column column : table.getColumns()) {
					writer.write("    " + column.getDataType() + " " + column.getName() + "\n");
				}
				writer.write("  }\n");
			}
			writer.write("\n");
			
			for (Table table : schema.getTables()) {
				for (ForeignKey fk : table.getForeignKeys()) {
					writer.write(String.format("  %s ||--o{ %s : \"%s\"\n",
						table.getName(),
						fk.getReferencedTable(),
						fk.getName()));
				}
			}
		}
		LOGGER.info("Mermaid ERD generated successfully.");
	}
}