package nitori.io;

public class backlight {
  public static float getBrightness() {
    String base_path = getBasePath();
    int raw_brightness = writer.valueToInt(writer.readValue(base_path + "brightness"));
    int raw_max_brightness = writer.valueToInt(writer.readValue(base_path + "max_brightness"));
    return raw_brightness/raw_max_brightness*100;
  }
  
  private static String getBasePath() {return "/sys/class/backlight/intel_backlight/";}
}
