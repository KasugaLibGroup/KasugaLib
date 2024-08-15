import {require} from "./loader";
const websocket = require("kasuga_lib:native/websocket");

interface MinecraftWebSocketHandler{
    send(buf:any):void;
    ping(): void;
    close(code:number , reason:string): void;
    removeEventListener(type: string, listener: any): void;
    addEventListener(type: string, listener: any): void;
}

class MinecraftWebSocket implements WebSocket{
    static readonly CONNECTING = 0;
    static readonly OPEN = 1;
    static readonly CLOSING = 2;
    static readonly CLOSED = 3;
    readonly CLOSED = 3;
    readonly CLOSING = 2;
    readonly CONNECTING = 0;
    readonly OPEN = 1;
    binaryType: BinaryType = "arraybuffer";
    readonly bufferedAmount: number = 0;
    readonly extensions: string = "";
    private handle: MinecraftWebSocketHandler;


    constructor(url:string|URL,protocols:string|string[]|undefined) {
        url = url.toString();
        this.handle = websocket.createWebSocket(url);
        this.addEventListener("open",()=>{
            this.readyState = MinecraftWebSocket.OPEN;
            if(this.onopen)
                this.onopen();
        })
        this.addEventListener("message",(ev)=>this.onmessage(ev));
        this.addEventListener("close",(ev)=>{
            this.readyState = MinecraftWebSocket.CLOSED;
            this.onclose(ev);
        });
        this.addEventListener("error",(ev)=>{
            this.readyState = MinecraftWebSocket.CLOSED;
            this.onerror(ev);
        });
        this.url = url;
        this.protocol = "ws";
    }

    onclose(ev: CloseEvent): any {
    }

    onerror(ev: Event): any {
    }

    onmessage(ev: MessageEvent): any {
    }

    onopen: (() => void) | null = null;
    readonly protocol: string = "";
    readyState: number = WebSocket.CONNECTING;
    readonly url: string = "";

    addEventListener<K extends keyof WebSocketEventMap>(type: K, listener: (this: WebSocket, ev: WebSocketEventMap[K]) => any, options?: boolean | AddEventListenerOptions): void;
    addEventListener(type: string, listener:any, options?: boolean | AddEventListenerOptions): void {
        this.handle.addEventListener(type, listener);
    }

    close(code?: number, reason?: string): void {
        this.handle.close(code ?? 0 ,reason ?? "");
    }

    dispatchEvent(event: Event): boolean {
        return false;
    }

    ping(): void {
        this.handle.ping();
    }

    removeEventListener<K extends keyof WebSocketEventMap>(type: K, listener: (this: WebSocket, ev: WebSocketEventMap[K]) => any, options?: boolean | EventListenerOptions): void;
    removeEventListener(type: string, listener: any, options?: boolean | EventListenerOptions): void {
        this.handle.removeEventListener(type, listener);
    }
    send(data: string | ArrayBufferLike | Blob | ArrayBufferView | ArrayBuffer): void {
        if(this.readyState != MinecraftWebSocket.OPEN){
            throw new Error("Invalid state: expected open");
        }
        return this.handle.send(data);
    }
}

Object.defineProperty(globalThis,"WebSocket",{
    value:MinecraftWebSocket,
    configurable:false
});