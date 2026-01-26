package nitori.features;

import nitori.io.fileio;
import nitori.io.stdout;

import java.io.File;
import java.util.ArrayList;

public class cpu {
  public static boolean canControlCPU() {return new File("/sys/devices/system/cpu/cpu0/cpufreq/").isDirectory();}
  
  public static boolean resetFrequencies(CPUInfo info) {
    return setFrequencies(info.hardware_min_frequency/1000, info.hardware_max_frequency/1000, info);
  }

  public static boolean setFrequencies(int min_clock_speed, int max_clock_speed, CPUInfo cpu_info) {
    final String cpu_base_path = getBasePath();
    boolean setMinimum = false;
    boolean setMaximum = false;
    int min_speed = min_clock_speed * 1000; //conversion from MHz (user input) to KHz as it's measured in the system files
    int max_speed = max_clock_speed * 1000;
    
    //set minimum
    if (min_clock_speed > 0) {
      setMinimum = true;
      if (min_speed < cpu_info.hardware_min_frequency) {
        min_speed = cpu_info.hardware_min_frequency;
        stdout.print("The given minimum clock speed "+ min_clock_speed +"MHz is below the CPU's minimum supported value\nSetting minimum clock speed " + min_speed + "KHz for all cores instead");
      }
      else stdout.print("Setting minimum clock speed " + min_clock_speed + "MHz for all cores");
      
      for (String core : cpu_info.cores) {
        String path = cpu_base_path + core + "/cpufreq/scaling_min_freq";
        fileio.writeValue(path, ""+min_speed);
      }
    }
    
    //set maxmimum
    if (max_clock_speed > 0) {
      setMaximum = true;
      if (max_speed > cpu_info.hardware_max_frequency) {
        max_speed = cpu_info.hardware_max_frequency;
        stdout.print("The given maximum clock speed "+ max_clock_speed +"MHz is above the CPU's maximum supported value\nSetting maximum clock speed " + max_speed + "KHz for all cores instead");
      }
      else stdout.print("Setting maximum clock speed " + max_clock_speed + "MHz for all cores");
      
      for (String core : cpu_info.cores) {
        String path = cpu_base_path + core + "/cpufreq/scaling_max_freq";
        fileio.writeValue(path, ""+max_speed);
      }
    }
    return setMinimum || setMaximum;
  }
  
  public static boolean setGovernor(String governor, CPUInfo cpu_info) {
    if (!cpu_info.supportedGovernor(governor)) {
      stdout.error("The provided cpu governor \""+governor+"\" is not supported!");
      return false;
    }

    stdout.print("Setting CPU governor " + governor + " for all cores");
    final String cpu_base_path = getBasePath();
    for (String core : cpu_info.cores) {
      String path = cpu_base_path + core + "/cpufreq/scaling_governor";
      fileio.writeValue(path, governor);
    }
    return true;
  }
  
  public static boolean setEnergyControl(String energy_mode, CPUInfo cpu_info) {
    if (!cpu_info.cpuSupportsEnergyControl()) {
      stdout.error("Energy preference control is not available for this CPU!");
      return false;
    }
    if (!cpu_info.supportedEnergyControl(energy_mode)) {
      stdout.error("The provided energy control mode \""+energy_mode+"\" is not supported!");
      return false;
    }
    
    final String cpu_base_path = getBasePath();
    for (String core : cpu_info.cores) {
      String path = cpu_base_path + core + "/cpufreq/energy_performance_preference";
      fileio.writeValue(path, energy_mode);
    }
    return true;
  }
  
  public static CPUInfo getInfo() {
    final String base_path = getBasePath();
    String[] cpu_paths = getCPUPaths(getBasePath());
    int core_count = cpu_paths.length;
    
    //Get CPU information and current configuration
    CPUInfo cpu_info = new CPUInfo(core_count, cpu_paths);
    for (int i = 0; i < core_count; i++) {
      String full_path = base_path + cpu_paths[i];
      String min_freq = fileio.readValue(full_path+"/cpufreq/scaling_min_freq");
      String max_freq = fileio.readValue(full_path+"/cpufreq/scaling_max_freq");
      String governor = fileio.readValue(full_path+"/cpufreq/scaling_governor");
      String energy_pref = fileio.readValue(full_path+"/cpufreq/energy_performance_preference");
      cpu_info.min_frequency[i] = fileio.valueToInt(min_freq);
      cpu_info.max_frequency[i] = fileio.valueToInt(max_freq);
      cpu_info.governor[i] = governor;
      cpu_info.energy_pref[i] = energy_pref;

      String turbo_path = full_path+"/cpufreq/boost";
      cpu_info.turbo_status_exists = fileio.fileExists(turbo_path);
      cpu_info.turbo_enabled = cpu_info.turbo_status_exists && fileio.readValue(turbo_path).equals("1");
    }
    //Base frequency information if available
    if (fileio.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/base_frequency")) {
      cpu_info.hardware_base_frequency = fileio.valueToInt(fileio.readValue("/sys/devices/system/cpu/cpu0/cpufreq/base_frequency"));
    }
    else {cpu_info.hardware_base_frequency = -1;}
    
    //CPU clock speed limits
    cpu_info.hardware_min_frequency = fileio.valueToInt(fileio.readValue("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
    cpu_info.hardware_max_frequency = fileio.valueToInt(fileio.readValue("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));
    
    //Available governors and available energy modes if available
    String governors_file = fileio.readValue("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors");
    cpu_info.governor_raw = governors_file;
    cpu_info.available_governors = fileio.extractWords(governors_file);
    if (fileio.fileExists("/sys/devices/system/cpu/cpu0/cpufreq/energy_performance_available_preferences")) {
      String preferences_file = fileio.readValue("/sys/devices/system/cpu/cpu0/cpufreq/energy_performance_available_preferences");
      cpu_info.energy_pref_raw = preferences_file;
      cpu_info.energy_preferences = fileio.extractWords(preferences_file);
    }
    
    return cpu_info;
  }
  
  public static float speedToMHz(int clock_speed) {return (float)clock_speed / 1000;}
  
  private static String getBasePath() {return "/sys/devices/system/cpu/";}
  
  //From the base CPU path, get the paths that follow the pattern of cpu0, cpu1, cpu2, cpu3, etc
  private static String[] getCPUPaths(String base_path) {
    String[] paths = new File(base_path).list();
    if (paths == null) {
      stdout.error("Failed to find CPU core information!");
      return new String[0];
    }
    var cpu_paths = new ArrayList<String>();
    int ascii_min = (int)'0';
    int ascii_max = (int)'9';
    
    for (String dir : paths) {
      if (dir.length() < 4) {continue;}
      if (dir.charAt(0) != 'c' || dir.charAt(1) != 'p' || dir.charAt(2) != 'u') {continue;}
      boolean has_only_digits = true;
      for (int i = 3; i < dir.length(); i++) {
        char c = dir.charAt(i);
        if (c < ascii_min || c > ascii_max) {has_only_digits = false; break;}
      }
      if (has_only_digits) {cpu_paths.add(dir);}
    }
    return cpu_paths.toArray(new String[0]);
  }
}
