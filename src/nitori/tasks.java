package nitori;

import nitori.io.*;
import nitori.features.*;
import nitori.cli.cli;
import nitori.preset.*;

import java.util.ArrayList;

//Processes the CLI arguments to run the Nitori functionality
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
    if (!set_freqs && !set_gov && !display_info && !reset && !set_energy) return false;
  
    if ((set_gov || set_freqs || reset || set_energy) && !root) {
      stdout.error("You must be root to be able to modify CPU configurations!");
      return true;
    }

    if (!cpu.canControlCPU()) {
      stdout.error("CPU monitor and control is currently unavailable!\nThis is often caused by disabling CPU clock scaling funtionality in BIOS/UEFI\nIf this is your case, you must enable whatever functionality your motherboard provides for scaling/changing the CPU frequencies (e.g Intel SpeedShift)");
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
    if (!ran_task) return false;
    if (!Battery.hasBattery) {
      stdout.error("No hardware battery was found!");
      return true;
    }
  
    if (set_charge) {
      if (!Battery.chargeLimitSupported()) {stdout.error("Your system's battery does not support setting charge limits at the OS level!\nMaybe it's available in BIOS/UEFI?"); return true;}
      if (!root) {stdout.error("You must be root to be able to modify battery charge limits!"); return true;}
      boolean result = Battery.setChargeLimit(charge_percentage);
      if (!result) {stdout.error("The battery charge limit must be a percentage value between 1% and 100%!");}
    }
    if (display_info) {
      Battery battery = new Battery();
      stdout.print(
        "[Battery Specifications]"
        + "\n * Technology: " + battery.technology
        + "\n * Manufacturer: " + battery.manufacturer
        + "\n * Model: " + battery.model
        + "\n * Energy capacity: " + battery.getFullEnergy()
        + "\n * Original energy capacity: " + battery.getFullEnergyDesign()
        + "\n * Battery health: " + battery.getBatteryHealth()
        + "\n * Current charge: " + battery.getEnergyNow()
        + "\n * Current charge percentage: " + battery.charge_percentage + "%"
        + "\n * Charge cycle count: " + battery.getCycleCount()
        + "\n * Power usage: " + battery.getPowerUsage()
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
      + "\n * Total memory:       " + convertUnit(meminfo.memory_total) + " GB"
      + "\n * Available memory:   " + convertUnit(meminfo.memory_available) + " GB"
      + "\n * Free memory:        " + convertUnit(meminfo.memory_free) + " GB"
      + "\n * Cached memory:      " + convertUnit(meminfo.memory_cached) + " GB"
      + "\n * Used memory:        " + convertUnit(meminfo.memory_used) + " GB"
    ;

    if (meminfo.system_uses_swap) {
      message +=
        "\n\n * Total swap:       " + convertUnit(meminfo.swap_total) + " GB"
        + "\n * Free swap:        " + convertUnit(meminfo.swap_free) + " GB"
        + "\n * Cached swap:      " + convertUnit(meminfo.swap_cached) + " GB"
      ;
    }
    else {message += "\n\nSwap is unavailable or not used by the system";}
    stdout.print(message);
    return true;
  }

  static boolean runProcessTasks(String[] args) {
    boolean count_processes = cli.countProcesses(args);
    boolean list_processes = cli.listProcesses(args);
    String find_process = cli.findProcess(args);
    if (find_process == null && !list_processes && !count_processes) {return false;}

    Proc[] processes = Proc.getSystemProcesses();
    if (processes == null) {return true;}

    //Kernel threads do not have a command line, userspace application processes do
    var user_procs = new ArrayList<Proc>();
    for (Proc p : processes) {
      if (p.has_cmd) {user_procs.add(p);}
    }

    if (list_processes) {
      var message = new StringBuilder();
      message.append("[Full list of running userspace system processes]");
      for (Proc p : user_procs) {
        message
          .append("\n\nProcess ID ")
          .append(p.pid)
          .append("\n  * Name: ")
          .append(p.getName())
          .append("\n  * Command: ")
          .append(p.getCMDstr())
          .append("\n  * Memory usage (MB): ")
          .append((float)p.ram_usage/1000)
          .append("\n  * Swap usage (MB): ")
          .append((float)p.swap_usage/1000)
        ;
      }
      stdout.print(message.toString());
    }
    if (count_processes) {
      stdout.print(
        "Total number of running system processes: " + processes.length
        + "\nUserspace processes: " + user_procs.size()
        + "\nKernel processes: " + (processes.length-user_procs.size())
      );
    }
    if (find_process != null) {
      var message = new StringBuilder();
      boolean found_any = false;
      message.append("Found the following processes including ").append(find_process).append(" in the path:");
      find_process = find_process.toLowerCase();
      for (Proc p : user_procs) {
        if (p.cmd[0].toLowerCase().contains(find_process)) {
          found_any = true;
          message.append("\n\nProcess ID: ").append(p.pid).append("\nCommand: ").append(p.getCMDstr());
        }
      }
      if (found_any) {stdout.print(message.toString());}
      else {stdout.print("No running system process was found with " + find_process + " in the path!");}
    }
    return true;
  }

  //kB to GB and few decimal cases
  private static float convertUnit(long number) {
    number = number / 1000; //MB no decimal cases
    return (float)number / 1000; //GB decimal cases
  }
}
