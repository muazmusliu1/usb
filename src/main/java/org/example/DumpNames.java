/*
 * Copyright (C) 2013 Klaus Reimer <k@ailis.de>
 * See LICENSE.txt for licensing information.
 */

package org.example;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.usb.*;

public class DumpNames
{
    private static void dumpName(final UsbDevice device)
            throws UnsupportedEncodingException, UsbException
    {
        final UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
        final byte iManufacturer = desc.iManufacturer();
        final byte iProduct = desc.iProduct();
        if (iManufacturer == 0 || iProduct == 0) return;

        // Dump the device name
        System.out.println(device.getString(iManufacturer) + " @ "
                + device.getString(iProduct));
        System.out.println("\n "+ desc.idProduct() +"   "+ desc.idVendor());
    }

    public static UsbDevice findDevice(UsbHub hub, String temp) throws UnsupportedEncodingException, UsbException {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (device.getString(desc.iManufacturer()).equals(temp)) return device;
            if (device.isUsbHub())
            {
                device = findDevice((UsbHub) device, temp);
                if (device != null) return device;
            }
        }
        return null;
    }
    public static UsbDevice findByIds(UsbHub hub, short vendorId, short productId){
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor()==vendorId && desc.idProduct()==productId) return device;
            if (device.isUsbHub())
            {
                device = findByIds((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }

    private static void connectWithDevice(final UsbDevice device) throws UsbException, UnsupportedEncodingException {
        UsbConfiguration configuration = device.getActiveUsbConfiguration();
        System.out.println(configuration.getConfigurationString());
        UsbInterface iface = configuration.getUsbInterface((byte) 0x1);
        iface.claim();
        try
        {
            //write or read
            System.out.printf("Reached");
        }
        finally
        {
            iface.release();
        }
    }
    private static void processDevice(final UsbDevice device)
    {
        // When device is a hub then process all child devices
        if (device.isUsbHub())
        {
            final UsbHub hub = (UsbHub) device;
            for (UsbDevice child: (List<UsbDevice>) hub.getAttachedUsbDevices())
            {
                processDevice(child);
            }
        }

        // When device is not a hub then dump its name.
        else
        {
            try
            {
                dumpName(device);
            }
            catch (Exception e)
            {

                System.err.println("Ignoring problematic device: " + e);
            }
        }
    }


    public static void main(final String[] args) throws UsbException, UnsupportedEncodingException {
        // Get the USB services and dump information about them
        final UsbServices services = UsbHostManager.getUsbServices();

        // Dump the root USB hub

        processDevice(services.getRootUsbHub());
       UsbDevice device = findDevice(services.getRootUsbHub(),"Apple Inc.");
        if(device!=null){
            connectWithDevice(device);
        }

    }
}