package nitori.io;

import java.io.File;

public class battery {
  public static boolean hasBattery() {return new File(getBasePath()).isDirectory();}
  
  public static BatInfo getInfo() {
    String base_path = getBasePath();
    BatInfo bat = new BatInfo();
    if (!new File(base_path).isDirectory()) {return bat;}
    
    bat.power_usage = writer.valueToInt(writer.readValue(base_path + "power_now"));
    bat.charge_percentage = writer.valueToByte(writer.readValue(base_path + "capacity"));
    bat.energy_full = writer.valueToInt(writer.readValue(base_path + "energy_full"));
    bat.energy_full_design = writer.valueToInt(writer.readValue(base_path + "energy_full_design"));
    bat.energy_now = writer.valueToInt(writer.readValue(base_path + "energy_now"));
    bat.technology = writer.readValue(base_path + "technology");
    bat.manufacturer = writer.readValue(base_path + "manufacturer");
    bat.model = writer.readValue(base_path + "model_name");
    
    return bat;
  }
  
  public static boolean setChargeLimit(byte limit) {
    if (limit < 1 || limit > 100) {return false;}
    String base_path = getBasePath();
    writer.writeValue(base_path+"charge_control_end_threshold", ""+limit);
    return true;
  }
  
  private static String getBasePath() {return "/sys/class/power_supply/BAT0/";}
}
