package com.bong.autotranscriber;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

public class Brother {
    public Brother() {
        Log.v("app", "Instantiating Brother Printer");
    }

    /**
     * Launch the thread to print
     */
    public void sendFileToQL820NWB(Bitmap bitmap, Context context) {
        Log.v("app", "Prepping to send file to printer");
        // Setup the printer
        Printer printer = new Printer();
        PrinterInfo printerInfo = printer.getPrinterInfo();

        // Ensure your printer is connected to the same wi-fi used by your device
        printerInfo.ipAddress = "10.0.0.43";
        printerInfo.printerModel = PrinterInfo.Model.QL_820NWB;
        printerInfo.port = PrinterInfo.Port.NET;

        printerInfo.labelNameIndex = LabelInfo.QL700.W62.ordinal();
        printerInfo.isAutoCut = true;
//        printerInfo.printMode = PrinterInfo.PrintMode.ORIGINAL;
        printerInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
        printerInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAPER;
        printerInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;
        printerInfo.workPath = FileHelper.Companion.getWorkPathDirectory(context);
        printer.setPrinterInfo(printerInfo);
        new Thread(new Runnable() {
            @Override
            public void run() {
                printBitmap(bitmap, printer);
            }
        }).start();
    }

    private void printBitmap(Bitmap bitmap, Printer printer) {
        if (printer.startCommunication()) {
            PrinterStatus status = printer.printImage(bitmap);

            // if error log the error
            if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                Log.e("app", "Brother Printer returned an error message: " + status.errorCode.toString());
            }

            printer.endCommunication();

            bitmap.recycle();
        }
    }

    /**
     * Launch the thread to print
     */
    public void sendFileToRJ2150(Bitmap bitmap, Context context) {
        // Specify printer
        Printer printer = new Printer();
        PrinterInfo printerInfo = printer.getPrinterInfo();
        printerInfo.printerModel = PrinterInfo.Model.RJ_2150;
        printerInfo.port = PrinterInfo.Port.BLUETOOTH;
        printerInfo.macAddress = "24:71:89:5D:5F:62";

        printer.setBluetooth(BluetoothAdapter.getDefaultAdapter());

        // Print Settings
        printerInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
        printerInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAPER;
        printerInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;

        printerInfo.workPath = FileHelper.Companion.getWorkPathDirectory(context);
        printerInfo.customPaper = FileHelper.Companion.getCustomPaperPath(context);

        printer.setPrinterInfo(printerInfo);

        new Thread(new Runnable() {
            @Override
            public void run() {
                printBitmap(bitmap, printer);
            }
        }).start();
    }
}
