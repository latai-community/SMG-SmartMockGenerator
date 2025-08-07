import { Command } from 'commander';
import { InquirerService } from '../services/InquirerService';
import { JavaExecutorService } from '../services/JavaExecutorService';

/**
 * SmgCLIController orchestrates the CLI's main flow, delegating
 * specific tasks to dedicated services.
 */
export class SmgCLIController {

    private program = new Command();
    private inquirerService: InquirerService;
    private javaExecutorService: JavaExecutorService;

    constructor() {
        this.inquirerService = new InquirerService();
        this.javaExecutorService = new JavaExecutorService();
        this.setupCommander();
    }

    /**
     * Sets up the command-line arguments using Commander.js.
     */
    private setupCommander() {
        this.program
            .version('1.0.0')
            .description('                                   \n' +
                ' ▄█▀▀▀█▄█████▄     ▄███▀ ▄▄█▀▀▀█▄█ \n' +
                '▄██    ▀█ ████    ████ ▄██▀     ▀█ \n' +
                '▀███▄     █ ██   ▄█ ██ ██▀       ▀ \n' +
                '  ▀█████▄ █  ██  █▀ ██ ██          \n' +
                '▄     ▀██ █  ██▄█▀  ██ ██▄    ▀████\n' +
                '██     ██ █  ▀██▀   ██ ▀██▄     ██ \n' +
                '█▀█████▀▄███▄ ▀▀  ▄████▄ ▀▀███████ \n' +
                '                                   \n' +
                '                                   \n')
            .option('-m, --model <name>', 'Specify the database model (e.g., HR, OE)')
            .option('-t, --tables <names>', 'Comma-separated list of tables to generate (e.g., employees,departments)')
            .option('-d, --diagram <path>', 'Generate a Mermaid ERD diagram')
            .option('-s, --schemaOutput <path>', 'Generate DDL schema output')
            .option('-o, --dataOutput <path>', 'Generate synthetic data output')
            .option('-g, --syntheticGenerate <tables>', 'Specify tables and row counts (e.g., employees(100),departments(50))')
            .option('-k, --mockApiKey <key>', 'Mockaroo API key')
            .parse(process.argv);
    }

    /**
     * Runs the CLI in interactive mode, prompting the user for input.
     */
    public async runInteractive() {
        try {
            const answers = await this.inquirerService.getInteractiveAnswers();
            const args = this.constructArgsFromAnswers(answers);
            await this.javaExecutorService.execute(args);
        } catch (error) {
            console.error('An error occurred during interactive execution:', error);
        }
    }

    /**
     * Runs the CLI from command-line arguments.
     */
    public async runFromArgs() {
        try {
            const options = this.program.opts();
            const args = this.constructArgsFromOptions(options);
            await this.javaExecutorService.execute(args);
        } catch (error) {
            console.error('An error occurred during command-line execution:', error);
        }
    }

    private constructArgsFromOptions(options: any): string[] {
        const args: string[] = [];
        if (options.model) args.push('-model', options.model);
        if (options.tables) args.push('-tables', options.tables);
        if (options.diagram) args.push('-diagram', options.diagram);
        if (options.schemaOutput) args.push('-schemaOutput', options.schemaOutput);
        if (options.dataOutput) args.push('-dataOutput', options.dataOutput);
        if (options.syntheticGenerate) args.push('-syntheticGenerate', options.syntheticGenerate);
        if (options.mockApiKey) args.push('-mockApiKey', options.mockApiKey);
        return args;
    }

    private constructArgsFromAnswers(answers: any): string[] {
        const args: string[] = [];
        if (answers.model) args.push('-model', answers.model);
        if (answers.tables) args.push('-tables', answers.tables);
        if (answers.dataOutput) args.push('-dataOutput', answers.dataOutput);
        if (answers.syntheticGenerate) args.push('-syntheticGenerate', answers.syntheticGenerate);
        return args;
    }
}