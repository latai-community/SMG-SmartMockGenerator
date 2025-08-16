```
.
├── cli/
│   ├── src/
│   │   ├── index.ts                     # CLI entry point.
│   │   ├── config/
│   │   │   ├── ConfigManager.ts         # Manages CLI configuration (missing in your structure).
│   │   │   └── Logger.ts                # Configures the logging system (missing in your structure).
│   │   ├── controllers/
│   │   │   └── SmgCLIController.ts      # Orchestrates the CLI logic.
│   │   └── services/
│   │   │   ├── InquirerService.ts       # Handles interactive questions.
│   │   │   └── JavaExecutorService.ts   # Executes the Java JAR.
│   ├── package.json
│   └── tsconfig.json
└── core/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── smg/
│   │   │           ├── SMGApplication.java          # Main class of the Java JAR.
│   │   │           ├── config/
│   │   │           │   ├── SMGConfig.java            # Configuration object.
│   │   │           │   └── PropertyReader.java       # Reads the smg.properties file.
│   │   │           ├── schemas/
│   │   │           │   ├── SchemaManager.java        # Loads, parses, and "cleans" the schemas.
│   │   │           │   ├── SchemaLoader.java         # Loads the schema SQL files from resources.
│   │   │           │   ├── SchemaParser.java         # Parses DDL to extract tables, columns, and constraints.
│   │   │           │   ├── entities/
│   │   │           │   │   ├── Schema.java           # Model object for the schema.
│   │   │           │   │   ├── Table.java            # Model object for a table.
│   │   │           │   │   ├── Column.java           # Model object for a column.
│   │   │           │   │   └── ForeignKey.java       # Model object for a foreign key.
│   │   │           ├── mockaroo/
│   │   │           │   ├── MockarooClient.java       # Mockaroo API client.
│   │   │           │   ├── MockarooSchemaGenerator.java # Builds the JSON for Mockaroo.
│   │   │           │   └── entities/
│   │   │           │       ├── MockarooField.java    # Model object for a Mockaroo field.
│   │   │           │       └── MockarooOptions.java 
│   │   │           ├── generation/
│   │   │           │   └── DataGenerator.java        # Coordinates the generation of synthetic data.
│   │   │           ├── diagrams/
│   │   │           │   └── DiagramGenerator.java     # Generates diagrams in MMD or DBML format.
│   │   │           ├── fileio/
│   │   │           │   ├── FileManager.java          # Class for all file operations.
│   │   │           │   └── exporters/
│   │   │           │       ├── SQLExporter.java      # Exports data to SQL.
│   │   │           │       ├── CSVExporter.java      # Exports data to CSV.
│   │   │           │       ├── XLSXExporter.java     # Exports data to XLSX.
│   │   │           │       └── JSONExporter.java     # Exports data to JSON.
│   │   │           └── logging/
│   │   │               ├── ErrorLogger.java          # Handles detailed error logs.
│   │   │               └── SummaryLogger.java        # Creates the summary log.
│   │   │
│   │   └── resources/
│   │       ├── schemas/
│   │       │   ├── HR_struct.sql            # DDL file for the HR schema.
│   │       │   ├── OE_struct.sql            # DDL file for the OE schema.
│   │       │   └── Invest_struct.sql        # DDL file for the Invest schema.
│   │       └── smg.properties        # Default properties file.
│   └── test/
│       └── java/
│           └── com/
│               └── smg/
│                   └── ... (Unit test classes)
└── pom.xml                 # Maven project configuration file.
```