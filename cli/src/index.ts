import { SmgCLIController } from './controllers/SmgCLIController';

async function main() {
    const controller = new SmgCLIController();

    // Check for command-line arguments. If they exist, use them.
    if (process.argv.length > 2) {
        controller.runFromArgs();
    } else {
        // If no arguments, run in interactive mode.
        controller.runInteractive();
    }
}

main();