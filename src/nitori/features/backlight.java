package nitori.features;

import nitori.io.*;
import java.io.File;

//Handles laptop screen backlight, can set and get the backlight brightness
public class backlight {
  private static String basePath = getBasePath(); //The directory with the brightness pseudo-files
  private static String saveFile = "/etc/nitori/saved_brightness"; //Used for saving and restoring screen brightness
  
  public static boolean hasBacklight() {return basePath != null;}
  
  public static float getBrightness() {
    float brightness = (float) fileio.readInt(basePath + "brightness");
    float max_brightness = (float)fileio.readInt(basePath + "max_brightness");
    return brightness/max_brightness*100;
  }
  
  public static boolean setBrightness(byte percentage) {
    if (percentage < 1 || percentage > 100) {return false;}
    
    float factor = (float)percentage/100;
    int max_brightness = fileio.readInt(basePath + "max_brightness"); //replace with a backlightinfo class later
    int brightness = (int)(max_brightness * factor);
    fileio.writeValue(basePath + "brightness", brightness);
    return true;
  }
  
  //Saves the current screen brightness into a file for restoring later on
  public static void saveBrightness() {
    String brightnessFile = basePath+"brightness";

    int brightness = fileio.readInt(brightnessFile);
    fileio.createDirectory("/etc/nitori/");
    boolean saved = fileio.writeValue(saveFile, brightness);
    if (saved) {stdout.print("Current backlight brightness level was saved for later use");}
  }

  //Sets screen brightness based on a previously-saved value
  public static void restoreBrightness() {
    String brightnessFile = basePath+"brightness";
    if (!fileio.fileExists(saveFile)) {
      stdout.print_verbose("No backlight brightness has been previously saved, skipping task");
      return;
    }

    int brightness = fileio.readInt(saveFile);
    boolean saved = fileio.writeValue(brightnessFile, brightness);
    if (saved) {stdout.print("Backlight brightness was restored to previously-saved value");}
  }

  private static String getBasePath() {
    String base_path = "/sys/class/backlight/";
    File f = new File(base_path);
    if (!f.isDirectory()) return null;
    
    String[] vendor_paths = f.list();
    if (vendor_paths == null || vendor_paths.length == 0) return null;
    return base_path+vendor_paths[0]+"/";
  }
}
