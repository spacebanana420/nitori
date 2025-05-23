package nitori.io;

import java.io.File;

public class backlight {
  public static boolean hasBacklight() {return new File(getBasePath()).isDirectory();}
  
  public static float getBrightness() {
    String base_path = getBasePath();
    float brightness = (float)writer.valueToInt(writer.readValue(base_path + "brightness"));
    float max_brightness = (float)writer.valueToInt(writer.readValue(base_path + "max_brightness"));
    return brightness/max_brightness*100;
  }
  
  public static boolean setBrightness(byte percentage) {
    if (percentage < 1 || percentage > 100) {return false;}
    
    String base_path = getBasePath();
    float factor = (float)percentage/100;
    int max_brightness = writer.valueToInt(writer.readValue(base_path + "max_brightness")); //replace with a backlightinfo class later
    int brightness = (int)(max_brightness * factor);
    writer.writeValue(base_path + "brightness", ""+brightness);
    return true;
  }
  
  private static String getBasePath() {return "/sys/class/backlight/intel_backlight/";}
}
