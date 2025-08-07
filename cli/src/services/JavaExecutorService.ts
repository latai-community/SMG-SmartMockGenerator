import { spawn } from 'child_process';
import ora from 'ora';

/**
 * Executes the Java core application with the provided arguments.
 */
export class JavaExecutorService {

    private javaProcessPath = 'java';
    private javaJarPath = '../core/target/smg-core-1.0.0.jar';

    public execute(args: string[]): Promise<void> {
        return new Promise((resolve, reject) => {
            const spinner = ora('Generating synthetic data...').start();
            const allArgs = ['-jar', this.javaJarPath, ...args];

            const child = spawn(this.javaProcessPath, allArgs);

            child.stdout.on('data', (data) => {
                // Update spinner with Java's log output
                spinner.text = data.toString().trim();
            });

            child.stderr.on('data', (data) => {
                spinner.stop();
                console.error(`\nError from Java process:\n${data}`);
                reject(new Error(data.toString()));
            });

            child.on('close', (code) => {
                if (code === 0) {
                    spinner.succeed('Generation completed successfully!');
                    console.log('Check the summary.log and error.log for details.');
                    resolve();
                } else {
                    spinner.fail(`Generation failed with exit code ${code}.`);
                    reject(new Error(`Java process exited with code ${code}`));
                }
            });
        });
    }
}