"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var PrintClient = (function () {
    function PrintClient(printType) {
        var self = this;
        var listener = new cn.ichi.android.ClientListener({
            onData: function (jsonData) {
                if (self.onData !== null) {
                    if (jsonData.length > 0) {
                        var data = JSON.parse(jsonData)
                        self.onData(data);
                    } else {
                        self.onData([]);
                    }
                }
            },
            onError: function(id, message) {
                if (self.onError !== null) {
                    self.onError(id, message);
                }
            },
            onConnected: function(id) {
                if (self.onConnected !== null) {
                    self.onConnected(id);
                }
            },
            onSent: function(id) {
                if (self.onSent !== null) {
                    self.onSent(id);
                }
            },
            onClosed: function(id) {
                if (self.onClosed !== null) {
                    self.onClosed(id);
                }
            }
        });
        if (!printType) {
            printType = 0;
        }
        this.client = new cn.ichi.android.Printer(listener, printType);
    }
    PrintClient.prototype.connect = function (servername, port) {
        return this.client.connect(servername, port);
    };
    PrintClient.prototype.close = function () {
        return this.client.close();
    };
    PrintClient.prototype.send = function (data) {
        return this.client.send(data);
    };
    PrintClient.prototype.receive = function () {
        return this.client.receive();
    };
    PrintClient.getUsbPrinters = function() {
        var jsonString = cn.ichi.android.Printer.getUsbPrinters();
        if (jsonString.length > 0) {
            return JSON.parse(jsonString);
        } else {
            return [];
        }
    }
    PrintClient.getBlueToothPrinters = function() {
        var jsonString =  cn.ichi.android.Printer.getBlueToothPrinters();
        if (jsonString.length > 0) {
            return JSON.parse(jsonString);
        } else {
            return [];
        }
    }
    PrintClient.getSerialPortPrinters = function() {
        var jsonString =  cn.ichi.android.Printer.getSerialPortPrinters();
        if (jsonString.length > 0) {
            return JSON.parse(jsonString);
        } else {
            return [];
        }
    }
    return PrintClient;
}());
exports.PrintClient = PrintClient;
//# sourceMappingURL=index.android.js.map