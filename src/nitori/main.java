package nitori;

import nitori.cli.*;
import nitori.io.*;

public class main {
  public static void main(String[] args) {
    if (cli.askedForHelp(args)) {help.printHelp(); return;}
    if (args.length == 0) {help.printSmallHelp(); return;}
    stdout.PRINT_LEVEL = stdout.getPrintLevel(args);
    
    boolean ran_task = runTasks(args);
    if (!ran_task) {help.printSmallHelp();}
  }
  
  private static boolean runTasks(String[] args) {
    if (!supportedOS()) {stdout.print("Unsupported OS! Nitori only works on Linux-based operating systems!"); return true;}
    final boolean root = isRoot();
    final boolean[] ran_tasks = new boolean[3];
    
    Thread[] t = new Thread[3];
    t[0] = new Thread(() -> {ran_tasks[0] = runCPUTasks(args, root);});
    t[1] = new Thread(() -> {ran_tasks[1] = runBatteryTasks(args, root);});
    t[2] = new Thread(() -> {ran_tasks[2] = runBacklightTasks(args, root);});
    for (Thread thread : t) {thread.start();}
    for (Thread thread : t) {
      try{thread.join();}
      catch(InterruptedException e) {e.printStackTrace(); return false;}
    }
    
    return ran_tasks[0] || ran_tasks[1] || ran_tasks[2];
  }
  
  private static boolean runCPUTasks(String[] args, boolean root) {    
    int[] cpu_freq = cli.cpuFrequencies(args);
    String gov = cli.cpuGovernor(args);
    boolean display_info = cli.cpuInfo(args);
    
    boolean set_freqs = cpu_freq[0] != -1 || cpu_freq[1] != -1;
    boolean set_gov = gov != null;
    if (!set_freqs && !set_gov && !display_info) {return false;}
    
    if ((set_gov || set_freqs) && !root) {
      stdout.error("You must be root to be able to modify CPU clock speeds and governor!");
      return true;
    }
    
    CPUInfo info = cpu.getInfo();
    if (set_freqs) {cpu.setFrequencies(cpu_freq[0], cpu_freq[1], info);}
    if (set_gov) {
      boolean result = cpu.setGovernor(gov, info);
      if (!result) {stdout.error("The provided cpu governor \""+gov+"\" is not supported!");}
    }
    if (display_info) {
      String governors_str = "\n * Available governors: ";
      for (int i = 0; i < info.available_governors.length-1; i++) {
        governors_str += info.available_governors[i] + " ";
      }
      governors_str += info.available_governors[info.available_governors.length-1]; //no space for last element
      stdout.print(
        "[CPU Specifications]"
        + "\n * Minimum supported clock speed: " + cpu.speedToMHz(info.hardware_min_frequency) + " MHz"
        + "\n * Maximum supported clock speed: " + cpu.speedToMHz(info.hardware_max_frequency) + " MHz"
        + "\n * Base clock speed: " +  (info.hardware_base_frequency != -1 ? cpu.speedToMHz(info.hardware_base_frequency) + " MHz" : "N/A")
        + "\n * Number of threads: " + info.core_count
        + governors_str
        + "\n"
        + "\n * Current minimum clock speed: " + cpu.speedToMHz(info.min_frequency[0]) + " MHz"
        + "\n * Current maximum clock speed: " + cpu.speedToMHz(info.max_frequency[0]) + " MHz"
        + "\n * Current governor: " + info.governor[0]
      );
    }
    return true;
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
      if (!battery.chargeLimitSupported()) {stdout.error("Your system's battery does not support setting charge limits at the OS level!\nMaybe it's available in BIOS/UEFI?"); return true;}
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
        + "\n * Energy capacity: " + info.getFullEnergy()
        + "\n * Original energy capacity: " + info.getFullEnergyDesign()
        + "\n * Battery health: " + info.getBatteryHealth()
        + "\n * Current charge: " + info.getEnergyNow()
        + "\n * Current charge percentage: " + info.charge_percentage + "%"
        + "\n * Charge cycle count: " + info.getCycleCount()
        + "\n * Power usage: " + info.getPowerUsage()
      );
    }
    return true;
  }
  
  private static boolean runBacklightTasks(String[] args, boolean root) {
    String base_path = backlight.getBasePath();
    byte percentage = cli.backlightPercentage(args);
    boolean display_info = cli.backlightInfo(args);
    boolean set_percentage = percentage != -1;
    if (!display_info && !set_percentage) {return false;}
    if (!backlight.hasBacklight(base_path)) {
      stdout.error("No built-in screen was found with available backlight control!");
      return true;
    }
    
    if (set_percentage) {
      if (!root) {stdout.error("You must be root to be able to modify the screen's backlight brightness!"); return true;}
      boolean result = backlight.setBrightness(base_path, percentage);
      if (!result) {stdout.error("The screen brightness must be a percentage value between 1% and 100%!");}
    }
    if (display_info) {
      stdout.print("Current backlight percentage: " + backlight.getBrightness(base_path) + "%");
    }
    return true;
  }
  
  private static boolean supportedOS() {return System.getProperty("os.name").equals("Linux");}
  private static boolean isRoot() {return System.getProperty("user.home").equals("/root");}
}
