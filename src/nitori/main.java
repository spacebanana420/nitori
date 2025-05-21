package nitori;

import nitori.cli.*;
import nitori.io.*;

public class main {
  public static void main(String[] args) {
    if (cli.askedForHelp(args)) {help.printHelp(); return;}
    boolean ran_task = runTasks(args);
    if (!ran_task) {help.printSmallHelp();}
  }
  
  private static boolean runTasks(String[] args) {
    boolean ran_task = runCPUTasks(args);
    return ran_task;
  }
  
  private static boolean runCPUTasks(String[] args) {    
    int[] cpu_freq = cli.cpuFrequencies(args);
    String gov = cli.cpuGovernor(args);
    boolean display_info = cli.cpuInfo(args);
    
    boolean set_freqs = cpu_freq[0] != -1 || cpu_freq[1] != -1;
    boolean set_gov = gov != null;
    boolean ran_task = set_freqs || set_gov || display_info;
    if (!ran_task) {return false;}
    
    CPUInfo info = cpu.getInfo();
    if (set_freqs) {cpu.setFrequencies(cpu_freq[0], cpu_freq[1], info);}
    if (set_gov) {cpu.setGovernor(gov, info);}
    if (display_info) {
      stdout.print(
        "[CPU Specifications]"
        + "\n"
        + "\n * Minimum supported clock speed: " + info.hardware_min_frequency
        + "\n * Maximum supported clock speed: " + info.hardware_max_frequency
        + "\n * Base clock speed: " + info.hardware_base_frequency
        + "\n * Number of threads: " + info.core_count
      );
    }
    return true;
  }
  
  private static boolean supportedOS() {return System.getProperty("os.name").equals("Linux");}
  private static boolean isRoot() {return System.getProperty("user.home").equals("/root");}
}
