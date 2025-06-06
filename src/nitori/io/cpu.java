package nitori.io;

import java.io.File;
import java.util.ArrayList;

public class cpu {
  public static boolean setFrequencies(int min_clock_speed, int max_clock_speed, CPUInfo cpu_info) {
    final String cpu_base_path = getBasePath();
    boolean setMinimum = false;
    boolean setMaximum = false;
    int min_speed = min_clock_speed * 1000; //conversion from MHz (user input) to KHz as it's measured in the system files
    int max_speed = max_clock_speed * 1000;
    
    //set minimum
    if (min_clock_speed > 0) {
      setMinimum = true;
      if (min_speed < cpu_info.hardware_min_frequency) {min_speed = cpu_info.hardware_min_frequency;}
      stdout.print("Setting minimum clock speed " + min_clock_speed + "MHz for all cores");
      
      for (String core : cpu_info.cores) {
        String path = cpu_base_path + core + "/cpufreq/scaling_min_freq";
        writer.writeValue(path, ""+min_speed);
      }
    }
    
    //set maxmimum
    if (max_clock_speed > 0) {
      setMaximum = true;
      if (max_speed > cpu_info.hardware_max_frequency) {max_speed = cpu_info.hardware_max_frequency;}
      stdout.print("Setting maximum clock speed " + max_clock_speed + "MHz for all cores");
      
      for (String core : cpu_info.cores) {
        String path = cpu_base_path + core + "/cpufreq/scaling_max_freq";
        writer.writeValue(path, ""+max_speed);
      }
    }
    return setMinimum || setMaximum;
  }
  
  public static boolean setGovernor(String governor, CPUInfo cpu_info) {
    if (!cpu_info.supportedGovernor(governor)) {return false;}
    
    final String cpu_base_path = getBasePath();
    for (String core : cpu_info.cores) {
      String path = cpu_base_path + core + "/cpufreq/scaling_governor";
      writer.writeValue(path, governor);
    }
    return true;
  }
  
  public static CPUInfo getInfo() {
    final String base_path = getBasePath();
    String[] cpu_paths = getCPUPaths(getBasePath());
    int core_count = cpu_paths.length;
    
    CPUInfo cpu_info = new CPUInfo(core_count, cpu_paths);
    for (int i = 0; i < core_count; i++) {
      String full_path = base_path + cpu_paths[i];
      String min_freq = writer.readValue(full_path+"/cpufreq/scaling_min_freq");
      String max_freq = writer.readValue(full_path+"/cpufreq/scaling_max_freq");
      String governor = writer.readValue(full_path+"/cpufreq/scaling_governor");
      cpu_info.min_frequency[i] = writer.valueToInt(min_freq);
      cpu_info.max_frequency[i] = writer.valueToInt(max_freq);
      cpu_info.governor[i] = governor;
    }
    if (new File("/sys/devices/system/cpu/cpu0/cpufreq/base_frequency").isFile()) {
      cpu_info.hardware_base_frequency = writer.valueToInt(writer.readValue("/sys/devices/system/cpu/cpu0/cpufreq/base_frequency"));
    }
    else {cpu_info.hardware_base_frequency = -1;}
    cpu_info.hardware_min_frequency = writer.valueToInt(writer.readValue("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
    cpu_info.hardware_max_frequency = writer.valueToInt(writer.readValue("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));
    
    String governors_file = writer.readValue("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors");
    var governors = new ArrayList<String>();
    String buffer = "";
    for (int i = 0; i < governors_file.length(); i++) {
      char c = governors_file.charAt(i);
      if (c == ' ' && buffer.length() > 0) {
        governors.add(buffer);
        buffer = "";
      }
      else {buffer += c;}
    }
    if (buffer.length() > 0) {governors.add(buffer);}
    cpu_info.available_governors = governors.toArray(new String[0]);
    
    return cpu_info;
  }
  
  public static float speedToMHz(int clock_speed) {return (float)clock_speed / 1000;}
  
  private static String getBasePath() {return "/sys/devices/system/cpu/";}
  
  private static String[] getCPUPaths(String base_path) {
    String[] paths = new File(base_path).list();
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
