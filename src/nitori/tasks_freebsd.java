package nitori;

import nitori.io.*;
import nitori.freebsd.*;
import nitori.cli.cli;
import nitori.preset.*;

import java.util.ArrayList;

//The main class that controls Nitori functionality, this is the high-level "overview" control of the program
//It reads the prompted CLI arguments and then runs the respective functionality
//Each function returns true if the user tried to run its respective task
//If all functions return false, then the user didn't try to do anything, and in that case main() prints a help screen
class tasks_freebsd {
  //CPU control and monitoring
  static boolean runCPUTasks(String[] args, SystemInfo info, boolean root) {
    int cpu_freq = cli.cpuFrequency(args);
    boolean set_freq = cpu_freq != -1;
    boolean display_info = cli.cpuInfo(args);
    boolean reset = cli.cpuReset(args);
     
    if (!set_freq && !display_info && !reset) return false;
    if ((set_freq || reset) && !root) {
      stdout.error("You must be root to be able to modify CPU configurations!");
      return true;
    }

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
}
