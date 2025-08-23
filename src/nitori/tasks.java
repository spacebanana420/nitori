package nitori;

import nitori.io.*;
import nitori.features.*;
import nitori.cli.cli;
import nitori.preset.*;

//Processes the CLI arguments to run the tasks/comands/features nitori supports
class tasks {
  static boolean runCPUTasks(String[] args, boolean root) {
    int[] cpu_freq = cli.cpuFrequencies(args);
    String gov = cli.cpuGovernor(args);
    String energy_pref = cli.cpuEnergy(args);
    boolean display_info = cli.cpuInfo(args);
    boolean reset = cli.cpuReset(args);
  
    boolean set_freqs = cpu_freq[0] != -1 || cpu_freq[1] != -1;
    boolean set_gov = gov != null;
    boolean set_energy = energy_pref != null;
    if (!set_freqs && !set_gov && !display_info && !reset && !set_energy) {return false;}
  
    if ((set_gov || set_freqs || reset || set_energy) && !root) {
      stdout.error("You must be root to be able to modify CPU configurations!");
      return true;
    }
  
    CPUInfo info = cpu.getInfo();
    if (set_freqs) {cpu.setFrequencies(cpu_freq[0], cpu_freq[1], info);}
    else if (reset) {cpu.resetFrequencies(info);}
    if (set_gov) {cpu.setGovernor(gov, info);}
    if (set_energy) {cpu.setEnergyControl(energy_pref, info);}
    if (display_info) {
      stdout.print(
        "[CPU Specifications]"
        + "\n * Minimum supported clock speed: " + cpu.speedToMHz(info.hardware_min_frequency) + " MHz"
        + "\n * Maximum supported clock speed: " + cpu.speedToMHz(info.hardware_max_frequency) + " MHz"
        + "\n * Base clock speed: " +  (info.hardware_base_frequency != -1 ? cpu.speedToMHz(info.hardware_base_frequency) + " MHz" : "N/A")
        + "\n * Number of threads: " + info.core_count
        + "\n * Available governors: " + info.governor_raw
        + "\n * Available energy preferences: " + info.str_energyPrefs()
        + "\n * Turbo enabled: " + info.str_turboStatus()
        + "\n"
        + "\n * Current minimum clock speed: " + cpu.speedToMHz(info.min_frequency[0]) + " MHz"
        + "\n * Current maximum clock speed: " + cpu.speedToMHz(info.max_frequency[0]) + " MHz"
        + "\n * Current governor: " + info.governor[0]
        + "\n * Current energy mode: " + info.str_currentEnergyPref()
      );
    }
    return true;
  }
  
  static boolean runBatteryTasks(String[] args, boolean root) {
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
  
  static boolean runBacklightTasks(String[] args, boolean root) {
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
  
  static boolean runSuspendTasks(String[] args, boolean root) {
    boolean view_states = cli.suspendStates(args);
    String suspend_mode = cli.suspendSystem(args);
    boolean hibernate = cli.hibernateSystem(args);
  
    boolean root_arguments = hibernate || suspend_mode != null;
    boolean ran_arguments = view_states || root_arguments;
    if (!ran_arguments) {return false;}
    if (!suspend.suspendIsSupported()) {
      stdout.print("Suspend functionality is not available on your system!");
      return true;
    }
  
    String[] states = suspend.supportedStates(); //Varies depending on system and configuration
    if (view_states) {
      String message = "Supported suspend states:";
      for (String state : states) {
        message +="\n * " + state;
      }
      stdout.print(message);
    }
    if (root_arguments && !root) {
      stdout.error("You must be root to be able to suspend the system!");
      return true;
    }
    if (suspend_mode != null) {
      stdout.print_verbose("Suspending system with mode " + suspend_mode);
      suspend.suspendSystem(suspend_mode, states);
    }
    else if (hibernate) {
      stdout.print_verbose("Hibernating system to disk");
      suspend.suspendSystem("disk", states);
    }
    return true;
  }

  static boolean runPresetTasks(String[] args, boolean is_root) {
    String run_preset = cli.runPreset(args);
    String create_preset = cli.createPreset(args);
    boolean list_presets = cli.listPresets(args);

    if (run_preset != null) {
      presetrun.runPreset(run_preset, is_root);
      return true;
    }
    if (create_preset != null) {
      pcodec.createPreset(create_preset, is_root);
      return true;
    }
    if (list_presets) {
      pcodec.printPresetList();
      return true;
    }
    return false;
  }

  static boolean runMemoryTask(String[] args) {
    if (!cli.memoryInfo(args)) {return false;}

    MemoryInfo meminfo = new MemoryInfo();
    if (meminfo.is_empty) {
      stdout.print_verbose("Cancelling memory info display");
      return true;
    }

    String message =
      "[System RAM information]"
      + "\n Total memory: " + (meminfo.memory_total / 1000) + " MB"
      + "\n Available memory: " + (meminfo.memory_available / 1000) + " MB"
      + "\n Free memory: " + (meminfo.memory_free / 1000) + " MB"
      + "\n Cached memory: " + (meminfo.memory_cached / 1000) + " MB"
      + "\n Used memory: " + (meminfo.memory_used / 1000) + " MB"
    ;
    stdout.print(message);
    return true;
  }
}