package com.smg;

import com.smg.config.PropertyReader;
import com.smg.config.SMGConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PropertyReader} class.
 * This test suite focuses on verifying the behavior of the `loadDefaultConfig`
 * method, which is responsible for loading properties from the classpath.
 * It uses a temporary file and static mocking to achieve test isolation.
 */
class PropertyReaderTest {

    private static final String TEST_PROPERTIES_FILE = "smg.properties";

    /**
     * Sets up the test environment by creating a temporary `smg.properties`
     * file in the current working directory. This file will be picked up by
     * the mocked class loader for testing.
     *
     * @throws IOException if an I/O error occurs while creating the file.
     */
    @BeforeEach
    void setUp() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(TEST_PROPERTIES_FILE))) {
            writer.println("model=HR");
            writer.println("model.tables=regions");
            writer.println("model.diagram=myHR_regions_diagram.mmd");
            writer.println("output.schema=createHR_Structure.sql");
            writer.println("output.data=mySyntheticDataForHR.sql");
            writer.println("config.encoding=UTF-8");
            writer.println("config.mock=config-mockaroo.json");
            writer.println("config.apikey_mockaroo=YOUR_MOCKAROO_API_KEY_HERE");
            writer.println("error.file=logErrorSmg.log");
            writer.println("summary.file=summarySmg.log");
        }
    }

    /**
     * Cleans up the temporary properties file after each test to ensure
     * a clean state for subsequent tests.
     */
    @AfterEach
    void tearDown() {
        File tempFile = new File(TEST_PROPERTIES_FILE);
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    /**
     * Tests that the critical properties (`model`, `tables`, `error.file`, `summary.file`)
     * are correctly read from the mock `smg.properties` file.
     * <p>
     * This test uses `MockedStatic` to intercept the call to `getResourceAsStream`
     * and redirect it to our temporary test file. This allows for a robust
     * test without relying on the actual classpath contents.
     *
     * @throws IOException if there is an error during I/O operations.
     */
    @Test
    void loadDefaultConfig_shouldLoadCriticalKeysCorrectly() throws IOException {
        try (MockedStatic<PropertyReader> mockedStatic = Mockito.mockStatic(PropertyReader.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(() -> PropertyReader.class.getClassLoader().getResourceAsStream(Mockito.anyString()))
                    .thenReturn(new java.io.FileInputStream(TEST_PROPERTIES_FILE));

            SMGConfig loadedConfig = PropertyReader.loadDefaultConfig();

            // Assertions to validate the critical properties
            assertNotNull(loadedConfig, "The loaded configuration object should not be null.");
            assertEquals("HR", loadedConfig.getModel(), "Model property should be 'HR'.");

            List<String> expectedTables = Arrays.asList("regions");
            assertEquals(expectedTables, loadedConfig.getTables(), "Tables list should contain 'regions'.");

            assertEquals("logErrorSmg.log", loadedConfig.getErrorFile(), "Error log file name should match.");
            assertEquals("summarySmg.log", loadedConfig.getSummaryFile(), "Summary log file name should match.");

            // Optionally, you can add assertions for non-critical parameters to ensure they are also loaded
            assertEquals("myHR_regions_diagram.mmd", loadedConfig.getDiagramOutput(), "Diagram output should match.");
            assertEquals("createHR_Structure.sql", loadedConfig.getSchemaOutput(), "Schema output should match.");
            assertEquals("mySyntheticDataForHR.sql", loadedConfig.getDataOutput(), "Data output should match.");
            assertEquals("UTF-8", loadedConfig.getEncoding(), "Encoding should be 'UTF-8'.");
            assertEquals("config-mockaroo.json", loadedConfig.getMockConfig(), "Mock config file should be loaded.");
            assertEquals("YOUR_MOCKAROO_API_KEY_HERE", loadedConfig.getMockApiKey(), "Mock API key should be loaded.");
        }
    }

    /**
     * Tests the behavior of `loadDefaultConfig` when the properties file is not found.
     * It should return a new {@link SMGConfig} object with default values and log a warning.
     * This test ensures that the application handles a missing configuration file gracefully.
     */
    @Test
    void loadDefaultConfig_shouldReturnDefaultConfigWhenFileNotFound() {
        // Mock the static getResourceAsStream to simulate a file not found
        try (MockedStatic<PropertyReader> mockedStatic = Mockito.mockStatic(PropertyReader.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(() -> PropertyReader.class.getClassLoader().getResourceAsStream(Mockito.anyString()))
                    .thenReturn(null);

            SMGConfig loadedConfig = PropertyReader.loadDefaultConfig();

            assertNotNull(loadedConfig, "The configuration object should not be null, even if the file is missing.");
            assertNull(loadedConfig.getModel(), "Model should be null when the properties file is not found.");

            // Check that default values are still applied
            assertEquals("UTF-8", loadedConfig.getEncoding(), "Default encoding should be 'UTF-8'.");
            assertEquals("logErrorSmg.log", loadedConfig.getErrorFile(), "Default error file should be 'logErrorSmg.log'.");
            assertEquals("summarySmg.log", loadedConfig.getSummaryFile(), "Default summary file should be 'summarySmg.log'.");
        }
    }
}