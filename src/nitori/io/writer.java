package nitori.io;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.io.IOException;
import java.io.File;

public class writer {
  public static void cpu_setFrequencies(int min_clock_speed, int max_clock_speed) {
    String cpu_base_path = cpu_getBasePath();
    String[] cpus = new File(cpu_base_path).list();
    //set minimum
    if (min_clock_speed > 0) {for (int i = 0; i < cpus.length; i++) {
      String path = cpu_base_path + "/" + cpus[i] + "/cpufreq/scaling_min_freq";
      writeValue(path, ""+min_clock_speed);
    }}
    
    //set maxmimum
    if (max_clock_speed > 0) {for (int i = 0; i < cpus.length; i++) {
      String path = cpu_base_path + "/" + cpus[i] + "/cpufreq/scaling_max_freq";
      writeValue(path, ""+max_clock_speed);
    }}
  }
  
  public static int[] cpu_getCurrentFrequencies() {
    
  }
  
  private static String cpu_getBasePath() {return "/sys/devices/system/cpu/";}
  
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
