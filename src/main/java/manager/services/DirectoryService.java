package manager.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import manager.enums.consoles.ConsoleImages;
import manager.enums.devices.DeviceSupport;
import manager.enums.devices.FunkeyDevice;
import manager.models.Rom;
import manager.models.SystemListItem;

public class DirectoryService {

    public static List<SystemListItem> loadDevice(){

        //check settings for device we are using, and what path we are using
        DeviceSupport device = DeviceSupport.FUNKEY_S; //set to funkey for now
        String rootFolder = "C:\\roms\\";

        Map<String, Entry<String, String>> folders = getFolderList(device, rootFolder);
        List<SystemListItem> systemList = new ArrayList<SystemListItem>();


        switch (device) {
            case FUNKEY_S: // Funkey S Implementation
                for (Map.Entry<String, Map.Entry<String, String>> folderEntry : folders.entrySet()) {
        
                    String filePath = folderEntry.getKey();
        
                    // Extracting both strings from the Entry
                    Map.Entry<String, String> innerEntry = folderEntry.getValue();
                    String folderName = innerEntry.getKey();
                    String enumName = innerEntry.getValue();

                    // Extract the ConsoleImages enum using the enumName
                    ConsoleImages imageEnum = null;
                    try {
                        imageEnum = ConsoleImages.valueOf(enumName);
                    } catch (IllegalArgumentException e) {
                        System.out.println("No console image found for: " + enumName);
                    }

                    BufferedImage image = null;

                    if (imageEnum != null) {
                        String imagePath = imageEnum.getPath();
                        // Get ConsoleImage from enum
                        try {
                            Image originalImage = ImageIO.read(DirectoryService.class.getResourceAsStream(imageEnum.getPath()));

                            // Create a new BufferedImage with the same dimensions and the default type
                            image = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

                            // Draw the original image onto the new BufferedImage
                            Graphics2D graphics = image.createGraphics();
                            graphics.drawImage(originalImage, 0, 0, null);
                            graphics.dispose();
                        } catch (Exception e) {
                            System.out.println("Error loading console image: " + imagePath);
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            Image originalImage = ImageIO.read(DirectoryService.class.getResourceAsStream("/images/systems/small/generic.png"));

                            // Create a new BufferedImage with the same dimensions and the default type
                            image = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

                            // Draw the original image onto the new BufferedImage
                            Graphics2D graphics = image.createGraphics();
                            graphics.drawImage(originalImage, 0, 0, null);
                            graphics.dispose();
                        } catch (IOException e) {
                            System.out.println("Error loading generic console image ");
                            e.printStackTrace();
                        }
                    }

        
                    List<Rom> roms = new ArrayList<>();
        
                    // TODO: eventually check json as a next step to get more info about each rom

        
                    // we get all the file pairs in a folder
                    for (File[] romFile : checkRomFolder(device, filePath)) {
        
                        // remove the file extention for the romName by default
                        String fileNameWithExtension = romFile[0].getName();
                        int extensionIndex = fileNameWithExtension.lastIndexOf(".");
                        String romName = (extensionIndex != -1) ? fileNameWithExtension.substring(0, extensionIndex) : fileNameWithExtension;
        
                        //add to the rom list for the systemListItem
                        roms.add(new Rom(
                                romName,
                                "", // TODO: grab release date from json
                                romFile
                        ));
                    }
        
                    // Using folderName here for systemName, replace it with the variable you want
                    SystemListItem system = new SystemListItem(folderName, roms, image);
                    systemList.add(system);
                }
                break;
            // potentially handle more devices here
        }
        

        return systemList;

    }


    /*
     * Based on device get folders that match that device only ruturns filePath and foldername
     */
    public static Map<String, Entry<String, String>> getFolderList(DeviceSupport deviceSupport, String filePath) {
        Map<String, Entry<String, String>> folderPaths = new HashMap<>();
    
        switch (deviceSupport) {
            case FUNKEY_S:
                File directory = new File(filePath);
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles(File::isDirectory);
                    if (files != null) {
                        for (File file : files) {
                            String folderName = file.getName();
                            for (FunkeyDevice device : FunkeyDevice.values()) {
                                if (device.getDeviceName().equals(folderName)) {
                                    folderPaths.put(
                                        file.getPath(), 
                                        new AbstractMap.SimpleEntry<>(folderName, device.name())
                                    );
                                }
                            }
                        }
                    }
                }
                break;
            // potentially handle more devices here
        }
    
        return folderPaths;
    }
    

    /*
     * check which method to use based on the device we have
     */
    public static List<File[]> checkRomFolder(DeviceSupport deviceSupport, String filePath) {
        List<File[]> romFiles = null;

        switch (deviceSupport) {
            case FUNKEY_S:
                String folderName = Paths.get(filePath).getFileName().toString();
                FunkeyDevice funkeyDevice = Arrays.stream(FunkeyDevice.values())
                        .filter(device -> device.getDeviceName().equals(folderName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No matching FunkeyDevice found for folder: " + folderName));

                        romFiles = funkeyFindRom(funkeyDevice, filePath);
                break;
            // potentially handle more devices here
        }

        // Dont return an empty list
        if(romFiles == null){
            return new ArrayList<File[]>();
        }

        return romFiles;
    }

    /*
     * Funkey Implementation for getting file pairs to give us for our Rom objects later
     */
    public static List<File[]> funkeyFindRom(FunkeyDevice funkeyDevice, String filePath) {
        List<File[]> foundFiles = new ArrayList<>();
        File directory = new File(filePath);
    
        if (directory.exists() && directory.isDirectory()) {
            for (String extension : funkeyDevice.getFileExtensions()) {
                File[] romFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));
                if (romFiles != null && romFiles.length > 0) {
                    for (File romFile : romFiles) {
                        String romNameWithoutExtension = romFile.getName().replaceFirst("[.][^.]+$", "");
    
                        File imageFile = new File(directory, romNameWithoutExtension + ".png");
    
                        if (imageFile.exists()) {
                            foundFiles.add(new File[]{romFile, imageFile});
                        } else {
                            foundFiles.add(new File[]{romFile, null});
                        }
                    }
                }
            }
        }
    
        return foundFiles;
    }
    
}
