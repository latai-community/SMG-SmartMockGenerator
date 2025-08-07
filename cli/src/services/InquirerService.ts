import inquirer from 'inquirer';
import {getConfig} from '../config/ConfigManager';

/**
 * Handles all interactive prompts for the CLI using Inquirer.js.
 */
export class InquirerService {

    private config: any;

    constructor() {
        this.config = getConfig();
    }

    public async getInteractiveAnswers(): Promise<any> {
        console.log('Welcome to the SMG Interactive CLI!');

        return inquirer.prompt([
            {
                type: 'list',
                name: 'model',
                message: 'Which database model do you want to use?',
                choices: ['HR', 'OE', 'Invest'],
                default: this.config.model,
            },
            {
                type: 'input',
                name: 'tables',
                message: 'Enter tables to generate (comma-separated):',
                default: this.config.tables.join(','),
            },
            {
                type: 'input',
                name: 'dataOutput',
                message: 'Enter output file path for data:',
                default: this.config.dataOutput,
            },
            {
                type: 'input',
                name: 'syntheticGenerate',
                message: 'Enter tables and row counts (e.g., employees(100),departments(50)):',
                default: this.config.syntheticGenerate,
            },
        ]);
    }
}