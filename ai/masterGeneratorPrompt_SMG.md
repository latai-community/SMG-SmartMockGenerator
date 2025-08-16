# Java TXT SMG: Synthetic Data Management Generator

## 💻 Project Description

Java TXT SMG is a Java 17 command-line application designed to **generate synthetic data** for predefined database schemas. It offers a flexible approach to data generation, allowing users to configure the process via command-line arguments or a properties file. The application integrates with the **Mockaroo API** to produce realistic mock data.

The core functionalities include:

* **Schema Definition**: Uses predefined DDL files for schemas like HR, OE, and Invest.
* **Customization**: Users can select specific tables, define row counts, and provide a custom configuration file for Mockaroo.
* **Data Generation**: Leverages the Mockaroo API to create synthetic data based on the cleaned schema and user-defined parameters.
* **Flexible Exports**: The final data can be exported in various formats, including **.sql**, **.csv**, **.xlsx**, and **.json**.
* **Diagrams**: Automatically generates entity-relationship diagrams in **MMD** or **DBML** format.
* **Logging**: Implements detailed error logging and a summary log to track the execution process.

-----

## 📂 Folder Structure

```
.
├── cli/
│   ├── src/
│   │   ├── index.ts              # CLI entry point.
│   │   ├── config/
│   │   │   ├── ConfigManager.ts          # Manages CLI configuration.
│   │   │   └── Logger.ts                 # Configures the logging system.
│   │   ├── controllers/
│   │   │   └── SmgCLIController.ts       # Orchestrates the CLI logic.
│   │   └── services/
│   │       ├── InquirerService.ts        # Handles interactive questions.
│   │       └── JavaExecutorService.ts    # Executes the Java JAR.
│   ├── package.json
│   └── tsconfig.json
├─── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── smg/
│   │       │           ├── SMGApplication.java                 # Main class of the Java JAR.
│   │       │           ├── config/
│   │       │           │   ├── SMGConfig.java                  # Configuration object.
│   │       │           │   └── PropertyReader.java             # Reads the smg.properties file.
│   │       │           ├── schemas/
│   │       │           │   ├── SchemaManager.java              # Loads, parses, and cleans the schemas.
│   │       │           │   ├── SchemaLoader.java               # Loads the schema SQL files.
│   │       │           │   ├── SchemaParser.java               # Old parser (manual or regex-based).
│   │       │           │   ├── antlr/
│   │       │           │   │   ├── ANTLRSchemaParser.java      # Wrapper around ANTLR to extract schema.
│   │       │           │   │   ├── runners/
│   │       │           │   │   │   └── ANTLRRunner.java        # Entry point to trigger parsing.
│   │       │           │   │   └── generated/                  # ANTLR-generated lexer & parser files.
│   │       │           │   │       ├── SQLLexer.java
│   │       │           │   │       ├── SQLParser.java
│   │       │           │   │       ├── SQLBaseListener.java
│   │       │           │   │       └── SQLParserBaseVisitor.java
│   │       │           │   └── entities/
│   │       │           │       ├── Schema.java                 # Model object for the schema.
│   │       │           │       ├── Table.java                  # Model object for a table.
│   │       │           │       ├── Column.java                 # Model object for a column.
│   │       │           │       └── ForeignKey.java             # Model object for a foreign key.
│   │       │           ├── mockaroo/
│   │       │           │   ├── MockarooClient.java             # Mockaroo API client.
│   │       │           │   ├── MockarooSchemaGenerator.java    # Builds the JSON for Mockaroo.
│   │       │           │   └── entities/
│   │       │           │       ├── MockarooField.java          # Model object for a Mockaroo field.
│   │       │           │       └── MockarooOptions.java
│   │       │           ├── generation/
│   │       │           │   └── DataGenerator.java              # Coordinates the generation of synthetic data.
│   │       │           ├── diagrams/
│   │       │           │   └── DiagramGenerator.java           # Generates diagrams in MMD or DBML format.
│   │       │           ├── fileio/
│   │       │           │   ├── FileManager.java                # Class for all file operations.
│   │       │           │   └── exporters/
│   │       │           │       ├── SQLExporter.java            # Exports data to SQL.
│   │       │           │       ├── CSVExporter.java            # Exports data to CSV.
│   │       │           │       ├── XLSXExporter.java           # Exports data to XLSX.
│   │       │           │       └── JSONExporter.java           # Exports data to JSON.
│   │       │           └── logging/
│   │       │               ├── ErrorLogger.java                # Handles detailed error logs.
│   │       │               └── SummaryLogger.java              # Creates the summary log.
│   │       └── resources/
│   │           ├── logback.xml
│   │           ├── schemas/
│   │           │   ├── HR.sql                  # DDL file for the HR schema.
│   │           │   ├── OE.sql
│   │           │   └── Invest.sql
│   │           ├── grammar/                    # ANTLR grammar files.
│   │           │   └── SQL.g4                  # SQL grammar definition for ANTLR.
│   │           └── smg.properties              # Default properties file.
├── test/
│   └── java/
│       └── com/
│           └── smg/
│               ├── schemas/
│               │   └── ANTLRSchemaParserTest.java      # Unit tests for ANTLR parser.
│               └── ... (other unit tests)
├── pom.xml                                 # Maven config
```

-----

## 🚀 Usage

The application can be run in two ways:

1.  **Command-line arguments**: Provide all parameters directly in the command.
2.  **Properties file**: Use a `smg.properties` file for configuration and run the application without arguments.

### 1\. Command-line Usage

You can use the following command structure to run the application:

```sh
java -jar smg.jar \
  -model HR \
  -tables employee,department \
  -diagram myHRdiagram.mmd \
  -schemaOutput createHR_Structure.sql \
  -dataOutput mySyntheticDataForHR.sql \
  -syntheticGenerate employee(100),department(50) \
  -encoding UTF8 \
  -errorlog logErrorSMG.log \
  -summarylog summarySMG.log
```

| Parameter | Description |
| :--- | :--- |
| `-model` | **Predefined database model** (e.g., `HR`, `OE`, `Invest`). |
| `-tables` | **Comma-separated list of tables** to include from the selected model. |
| `-diagram` | **Output file name** for the ER diagram. Supported formats: `.mmd` and `.dbml`. |
| `-schemaOutput` | **Output file name** for the schema definition. Supported formats: `.sql`, `.json`, `.csv`. Automatically removes foreign key constraints that point to non-selected tables. |
| `-dataOutput` | **Output file name** for the synthetic data. Supported formats: `.sql`, `.csv`, `.xlsx`, `.json`. |
| `-syntheticGenerate` | **Row counts for each table**, formatted as `table1(count1),table2(count2)`. Defaults to `100` if not specified for a table. |
| `-encoding` | **Character encoding** for the output files (e.g., `UTF8`, `UTF-16`, `ASCII`). Defaults to `UTF8`. |
| `-errorlog` | **File path** for detailed error logs. Defaults to `logErrorSMG.log`. |
| `-summarylog` | **File path** for the summary log of the process. Defaults to `summarySmg.log`. |

### 2\. Properties File Usage

Alternatively, you can place a `smg.properties` file in the current directory and run the application with no arguments.

```sh
java -jar smg.jar
```

Example `smg.properties` file:

```properties
# Model to be chosen
model=HR
# Table selection for the Schema
model.tables=employee,department
# Diagram output in mermaid DSL (so its text not image)
model.diagram=myHRdiagram.mmd
# Output file for schema structure (or database structure if its SQL)
output.schema=createHR_Structure.json
# Output file for insert rows
output.data=mySyntheticDataForHR.json
# Encoding to result schema & output
config.encoding=UTF8
# Configuration file with the extra parameters to generate the synthetic data
config.mock=config-mockaroo.json
# Mockaroo api key
config.apikey_mockaroo=your_mockaroo_api_key
error.file=logErrorSmg.log
summary.file=summarySmg.log
```

-----

## 💡 Key Features and Design

### Architecture

The application follows a modular architecture, separating concerns into distinct packages for configuration, schema parsing, data generation, and file I/O. This design ensures **clean code**, **maintainability**, and **extensibility**.

### Schema Parsing with ANTLR

The application uses **ANTLR** (ANother Tool for Language Recognition) to parse the SQL schema files. This approach is more robust than regex-based parsing, as it creates a formal syntax tree that can accurately represent the schema's structure, including tables, columns, and foreign keys. The `ANTLRSchemaParser` acts as a wrapper, translating the parsed SQL into the application's internal `Schema` objects.

### Entity-Relationship Diagram Generation

The `DiagramGenerator` class is responsible for creating visual representations of the selected schema. It uses the `Schema` model to generate diagrams in text-based formats like **Mermaid (`.mmd`)** or **DBML (`.dbml`)**, which can be easily rendered by other tools.

### Core Components

* `SMGApplication.java`: The main class that orchestrates the entire application flow, from reading configurations to invoking the data generation process.
* `SchemaManager.java`: Manages the loading, parsing, and cleaning of the database schema. It automatically removes foreign key constraints that are not part of the selected tables.
* `DataGenerator.java`: Coordinates the synthetic data generation. It interacts with `MockarooClient` to get the data and then uses the appropriate `Exporter` to write it to a file.
* `FileManager.java`: Centralizes all file-related operations, including reading properties, writing logs, and saving output files.
* `ErrorLogger.java` & `SummaryLogger.java`: Provide robust logging. The error logger captures detailed information like timestamps and stack traces, while the summary logger provides a high-level overview of the execution.

-----
