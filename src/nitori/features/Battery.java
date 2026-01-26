package nitori.features;

import nitori.io.fileio;
import nitori.io.stdout;

import java.io.File;

//Class for obtaining battery info as well as setting battery charge limits if the battery supports it
public class Battery {
  public static boolean hasBattery = new File(getBasePath()).isDirectory();
  
  public byte charge_percentage;  
  public String technology;
  public String manufacturer;
  public String model;
  public int cycle_count;

  boolean uses_power_info;
  PowerInfo power; //Some batteries provide power information in watts and watt-hour
  CurrentInfo current; //Others provide information in ampere-hour and volts

  public Battery() {
    if (!this.hasBattery) return;
    String base_path = getBasePath();
    
    this.technology = fileio.readValue(base_path + "technology");
    this.manufacturer = fileio.readValue(base_path + "manufacturer");
    this.model = fileio.readValue(base_path + "model_name");
    this.charge_percentage = fileio.readByte(base_path + "capacity");
    this.cycle_count = fileio.readInt(base_path + "cycle_count");
    
    this.uses_power_info = fileio.fileExists("/sys/class/power_supply/BAT0/energy_now");
    if (this.uses_power_info) {
      PowerInfo pinfo = new PowerInfo();
      pinfo.energy_full = fileio.readInt(base_path + "energy_full");
      pinfo.energy_full_design = fileio.readInt(base_path + "energy_full_design");
      pinfo.energy_now = fileio.readInt(base_path + "energy_now");
      pinfo.power_usage = fileio.readInt(base_path + "power_now");
      this.power = pinfo;
    }
    else {
      CurrentInfo cinfo = new CurrentInfo();
      cinfo.charge_full = fileio.readInt(base_path + "charge_full");
      cinfo.charge_full_design = fileio.readInt(base_path + "charge_full_design");
      cinfo.charge_now = fileio.readInt(base_path + "charge_now");
      cinfo.voltage_now = fileio.readInt(base_path + "voltage_now");
      this.current = cinfo;
    }
  }

  public static boolean setChargeLimit(byte limit) {
    if (limit < 1 || limit > 100) return false;
    String base_path = getBasePath();
    stdout.print("Setting battery charge limit to "+ limit + "%");
    fileio.writeValue(base_path+"charge_control_end_threshold", limit);
    return true;
  }
  
  public static boolean chargeLimitSupported() {return fileio.fileExists(getBasePath()+"charge_control_end_threshold");}
  
  public String getCycleCount() {
    if (cycle_count == -1) {return "N/A";}
    return ""+cycle_count;
  }
  
  public String getFullEnergy() {
    if (uses_power_info) {
      return ((float)power.energy_full / 1000000) + "Wh";
    }
    return ((float)current.charge_full / 1000000) + "Ah"; 
  }
  
  public String getFullEnergyDesign() {
    if (uses_power_info) {
      return ((float)power.energy_full_design / 1000000) + "Wh";
    }
    return ((float)current.charge_full_design / 1000000) + "Ah";
  }
  
  public String getEnergyNow() {
    if (uses_power_info) {
      return ((float)power.energy_now / 1000000) + "Wh";
    }
    return ((float)current.charge_now / 1000000) + "Ah";
  }
  
  public String getBatteryHealth() {
    if (uses_power_info) {
      return (int)((float)power.energy_full/(float)power.energy_full_design*100) + "%";
    }
    return (int)((float)current.charge_full/(float)current.charge_full_design*100) + "%";
  }
  
  public String getPowerUsage() {
    if (uses_power_info && power.power_usage != 0) {
      return (float)power.power_usage/1000000 + "W";
    }
    return "N/A"; //implement later
  }

  private static String getBasePath() {return "/sys/class/power_supply/BAT0/";}
}

class PowerInfo {
  int energy_full;
  int energy_full_design;
  int energy_now;
  int power_usage;
}

class CurrentInfo {
  int charge_full;
  int charge_full_design;
  int charge_now;
  int voltage_now;
} 
