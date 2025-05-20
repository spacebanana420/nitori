package nitori.io;

import java.io.File;

public class backlight {
  public static boolean hasBacklight() {return new File(getBasePath()).isDirectory();}
  
  public static float getBrightness() {
    String base_path = getBasePath();
    int brightness = writer.valueToInt(writer.readValue(base_path + "brightness"));
    int max_brightness = writer.valueToInt(writer.readValue(base_path + "max_brightness"));
    return brightness/max_brightness*100;
  }
  
  public static boolean setBrightness(float percentage) {
    if (percentage < 1 || percentage > 100) {return false;}
    
    String base_path = getBasePath();
    float factor = percentage/100;
    int max_brightness = writer.valueToInt(writer.readValue(base_path + "max_brightness")); //replace with a backlightinfo class later
    int brightness = raw_max_brightness * factor;
    writer.writeValue(base_path + "brightness", brightness);
    return true;
  }
  
  private static String getBasePath() {return "/sys/class/backlight/intel_backlight/";}
}
