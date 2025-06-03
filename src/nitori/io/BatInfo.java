package nitori.io;

public class BatInfo {
  public byte charge_percentage;  
  public String technology;
  public String manufacturer;
  public String model;
  public int cycle_count;
  
  boolean uses_power_info;
  public PowerInfo power; //Some batteries provide power information in watts and watt-hour
  public CurrentInfo current; //Others provide information in ampere-hour and volts
  
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
