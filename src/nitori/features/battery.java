package nitori.features;

import nitori.io.fileio;
import nitori.io.stdout;

import java.io.File;

public class battery {
  public static boolean hasBattery() {return new File(getBasePath()).isDirectory();}
  
  public static BatInfo getInfo() {
    String base_path = getBasePath();
    BatInfo bat = new BatInfo();
    if (!new File(base_path).isDirectory()) {return bat;}
    
    bat.technology = fileio.readValue(base_path + "technology");
    bat.manufacturer = fileio.readValue(base_path + "manufacturer");
    bat.model = fileio.readValue(base_path + "model_name");
    bat.charge_percentage = fileio.valueToByte(fileio.readValue(base_path + "capacity"));
    bat.cycle_count = fileio.valueToInt(fileio.readValue(base_path + "cycle_count"));
    
    bat.uses_power_info = new File("/sys/class/power_supply/BAT0/energy_now").isFile();
    if (bat.uses_power_info) {
      PowerInfo pinfo = new PowerInfo();
      pinfo.energy_full = fileio.valueToInt(fileio.readValue(base_path + "energy_full"));
      pinfo.energy_full_design = fileio.valueToInt(fileio.readValue(base_path + "energy_full_design"));
      pinfo.energy_now = fileio.valueToInt(fileio.readValue(base_path + "energy_now"));
      pinfo.power_usage = fileio.valueToInt(fileio.readValue(base_path + "power_now"));
      bat.power = pinfo;
    }
    else {
      CurrentInfo cinfo = new CurrentInfo();
      cinfo.charge_full = fileio.valueToInt(fileio.readValue(base_path + "charge_full"));
      cinfo.charge_full_design = fileio.valueToInt(fileio.readValue(base_path + "charge_full_design"));
      cinfo.charge_now = fileio.valueToInt(fileio.readValue(base_path + "charge_now"));
      cinfo.voltage_now = fileio.valueToInt(fileio.readValue(base_path + "voltage_now"));
      bat.current = cinfo;
    }
    return bat;
  }
  
  public static boolean setChargeLimit(byte limit) {
    if (limit < 1 || limit > 100) {return false;}
    String base_path = getBasePath();
    stdout.print("Setting battery charge limit to "+ limit + "%");
    fileio.writeValue(base_path+"charge_control_end_threshold", ""+limit);
    return true;
  }
  
  public static boolean chargeLimitSupported() {return fileio.fileExists(getBasePath()+"charge_control_end_threshold");}
  
  private static String getBasePath() {return "/sys/class/power_supply/BAT0/";}
}
