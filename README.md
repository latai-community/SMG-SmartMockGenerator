## Smart Mock Generator (SMG) 🔫📃
SMG is a command-line interface (CLI) tool designed to generate synthetic data based on an existing database schema. It parses Data Definition Language (DDL) files to understand table structures and uses the Mockaroo API to generate realistic-looking data. The application is built with a modular architecture, with a Java core for data processing and a Node.js CLI for user interaction.

### ✨ Features
* DDL Schema Parsing: Automatically reads and understands table and column definitions from SQL DDL files.
* Mockaroo Integration: Connects to the Mockaroo API to generate high-quality, synthetic data based on inferred data types.
* Schema Visualization: Generates Entity-Relationship Diagrams (ERDs) in Mermaid format (.mmd) for a visual representation of the database schema.
* Flexible Output: Outputs data in SQL format (INSERT statements), ready to be loaded into any database.
* Interactive & Command-Line Modes: Offers a user-friendly interactive mode to guide you through the process, as well as a command-line mode for automation and scripting. 
* Configurable: Supports a configuration file to save default settings for quick and consistent use.

### 📁 Project Structure
The project is organized into two main modules:
```text
.
├── core/                         # The Java core application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smg/      # Java source code
│   │   │   │   ├── config/
│   │   │   │   ├── database/
│   │   │   │   ├── diagrams/
│   │   │   │   ├── fileio/
│   │   │   │   ├── logging/
│   │   │   │   ├── mockaroo/
│   │   │   │   └── schemas/
│   │   │   └── resources/          # Logback configuration
│   │   └── test/
│   └── pom.xml                   # Maven build file
│
├── cli/                          # The Node.js command-line interface
│   ├── src/
│   │   ├── config/               # CLI configuration and logging
│   │   ├── controllers/          # CLI logic orchestration
│   │   ├── services/             # Dedicated services for CLI tasks
│   │   └── index.ts              # CLI entry point
│   ├── package.json              # Node.js configuration and dependencies
│   └── tsconfig.json             # TypeScript configuration
```

### 🛠️ Prerequisites
To build and run this application, you will need to have the following installed:
-[x]Java Development Kit (JDK): Version 8 or higher. 
-[x]Apache Maven: Version 3.6 or higher.
-[x]Node.js: Version 14 or higher.
-[x]npm (Node Package Manager): Comes bundled with Node.js.

You will also need a Mockaroo API Key to generate data. You can get one for free from the Mockaroo website.

### 🚀 How to Build and Run
Follow these steps to get the application up and running.

#### **Step 1: Build the Java Core Module**
Navigate to the core directory and use Maven to compile and package the project into a JAR file.

```bash
cd core
mvn clean package
```

This will create `smg-core-1.0-SNAPSHOT.jar` in the `core/target/` directory.

#### **Step 2: Set up the Node.js CLI**
Return to the project root, navigate to the cli directory, and install the Node.js dependencies.

```bash
cd ../cli
npm install
npm run build
```

The `npm run build` command will compile the TypeScript code and place the output in the `dist/` directory.

#### **Step 3: Run the CLI**
Now you can run the CLI from the `cli` directory.

### ✍️ Usage
The SMG CLI can be used in two modes: interactive and command-line.

### Interactive Mode
Run the CLI without any arguments to start the interactive mode.


```bash
node dist/index.js
```

The CLI will ask you a series of questions to configure the data generation process.

## 📺 Command-Line Mode
You can also provide all the required arguments directly in the command.

| Short Flag | Long Flag             | Description / Example                                                            |
|------------|-----------------------|----------------------------------------------------------------------------------|
| `-m HR`    | `--model`             | Specifies the database model (e.g., `HR`, `OE`).                                 |
| `-t ...`   | `--tables`            | Comma-separated list of tables (e.g., `employees,departments`).                  |
| `-d ...`   | `--diagram`           | Output path for the Mermaid ERD diagram (e.g., `diagrams/hr_erd.mmd`).           |
| `-s ...`   | `--schemaOutput`      | Output path for the DDL schema file (e.g., `schemas/hr_schema.sql`).             |
| `-o ...`   | `--dataOutput`        | Output path for the synthetic data (SQL INSERTs) (e.g., `data/hr_data.sql`).     |
| `-g ...`   | `--syntheticGenerate` | Tables and number of rows to generate (e.g., `employees(1000),departments(50)`). |
| `-k ...`   | `--mockApiKey`        | Your Mockaroo API key. Required for synthetic data generation.                   |

#### Example 
```bash

node dist/index.js -m HR -t employees,departments -o data/hr_data.sql -g 'employees(1000),departments(50)' -k 'YOUR_API_KEY'
```

## 📜 Dependencies
### Java Core Module

* `org.apache.maven.plugins:maven-compiler-plugin:` For compiling Java code.
* `org.slf4j:` Standard logging facade.
* `ch.qos.logback:` Logging implementation.
* `com.fasterxml.jackson.core:jackson-databind:` For JSON serialization/deserialization.
* `org.apache.httpcomponents.client5:httpclient5:` For making HTTP requests to the Mockaroo API.

### Node.js CLI Module
* `chalk:` For adding colors and styles to terminal text.
* `commander:` For parsing command-line arguments.
* `inquirer:` For creating interactive command-line prompts.
* `log4js:` A robust logging framework for Node.js.
* `ora:` For creating elegant terminal spinners.
* `typescript:` The language used for the CLI.
