package nitori;

import nitori.io.*;
import nitori.freebsd.*;
import nitori.cli.cli;
import nitori.preset.*;

import java.util.ArrayList;

//The main class that controls FreeBSD functionality, this is the high-level "overview" control of the program
//It reads the prompted CLI arguments and then runs the respective functionality
//Each function returns true if the user tried to run its respective task
//If all functions return false, then the user didn't try to do anything, and in that case main() prints a help screen
class tasks_freebsd {
  //CPU control and monitoring
  static boolean runCPUTasks(String[] args, SystemInfo info) {
    int cpu_freq = cli.cpuFrequency(args);
    boolean set_freq = cpu_freq != -1;
    boolean display_info = cli.cpuInfo(args);
    boolean reset = cli.cpuReset(args);
     
    if (!set_freq && !display_info && !reset) return false;

    int currentFreq = info.getCPUFrequency();
    String availableFreqs = info.getAvailableFrequencies();
    if (currentFreq != -1) {
      if (set_freq) cpu.setClockSpeed(cpu_freq, currentFreq, availableFreqs);
      else if (reset) cpu.resetClockSpeed(info);
    }
    else stdout.error("CPU frequency control is not available on this system!");
    
    if (display_info) {
      stdout.print(
        "[CPU Specifications]"
        + "\n * Current clock speed: "+currentFreq+" MHz"
        + "\n * Available CPU clock speeds: "+availableFreqs
        + "\n * Number of threads: " + info.getCoreCount()
      );
    }
    return true;
  }

  static boolean runBatteryTasks(String[] args, SystemInfo info) {
    boolean displayInfo = cli.batteryInfo(args);
    String batteryState = info.getBatteryState();
    int batteryPercentage = info.getBatteryPercentage();
    boolean hasBattery = batteryState != null && batteryPercentage != -1;

    
    if (!displayInfo) return false;
    if (!hasBattery) {
      stdout.error("No hardware battery was found!");
      return true;
    }

    String percentage_str = batteryPercentage != -1 ? ""+batteryPercentage : "N/A";
    String state_str = batteryState != null ? batteryState : "N/A";
    stdout.print(
      "[Battery Specifications]"
      + "\n * Charge percentage: " + percentage_str
      + "\n * Charge state: " + state_str
    );
    return true;
  }

  //Unfinished, unit needs to be converted or checked if it's correct
  static boolean runMemoryTasks(String[] args, SystemInfo info) {
    if (!cli.memoryInfo(args)) return false;
    String message = 
      "[System RAM information]"
        + "\n * Total memory: " + info.getTotalMemory() + " GB"
        + "\n * Free memory: " + info.getFreeMemory() + " GB"
        + "\n * Used memory: " + info.getUsedMemory() + " GB"
        + "\n * Wire memory: " + info.getWireMemory() + " GB"
        + "\n * Cached memory: " + info.getCachedMemory() + " GB"
    ;
    stdout.print(message);
    return true;
  }
}
