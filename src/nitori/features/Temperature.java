package nitori.features;

import nitori.io.fileio;
import java.util.ArrayList;

//Simple class that stores temperature information according to the thermal zones in /sys/class/thermal/
public class Temperature {
  public String type = null;
  public int temp = 0; //Celsius, stored in millidegrees

  //Get the temperature of all thermal zones
  public static Temperature[] getAllTemperatures() {
    int thermalZone = 0;
    var temps = new ArrayList<Temperature>();
    while (true) {
      String path = "/sys/class/thermal/thermal_zone"+thermalZone+"/";
      if (!fileio.directoryExists(path)) break;
      temps.add(new Temperature(path));
      thermalZone++;
    }
    return temps.toArray(new Temperature[0]);
  }

  Temperature(String basePath) {    
    String type = fileio.readValue(basePath+"type");
    int temp = fileio.readInt(basePath+"temp");
    this.type = type;
    this.temp = temp;
  }

  public String getTemperature(boolean simple) {
    if (this.temp <= 0) return "N/A";
    float roundedTemp = (float)(temp/100)/10; //Converted to degrees and rounded to 1 decimal case
    return simple ? roundedTemp+"" : roundedTemp+" °C"; //In simple mode, only the numerical value is returned (for clean stdout)
  }

  public String getType() {return this.type == null ? "N/A" : this.type;}
}
