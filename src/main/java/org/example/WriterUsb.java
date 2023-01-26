package org.example;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WriterUsb {
    List<UsbDevice> devices = new ArrayList<UsbDevice>();
    final UsbServices services = UsbHostManager.getUsbServices();
    public WriterUsb() throws UsbException, UnsupportedEncodingException {
        findDevices(services.getRootUsbHub());

    }

    private void findDevices(UsbHub hub) throws UnsupportedEncodingException, UsbException {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            if(device.isUsbHub()){
                final UsbHub new_hub = (UsbHub) device;
                findDevices(new_hub);
            }
            else {
                final UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
                final byte iManufacturer = desc.iManufacturer();
                final byte iProduct = desc.iProduct();
                if (iManufacturer == 0 || iProduct == 0) break;
                devices.add(device);
            }
        }
    }
    public void printDevices() throws UnsupportedEncodingException, UsbException {
        Integer i = 0;
        for(UsbDevice device: devices){
        final UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
        final byte iManufacturer = desc.iManufacturer();
        final byte iProduct = desc.iProduct();
        if (iManufacturer == 0 || iProduct == 0) return;

        System.out.println(i+":"+device.getString(iManufacturer) + " @@@ "
                + device.getString(iProduct));
        System.out.println("\n "+ desc.idProduct() +"   "+ desc.idVendor());
        i++;
        }
    }

    public UsbDevice getByIndex(Integer i) throws Exception {
       try {
           return devices.get(i);
       }
       catch (IndexOutOfBoundsException e) {
           e.printStackTrace();
           System.out.println("index is out of bound!!!");
           throw new Exception();
       }
    }

    public void connectWithDevice(final UsbDevice device) throws UsbException, UnsupportedEncodingException {
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
}
