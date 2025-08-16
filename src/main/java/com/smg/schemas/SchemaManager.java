package com.smg.schemas;

import com.smg.schemas.entities.Schema;
import com.smg.schemas.entities.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the loading, parsing, and cleaning of database schemas.
 * It ensures that the final schema only contains the user-selected tables
 * and has its foreign keys adjusted accordingly.
 */
public class SchemaManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaManager.class);
	private final SchemaLoader schemaLoader;
	private final SchemaParser schemaParser;
	private final Map<String, Schema> loadedSchemas;
	
	public SchemaManager() {
		this.schemaLoader = new SchemaLoader();
		this.schemaParser = new SchemaParser();
		this.loadedSchemas = new HashMap<>();
	}
	
	/**
	 * Loads all predefined schemas (HR, OE, Invest) from resources.
	 */
	public void loadSchemas() {
		String[] modelNames = {"HR", "OE", "Invest"};
		for (String modelName : modelNames) {
			String sqlContent = schemaLoader.loadSqlContent(modelName);
			if (sqlContent != null) {
				Schema schema = schemaParser.parse(modelName, sqlContent);
				loadedSchemas.put(modelName.toUpperCase(), schema);
				LOGGER.info("Schema '{}' loaded and parsed successfully.", modelName);
			}
		}
	}
	
	/**
	 * Cleans a schema by keeping only the specified tables and removing
	 * foreign keys that reference tables outside of the selected set.
	 *
	 * @param modelName The name of the model to clean.
	 * @param selectedTables The names of the tables to keep.
	 * @return A new Schema object containing only the selected tables and valid foreign keys.
	 */
	public Schema cleanSchema(String modelName, List<String> selectedTables) {
		Schema fullSchema = loadedSchemas.get(modelName.toUpperCase());
		if (fullSchema == null) {
			LOGGER.error("Model '{}' not found. Cannot clean schema.", modelName);
			throw new IllegalArgumentException("Model not found: " + modelName);
		}
		
		LOGGER.info("Cleaning schema '{}' for selected tables: {}", modelName, selectedTables);
		
		// Convert the list of selected tables to a Set for efficient lookups
		Set<String> selectedTableSet = selectedTables.stream()
			.map(String::toUpperCase)
			.collect(Collectors.toSet());
		
		// Create a new schema with only the selected tables
		Schema cleanedSchema = new Schema(fullSchema.getName());
		List<Table> tablesToKeep = fullSchema.getTables().stream()
			.filter(table -> selectedTableSet.contains(table.getName().toUpperCase()))
			.collect(Collectors.toList());
		
		// Process each table to remove foreign keys referencing unselected tables
		for (Table table : tablesToKeep) {
			Table newTable = new Table(table.getName());
			newTable.setColumns(new ArrayList<>(table.getColumns())); // Copy all columns
			
			// Filter foreign keys
			List<String> foreignKeysRemoved = new ArrayList<>();
			table.getForeignKeys().stream()
				.filter(fk -> selectedTableSet.contains(fk.getReferencedTable().toUpperCase()))
				.forEach(fk -> newTable.addForeignKey(fk));
			
			// Log which foreign keys were removed
			table.getForeignKeys().stream()
				.filter(fk -> !selectedTableSet.contains(fk.getReferencedTable().toUpperCase()))
				.forEach(fk -> {
					LOGGER.warn("Removing foreign key '{}' from table '{}' because it references non-selected table '{}'.",
						fk.getName(), table.getName(), fk.getReferencedTable());
					foreignKeysRemoved.add(fk.getName());
				});
			
			cleanedSchema.addTable(newTable);
		}
		
		LOGGER.info("Schema cleaning complete. Kept {} tables.", cleanedSchema.getTables().size());
		return cleanedSchema;
	}
	
	/**
	 * Retrieves a previously loaded schema by name.
	 * @param name The name of the schema.
	 * @return The Schema object, or null if not found.
	 */
	public Schema getSchema(String name) {
		return loadedSchemas.get(name.toUpperCase());
	}
}