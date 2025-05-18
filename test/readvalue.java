import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class readvalue {
  public static void main(String[] args) {
    try {
      byte[] data = Files.readAllBytes(Path.of("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
      String value = new String(data);
      System.out.println(value);
    } catch(IOException e) {e.printStackTrace();}
  }
}
