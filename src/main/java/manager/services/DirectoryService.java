package manager.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import manager.enums.consoles.ConsoleImages;
import manager.enums.devices.DeviceSupport;
import manager.enums.devices.FunkeyDevice;
import manager.enums.devices.PowKiddyV90Device;
import manager.models.Rom;
import manager.models.Settings;
import manager.models.SystemListItem;

public class DirectoryService {

    public static List<SystemListItem> loadDevice(String directory){
        Settings settings = SettingsService.loadSettings();
        String rootFolder;

        if(directory == null || directory.isEmpty()){
            // first check if the settings exists
            if(settings != null){
                rootFolder = settings.getGeneral().getRootDirectory();
            }else{
                // otherwise default to first removable drive
                rootFolder = getFirstRemovableDrivePath();
            }
        }else{
            // if we pass in a directory we use that to load 
            rootFolder = directory;
        }
        
        //check settings for device we are using
        DeviceSupport device;
        if(settings.getGeneral().getDeviceProfile() == null || settings.getGeneral().getDeviceProfile().isEmpty()){
            device =  DeviceSupport.FUNKEY_S; //Default to Funkey S if no value is found
        }else{
            device = DeviceSupport.getByName(settings.getGeneral().getDeviceProfile());
        }
        
        Map<String, Entry<String, String>> folders = getFolderList(device, rootFolder);
        List<SystemListItem> systemList = new ArrayList<SystemListItem>();


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
                        romFile,
                        enumName 
                ));
            }

            // Using folderName here for systemName, replace it with the variable you want
            SystemListItem system = new SystemListItem(folderName, enumName, roms, image);
            systemList.add(system);
        }
 
        return systemList;

    }

    public static Rom saveRom(Rom inputRom) {
        try {
            // Get settings
            Settings settings = SettingsService.loadSettings();

             //check settings for device we are using, and what path we are using
            DeviceSupport device = DeviceSupport.getByName(settings.getGeneral().getDeviceProfile()); //set to funkey for now
            
            String system = null;

            switch(device){
                case FUNKEY_S:
                    system = FunkeyDevice.getDeviceByEnumName(inputRom.getSystem()).getFolderPath();
                break;
                case POWKIDDY_V90:
                    system = PowKiddyV90Device.getDeviceByEnumName(inputRom.getSystem()).getFolderPath();
                break;
            }
            
            File[] files = inputRom.getFiles();
            
            // Define root directory
            Path rootDirectory = Paths.get(settings.getGeneral().getRootDirectory());
    
            // Define target directory
            Path targetDirectory = rootDirectory.resolve(system);
    
            // Create directory if it doesn't exist
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }
    
            // Create a list to store non-null copied files
            List<File> copiedFiles = new ArrayList<>();
            
            // Copy each file into the target directory
            for (File file : files) {
                // Skip null files
                if (file != null) {
                    Path originalPath = file.toPath();
                    Path targetPath = targetDirectory.resolve(file.getName());
                    Files.copy(originalPath, targetPath);
                    // Add copied file to list
                    copiedFiles.add(targetPath.toFile());
                }
            }
            
            // Update the Rom object with non-null copied files
            inputRom.setFiles(copiedFiles.toArray(new File[0]));
            
        } catch (IOException e) {
            e.printStackTrace();
            // You may want to handle exceptions differently, depending on your application
        }
        return inputRom;
    }    

    /*
     * Based on device get folders that match that device only ruturns filePath and foldername
     */
    public static Map<String, Entry<String, String>> getFolderList(DeviceSupport deviceSupport, String filePath) {
        Map<String, Entry<String, String>> folderPaths = new HashMap<>();
        File directory = new File(filePath);
        
        switch (deviceSupport) {
            case FUNKEY_S:
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles(File::isDirectory);
                    if (files != null) {
                        for (File file : files) {
                            String folderName = file.getName();
                            for (FunkeyDevice device : FunkeyDevice.values()) {
                                if (device.getFolderPath().equals(folderName)) {
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
            case POWKIDDY_V90:
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles(File::isDirectory);
                    if (files != null) {
                        for (File file : files) {
                            String folderName = file.getName();
                            for (PowKiddyV90Device device : PowKiddyV90Device.values()) {
                                if (device.getFolderPath().equals(folderName)) {
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
        String folderName = Paths.get(filePath).getFileName().toString();
        
        switch (deviceSupport) {
            case FUNKEY_S:
                FunkeyDevice funkeyDevice = Arrays.stream(FunkeyDevice.values())
                        .filter(device -> device.getFolderPath().equals(folderName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No matching FunkeyDevice found for folder: " + folderName));

                        romFiles = findRom(funkeyDevice, filePath);
                break;
            case POWKIDDY_V90:
                PowKiddyV90Device powKiddyDevice = Arrays.stream(PowKiddyV90Device.values())
                        .filter(device -> device.getFolderPath().equals(folderName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No matching FunkeyDevice found for folder: " + folderName));

                        romFiles = findRom(powKiddyDevice, filePath);
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
    public static List<File[]> findRom(FunkeyDevice funkeyDevice, String filePath) {
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

    /*
     * PowKiddy V90 Implementation for getting file pairs to give us for our Rom objects later
     */
    public static List<File[]> findRom(PowKiddyV90Device powKiddyDevice, String filePath) {
        List<File[]> foundFiles = new ArrayList<>();
        File directory = new File(filePath);
    
        if (directory.exists() && directory.isDirectory()) {
            for (String extension : powKiddyDevice.getFileExtensions()) {
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

    public static String getFirstRemovableDrivePath() {
        // Get all filesystem roots
        File[] roots = File.listRoots();
    
        for (File root : roots) {
            // Skip the C: drive
            if (!root.getAbsolutePath().equalsIgnoreCase("C:\\")) {
                if (isRemovableDrive(root)) {
                    return root.getAbsolutePath();
                }
            }
        }
    
        // No removable drive found, so fallback to documents folder or home folder
        String userHome = System.getProperty("user.home");
        File documentsFolder = new File(userHome, "Documents");
        File romsFolder = new File(documentsFolder, "roms");
    
        if (!romsFolder.exists()) {
            romsFolder.mkdirs(); // Create the "roms" folder if it doesn't exist
        }
    
        return romsFolder.getAbsolutePath();
    }

    private static boolean isRemovableDrive(File root) {
        String os = System.getProperty("os.name").toLowerCase();
    
        if (os.contains("win")) {
            // Get version of Windows
            String version = System.getProperty("os.version");
            String majorVersionString = version.split("\\.")[0];
            int majorVersion = Integer.parseInt(majorVersionString);
    
            // Check if Windows version is 7 or later
            if (majorVersion >= 6) {
                // Try to get the drive's type using a PowerShell command
                String driveLetter = root.getAbsolutePath().substring(0, 2); // e.g., "E:"
                ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "/c", "Get-WmiObject -Query \"SELECT * from win32_logicaldisk WHERE deviceid = '" + driveLetter + "'\" | select MediaType");
                processBuilder.redirectErrorStream(true);
                try {
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        // If the drive's MediaType is null or 11, it's probably removable
                        if (line.isEmpty() || "11".equals(line)) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("sunos")) {
            String path = root.getAbsolutePath();
            return path.toLowerCase().startsWith("/volumes") || path.toLowerCase().startsWith("/media") || path.toLowerCase().startsWith("/run/media");
        } else {
            return false;
        }
    }
    
}
