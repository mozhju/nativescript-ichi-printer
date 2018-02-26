# nativescript-ichi-printer

POS printer for NativeScript.

## Supported platforms

- Android (any device with Android 4.4 and higher)

There is no support for iOS yet!

## Installing

```
tns plugin add nativescript-ichi-printer
```

## Usage

Here is a TypeScript example:

```js
import {PrintClient} from "nativescript-ichi-printer";

// Connect to the Printer
var printClient = new PrintClient(0);
printClient.onData = (data: string) => {
    console.log("Data from Printer: ", data);
};
printClient.onError = (id: number, message: string) => {
    console.log("Print client error for action #", id, ": ", message);
};
printClient.onConnected = (id: number) => {
    console.log("Print client connected action #: ", id);
};
printClient.onSended = (id: number) => {
    console.log("Print client sened action #: ", id);
};
printClient.onClosed = (id: number) => {
    console.log("Print client closed action #: ", id);
};

// Connect printer
printClient.connect("192.168.1.192", 9100);

var message = "Print test String!";
var bytes = [];
for (var i = 0; i < message.length; i++) {
    var c = message.charCodeAt(i);
    bytes.push(c & 0xFF);
}
printClient.send(bytes);

// When we are finished
printClient.close();
```



