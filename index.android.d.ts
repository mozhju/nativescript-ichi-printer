

export declare class PrintClient {
    private client;
    public onData: {(data: Array<number>): void;};
    public onError: {(id: number, message: string): void;};
    public onConnected: {(id: number): void;};
    public onSent: {(id: number): void;};
    public onClosed: {(id: number): void;};

    constructor(printType?: number) {
    }

    public connect(servername: string, port: number): number;

    public close(): number ;

    public send(data: Array<number>): number;

    public receive(): number;

    public static getUsbPrinters(): Array<string>;

    public static getBlueToothPrinters(): Array<string>;

    public static getSerialPortPrinters(): Array<string>;
}


