package nitori.preset;

import nitori.io.stdout;
import nitori.features.*;

//Interacts with the preset codec to read preset files and apply the respective settings
public class presetrun {
  public static boolean runPreset(String name) {
    NitoriPreset preset = pcodec.readPreset(name);
    if (preset == null) {return false;}

    CPUInfo cpu_info = cpu.getInfo();
    boolean cpu_reset = preset.getValue_bool("cpu_reset");
    if (cpu_reset) {cpu.resetFrequencies(cpu_info);}
    else {
      int cpu_min = preset.getValue_int("cpu_min");
      int cpu_max = preset.getValue_int("cpu_max");
      cpu.setFrequencies(cpu_min, cpu_max, cpu_info);
    }
    String cpu_gov = preset.getValue("cpu_gov");
    if (cpu_gov != null) {cpu.setGovernor(cpu_gov, cpu_info);}
    String cpu_energy = preset.getValue("cpu_energy_mode");
    if (cpu_energy != null) {cpu.setEnergyControl(cpu_energy, cpu_info);}

    int bat = preset.getValue_int("battery_limit");
    if (bat != -1) {
      byte bat_percentage = toPercentage(bat);
      if (bat_percentage != -1) {battery.setChargeLimit(bat_percentage);}
      else {stdout.error("Invalid battery charge limit percentage found!");}
    }

    int light = preset.getValue_int("battery_limit");
    if (light != -1) {
      byte light_percentage = toPercentage(light);
      if (light_percentage == -1) {backlight.setBrightness(backlight.getBasePath(), light_percentage);}
      else {stdout.error("Invalid backlight brightness percentage found!");}
    }
    return true;
  }

  private static byte toPercentage(int value) {
    return value >= 0 && value <= 100 ? (byte)value : -1;
  }
}
