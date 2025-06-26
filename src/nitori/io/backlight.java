package nitori.io;

import java.io.File;

public class backlight {
  public static boolean hasBacklight(String base_path) {return base_path != null;}
  
  public static float getBrightness(String base_path) {
    float brightness = (float)fileio.valueToInt(fileio.readValue(base_path + "brightness"));
    float max_brightness = (float)fileio.valueToInt(fileio.readValue(base_path + "max_brightness"));
    return brightness/max_brightness*100;
  }
  
  public static boolean setBrightness(String base_path, byte percentage) {
    if (percentage < 1 || percentage > 100) {return false;}
    
    float factor = (float)percentage/100;
    int max_brightness = fileio.valueToInt(fileio.readValue(base_path + "max_brightness")); //replace with a backlightinfo class later
    int brightness = (int)(max_brightness * factor);
    fileio.writeValue(base_path + "brightness", ""+brightness);
    return true;
  }
  
  public static String getBasePath() {
    String base_path = "/sys/class/backlight/";
    File f = new File(base_path);
    if (!f.isDirectory()) {return null;}
    
    String[] vendor_paths = f.list();
    if (vendor_paths == null || vendor_paths.length == 0) {return null;}
    return base_path+vendor_paths[0]+"/";
  }
}
