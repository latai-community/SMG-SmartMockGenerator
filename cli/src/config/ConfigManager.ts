import fs from 'fs';
import path from 'path';

const configFilePath = path.join(process.cwd(), 'smg-config.json');

/**
 * Manages saving and loading the SMG CLI configuration.
 * This allows the interactive mode to remember previous choices.
 */
export function getConfig(): any {
  try {
    const configData = fs.readFileSync(configFilePath, 'utf-8');
    return JSON.parse(configData);
  } catch (error) {
    // Return a default configuration if the file doesn't exist or is invalid
    return {
      model: 'HR',
      tables: ['employees', 'departments', 'jobs'],
      dataOutput: 'data/synthetic_data.sql',
      syntheticGenerate: 'employees(100),departments(50),jobs(10)',
    };
  }
}

/**
 * Saves the current configuration to a file.
 *
 * @param config The configuration object to save.
 */
export function saveConfig(config: any): void {
  try {
    const configData = JSON.stringify(config, null, 2);
    fs.writeFileSync(configFilePath, configData);
    console.log(`Configuration saved to ${configFilePath}`);
  } catch (error) {
    console.error('Failed to save configuration:', error);
  }
}