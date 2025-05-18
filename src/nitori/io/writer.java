package nitori.io;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

public class writer {
  public static void cpu_setFrequencies(int min_clock_speed, int max_clock_speed, CPUInfo cpu_info) {
    String cpu_base_path = cpu_getBasePath();
    String[] cpus = new File(cpu_base_path).list();
    
    //set minimum
    if (min_clock_speed > 0) {
      if (min_clock_speed < cpu_info.hardware_min_frequency) {min_clock_speed = cpu_info.hardware_min_frequency;}
      for (int i = 0; i < cpus.length; i++) {
        String path = cpu_base_path + cpus[i] + "/cpufreq/scaling_min_freq";
        writeValue(path, ""+min_clock_speed);
      }
    }
    
    //set maxmimum
    if (max_clock_speed > 0) {
      if (max_clock_speed > cpu_info.hardware_max_frequency) {max_clock_speed = cpu_info.hardware_max_frequency;}
      for (int i = 0; i < cpus.length; i++) {
        String path = cpu_base_path + cpus[i] + "/cpufreq/scaling_max_freq";
        writeValue(path, ""+max_clock_speed);
      }
    }
  }
  
  public static CPUInfo cpu_getInfo() { //some settings are not implemented yet
    final String base_path = cpu_getBasePath();
    String[] cpu_paths = cpu_getCPUPaths(cpu_getBasePath());
    int core_count = cpu_paths.length;
    
    CPUInfo cpu_info = new CPUInfo(core_count, cpu_paths);
    for (int i = 0; i < cpu_paths.length; i++) {
      String full_path = base_path + cpu_paths[i];
      String min_freq = readValue(full_path+"/cpufreq/scaling_min_freq");
      String max_freq = readValue(full_path+"/cpufreq/scaling_max_freq");
      String governor = readValue(full_path+"/cpufreq/scaling_governor");
      cpu_info.min_frequency[i] = valueToInt(min_freq);
      cpu_info.max_frequency[i] = valueToInt(max_freq);
      cpu_info.governor[i] = governor;
    }
    cpu_info.hardware_base_frequency = valueToInt(readValue("/sys/devices/system/cpu/cpu0/cpufreq/base_frequency"));
    cpu_info.hardware_min_frequency = valueToInt(readValue("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
    cpu_info.hardware_max_frequency = valueToInt(readValue("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));
    
    String governors_file = readValue("/sys/devices/system/cpu/cpu0/cpufreq/");
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
  
  private static String cpu_getBasePath() {return "/sys/devices/system/cpu/";}
  
  private static String[] cpu_getCPUPaths(String base_path) {
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
  
  private static boolean writeValue(String path, String content) {
    byte[] string_data = content.getBytes();
    try {
      var output = new FileOutputStream(path);
      output.write(string_data);
      return true;
    } catch(IOException e) {return false;}
  }
  
  private static String readValue(String path) {
    try {
      byte[] data = Files.readAllBytes(Path.of(path));
      return new String(data);
    } catch(IOException e) {return null;}
  }
  
  private static int valueToInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch(NumberFormatException e) {return -1;}
  }
}
