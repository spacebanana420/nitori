package nitori.preset;

import nitori.io.stdout;
import nitori.features.*;

//Work in progress
public class presetrun {
  public static boolean runPreset(String name) {
    NitoriPreset preset = pcodec.readPreset(name);
    if (preset == null) {return false;}
    CPUInfo cpu_info = cpu.getInfo();
    boolean cpu_reset = preset.getValue_bool("cpu_reset");
    int cpu_min = preset.getValue_int("cpu_min");
    int cpu_max = preset.getValue_int("cpu_max");
    String cpu_gov = preset.getValue("cpu_gov");
    String cpu_energy = preset.getValue("cpu_energy_mode");

    int battery = preset.getValue_int("battery_limit");
    byte bat_percentage = toPercentage(battery);
    if (bat_percentage == -1) {stdout.error("Invalid battery charge limit percentage found!");}

    int backlight = preset.getValue_int("battery_limit");
    byte light_percentage = toPercentage(battery);
    if (light_percentage == -1) {stdout.error("Invalid backlight brightness percentage found!");}
    return true;
  }

  private static byte toPercentage(int value) {
    return value >= 0 && value <= 100 ? (byte)value : -1;
  }
}
