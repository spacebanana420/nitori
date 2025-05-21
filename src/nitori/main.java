package nitori;

import nitori.cli.*;
import nitori.io.*;

public class main {
  public static void main(String[] args) {
    if (cli.askedForHelp(args)) {help.printHelp(); return;}
    if (!supportedOS()) {stdout.print("Unsupported OS! Nitori only works on Linux-based operating systems!");}
    
    boolean ran_task = runTasks(args);
    if (!ran_task) {help.printSmallHelp();}
  }
  
  private static boolean runTasks(String[] args) {
    boolean root = isRoot();
    boolean ran_cpu = runCPUTasks(args, root);
    boolean ran_bat = runBatteryTasks(args, root);
    boolean ran_light = runBacklightTasks(args, root);
    return ran_cpu || ran_bat || ran_light;
  }
  
  private static boolean runCPUTasks(String[] args, boolean root) {    
    int[] cpu_freq = cli.cpuFrequencies(args);
    String gov = cli.cpuGovernor(args);
    boolean display_info = cli.cpuInfo(args);
    
    boolean set_freqs = cpu_freq[0] != -1 || cpu_freq[1] != -1;
    boolean set_gov = gov != null;
    boolean ran_task = false;
    
    if ((set_gov || set_freqs) && !root) {
      stdout.error("You must be root to be able to modify CPU clock speeds and governor!");
      return true;
    }
    
    CPUInfo info = cpu.getInfo();
    if (set_freqs) {cpu.setFrequencies(cpu_freq[0], cpu_freq[1], info); ran_task = true;}
    if (set_gov) {cpu.setGovernor(gov, info); ran_task = true;}
    if (display_info) {
      float min_freq = info.hardware_min_frequency / 1000;
      float max_freq = info.hardware_max_frequency / 1000;
      float base_freq = info.hardware_base_frequency / 1000;
      stdout.print(
        "[CPU Specifications]"
        + "\n * Minimum supported clock speed: " + min_freq + " MHz"
        + "\n * Maximum supported clock speed: " + max_freq + " MHz"
        + "\n * Base clock speed: " + base_freq + " MHz"
        + "\n * Number of threads: " + info.core_count
      );
      ran_task = true;
    }
    return ran_task;
  }
  
  private static boolean runBatteryTasks(String[] args, boolean root) {
    byte charge_percentage = cli.batteryPercentage(args);
    boolean display_info = cli.batteryInfo(args);
    boolean set_charge = charge_percentage != -1;
    boolean ran_task = set_charge || display_info;
    if (!ran_task) {return false;}
    if (!battery.hasBattery()) {
      stdout.error("No hardware battery was found!");
      return true;
    }
    
    if (set_charge) {
      if (!root) {stdout.error("You must be root to be able to modify battery charge limits!"); return true;}
      boolean result = battery.setChargeLimit(charge_percentage);
      if (!result) {stdout.error("The battery charge limit must be a percentage value between 1% and 100%!");}
    }
    if (display_info) {
      BatInfo info = battery.getInfo();
      stdout.print(
        "[Battery Specifications]"
        + "\n * Technology: " + info.technology
        + "\n * Manufacturer: " + info.manufacturer
        + "\n * Model: " + info.model
        + "\n * Energy capacity: " + info.energy_full
        + "\n * Original energy capacity  : " + info.energy_full_design
        + "\n * Current charge: " + info.energy_now
        + "\n * Current charge percentage: " + info.charge_percentage + "%"
        + "\n * Power usage: " + info.power_usage
      );
    }
    return true;
  }
  
  private static boolean runBacklightTasks(String[] args, boolean root) {
    byte percentage = cli.backlightPercentage(args);
    boolean display_info = cli.backlightInfo(args);
    boolean set_percentage = percentage != -1;
    if (!display_info && !set_percentage) {return false;}
    if (!backlight.hasBacklight()) {
      stdout.error("No built-in screen was found with available backlight control!");
      return true;
    }
    
    if (set_percentage) {
      if (!root) {stdout.error("You must be root to be able to modify the screen's backlight brightness!"); return true;}
      boolean result = backlight.setBrightness(percentage);
      if (!result) {stdout.error("The screen brightness must be a percentage value between 1% and 100%!");}
    }
    if (display_info) {
      stdout.print("Current backlight percentage: " + backlight.getBrightness());
    }
    return true;
  }
  
  private static boolean supportedOS() {return System.getProperty("os.name").equals("Linux");}
  private static boolean isRoot() {return System.getProperty("user.home").equals("/root");}
}
