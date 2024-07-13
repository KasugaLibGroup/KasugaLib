import "./interface"
const native = require("kasuga:internal/registry");
export function getRegistry(registry:string){
    return new Registry(registry);
}

class Registry{
    nativeInterface:any
    constructor(name:string) {
        this.nativeInterface = native.getRegistry(name);
    }

    register(name:string,item:any){
        this.nativeInterface.register(name,item);
    }
}