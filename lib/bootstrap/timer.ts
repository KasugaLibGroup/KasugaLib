import {} from "./interface"
import {require} from "./loader";

const timer = require("kasuga_lib:native/timer");
let memory : Record<number, any> = {};
globalThis.setTimeout = function(handler:TimerHandler, timeout?:number | undefined,...args:any[]){
    let id : number;
    if(timeout === undefined)
        timeout = 1;
    if(timeout < 50){
        timeout = 50;
    }
    id = timer.requestTimeout(()=>{
        if(typeof handler == 'string'){
            eval(handler);
        }else{
            handler(...memory[id]);
        }
    },timeout);
    if(id == null || id === -1){
        throw new Error("Failed to request timer")
    }
    memory[id] = args;
    return id;
}

globalThis.setInterval = function(handler:TimerHandler, interval?:number | undefined,...args:any[]){
    let id : number;
    if(interval === undefined)
        interval = 1;
    if(interval < 50){
        interval = 50;
    }
    id = timer.requestInterval(()=>{
        if(typeof handler == 'string'){
            eval(handler);
        }else{
            handler(...memory[id]);
        }
    },interval);
    if(id == null || id === -1){
        throw new Error("Failed to request ")
    }
    memory[id] = args;
    return id;
};

globalThis.clearTimeout = globalThis.clearInterval = function(handler){
    if(handler === undefined){
        return;
    }
    timer.clearSchedule(handler);
    delete memory[handler];
}
