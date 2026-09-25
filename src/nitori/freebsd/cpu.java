package nitori.freebsd;

import nitori.numops;
import nitori.io.stdout;
import nitori.io.process;

public class cpu {
  public static void setClockSpeed(int speed, int currentSpeed, String availableFrequencies) {
    if (speed == currentSpeed) {
      stdout.print("Specified clock speed of "+speed+" MHz is the same as the current CPU speed, skipping.");
      return;
    }
    Process p = process.exec("sysctl", "dev.cpu.0.freq="+speed);
    int resultFreq = numops.toInt(getResultFrequency(new String(process.readOutput(p))));
    if (resultFreq == speed) stdout.print("CPU clock speed was set to "+resultFreq+" MHz.");
    else stdout.print("Provided clock speed of "+speed+" MHz is not supported\nSupported frequencies:"+availableFrequencies+"\nCPU clock speed was set to "+resultFreq+" MHz instead.");
  }

  //Sets clock speed to highest supported value
  public static void resetClockSpeed(SystemInfo info) {
    String speed = info.getHighestFrequency();
    Process p = process.exec("sysctl", "dev.cpu.0.freq="+speed);
    stdout.print("CPU clock speed was set to the highest supported value, which is "+speed+" MHz");
  }
  
  //Sysctl tells the resulting CPU clock speed after changing it, this function retrieves it
  //It follows the format of "speed1 -> speed2" (for example "2200 -> 3800", extracts the number 3800)
  private static String getResultFrequency(String stdout) {
    var freq = new StringBuilder();
    boolean copy = false;
    for (int i = 0; i < stdout.length(); i++) {
      char c = stdout.charAt(i);
      if (copy) freq.append(c);
      else if (c == '>') copy = true;
    }
    return freq.toString().trim();
  }
}
