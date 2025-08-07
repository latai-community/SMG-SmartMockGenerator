import log4js from 'log4js';

log4js.configure({
    appenders: {
        console: { type: 'console' },
        app: { type: 'file', filename: 'smg-cli.log' }
    },
    categories: {
        default: { appenders: ['console', 'app'], level: 'info' }
    }
});

export const logger = log4js.getLogger();