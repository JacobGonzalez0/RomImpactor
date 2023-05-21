package manager.services;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import manager.enums.devices.DeviceSupport;
import manager.enums.devices.FunkeyDevice;
import manager.models.Rom;
import manager.models.SystemListItem;

public class DirectoryService {

    public static List<SystemListItem> loadDevice(){

        //check settings for device we are using, and what path we are using
        DeviceSupport device = DeviceSupport.FUNKEY_S; //set to funkey for now
        String rootFolder = "C:\\roms\\";

        Map<String,String> folders = getFolderList(device, rootFolder);
        List<SystemListItem> systemList = new ArrayList<SystemListItem>();


        switch (device) {
            case FUNKEY_S: // Funkey S Implementation
                for(Entry<String, String> folderPath : folders.entrySet()){

                    String filePath = folderPath.getKey();
                    String systemName = folderPath.getValue();
                    
                    List<Rom> roms = new ArrayList<Rom>();
                    
                    // TODO: eventually check json as a next step to get more info about each rom

                    // we get all the file pairs in a folder
                    for(File[] romFile : checkRomFolder(device, filePath)){

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

                    SystemListItem system = new SystemListItem(systemName, roms);
                    systemList.add(system);
                }
            break;
        }

        return systemList;

    }


    /*
     * Based on device get folders that match that device only ruturns filePath and foldername
     */
    public static Map<String,String> getFolderList(DeviceSupport deviceSupport, String filePath) {
        Map<String,String> folderPaths = new HashMap<String,String>();
    
        switch (deviceSupport) {
            case FUNKEY_S:
                File directory = new File(filePath);
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles(File::isDirectory);
                    if (files != null) {
                        for (File file : files) {
                            String folderName = file.getName();
                            if (Arrays.stream(FunkeyDevice.values()).anyMatch(device -> device.getDeviceName().equals(folderName))) {
                                folderPaths.put(file.getPath(), folderName);
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

                        File[] imageFiles = directory.listFiles((dir, name) -> Pattern.matches(funkeyDevice.getImageRegexPattern(), name)
                                && name.startsWith(romNameWithoutExtension));

                        foundFiles.add(new File[] {romFile, imageFiles != null && imageFiles.length > 0 ? imageFiles[0] : null});
                    }
                }
            }
        }

        return foundFiles;
    }
}
