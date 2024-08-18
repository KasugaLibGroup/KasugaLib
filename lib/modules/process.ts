import {} from './interface'

const native_process = require('kasuga:native/process');

const process = {}

const env = new Proxy({}, {
    get: (target, prop) => {
        return native_process.getEnv(prop);
    },
    set: (target, prop, value) => {
        return true;
    },
    ownKeys: () => {
        return native_process.listEnv();
    },
})

Object.defineProperty(process, 'env', {
    value: env,
    writable: false,
    enumerable: true,
    configurable: false,
})

export { process }