package org.example;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws Exception {
        WriterUsb writerUsb = new WriterUsb();
        writerUsb.printDevices();
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter a index");
        String i =myObj.nextLine();
        Integer x = Integer.parseInt(i);
        UsbDevice dev = writerUsb.getByIndex(x);
        writerUsb.connectWithDevice(dev);
    }
}
