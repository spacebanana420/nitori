import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class readvalue {
  public static void main(String[] args) {
    String value = new String(readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
    System.out.println(value);
    value = new String(readFile("/sys/devices/system/cpu/cpu0/cpufreq/base_frequency"));
    System.out.println(value);
  }
  
  static byte[] readFile(String path) {
    try {return Files.readAllBytes(Path.of(path));}
    catch(IOException e) {e.printStackTrace(); return null;}
  }
}
