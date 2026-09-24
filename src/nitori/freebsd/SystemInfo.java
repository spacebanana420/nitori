package nitori.freebsd;

import nitori.io.process;
import java.util.ArrayList;


//Class which stores all relevant system/hardware information from FreeBSD
//Variables below are only assigned once they need to be accessed
public class SystemInfo {
  //CPU
  private String availableFrequencies = null;
  private int currentFrequency = -1;
  private String highestFrequency = null;
  private int coreCount = -1;

  //Laptop screen and battery
  private int screenBrightness = -1;
  private int batteryPercentage = -1;
  private int batteryState = -1;
  //private String[] suspendStates = null;
  //private int processCount = -1;

  //System RAM
  private int totalMemory = -1;
  private int freeMemory = -1;
  private int usedMemory = -1;
  private int wireMemory = -1;
  private int cachedMemory = -1;

  private ArrayList<String> osInfo; //Each line is "name: value"

  //Runs sysctl to retrieve system and hardware information, each line represents a value
  public SystemInfo() {
    Process p = process.exec(
      "sysctl",
      "dev.cpu.0.available_freqs",
      "dev.cpu.0.freq",
      "hw.ncpu",

      "hw.acpi.video.lcd0.brightness",

      "hw.acpi.battery.life",
      "hw.acpi.battery.state",

      "hw.physmem",
      "vm.stats.vm.v_free_count",
      "vm.stats.vm.v_active_count",
      "vm.stats.vm.v_wire_count",
      "vm.stats.vm.v_cache_count"
    );
    String output = new String(process.readOutput(p));

    var lines = new ArrayList<String>();
    var line = new StringBuilder();
    for (int i = 0; i < output.length(); i++) {
      char c = output.charAt(i);
      if (c != '\n') {
        line.append(c);
        continue;
      }
      lines.add(line.toString());
      line = new StringBuilder();
    }
    if (line.length() != 0) lines.add(line.toString());

    this.osInfo = lines;
  }

  public String getAvailableFrequencies() {
    if (this.availableFrequencies == null) this.availableFrequencies = retrieveValue(this.osInfo, "dev.cpu.0.available_freqs");
    return this.availableFrequencies;
  }

  public int getCPUFrequency() {
    if (this.currentFrequency == -1) this.currentFrequency = retrieveInt(this.osInfo, "dev.cpu.0.freq");
    return this.currentFrequency;
  }

  public String getHighestFrequency() {
    if (this.highestFrequency == null) { //Get the first of the available frequencies which is highest (follows format "freq/power freq/power freq/power")
      String availableFreqs = getAvailableFrequencies();
      var freq = new StringBuilder();
      for (int i = 0; i < availableFreqs.length(); i++) {
        char c = availableFreqs.charAt(i);
        if (c == '/') break;
        freq.append(c);
      }
      this.highestFrequency = freq.toString().trim();
    }
    return this.highestFrequency;
  }

  public int getCoreCount() {
    if (this.coreCount == -1) this.coreCount = retrieveInt(this.osInfo, "hw.ncpu");
    return this.coreCount;
  }

  public int getTotalMemory() {
    if (this.totalMemory == -1) this.totalMemory = retrieveInt(this.osInfo, "hw.physmem");
    return this.totalMemory;
  }

  public int getFreeMemory() {
    if (this.freeMemory == -1) this.freeMemory = retrieveInt(this.osInfo, "vm.stats.vm.v_free_count");
    return this.freeMemory;
  }

  public int getWireMemory() {
    if (this.wireMemory == -1) this.wireMemory = retrieveInt(this.osInfo, "vm.stats.vm.v_wire_count");
    return this.wireMemory;
  }

  public int getCachedMemory() {
    if (this.cachedMemory == -1) this.cachedMemory = retrieveInt(this.osInfo, "vm.stats.vm.v_cache_count");
    return this.cachedMemory;
  }
  
  public int getBatteryPercentage() {
    if (this.batteryPercentage == -1) this.batteryPercentage = retrieveInt(this.osInfo, "hw.acpi.battery.life");
    return this.batteryPercentage;
  }

  public int getBatteryState() {
    if (this.batteryState == -1) this.batteryState = retrieveInt(this.osInfo, "hw.acpi.battery.state");
    return this.batteryState;
  }

  public int getScreenBrightness() {
    if (this.screenBrightness == -1) this.screenBrightness = retrieveInt(this.osInfo, "hw.acpi.video.lcd0.brightness");
    return this.screenBrightness;
  }
  

  //Each line has the information in the format of "name: value"
  //First check if the names match, if it does then retrieve and return the value
  private static String retrieveValue(ArrayList<String> lines, String name) {
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (!line.contains(name)) continue;
      
      var value = new StringBuilder();
      boolean startCopying = false;
      for (int c = 0; c < line.length(); c++) { //Retrieve the value
        char ch = line.charAt(c);
        if (startCopying) value.append(ch);
        else if (ch == ':') startCopying = true;
      }
      lines.remove(i);
      return value.toString().trim(); 
    }
    return null; //The setting was not found in the list
  }

  private static int retrieveInt(ArrayList<String> lines, String name) {
    String value = retrieveValue(lines, name);
    try {return Integer.parseInt(value);}
    catch (NumberFormatException e) {return -1;}
  }
}
