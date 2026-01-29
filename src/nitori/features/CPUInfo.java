package nitori.features;

//Stores CPU information and status, for both viewing and modifying
//All frequency values are in KHz
//Arrays store the respective information to each CPU core
public class CPUInfo {
  //Current CPU configuration
  public int[] min_frequency;
  public int[] max_frequency;
  public String[] governor;
  public String[] energy_pref;

  //Hardware CPU specifications and limitations
  public int hardware_base_frequency;
  public int hardware_min_frequency;
  public int hardware_max_frequency;
  public String[] available_governors;
  public String[] energy_preferences;
  public boolean turbo_enabled;
  public boolean turbo_status_exists; //file might not exist
  
  public String[] cores; //The name of the directory of each core, for convenience
  public int core_count;
  
  //Unfiltered text as it's seen in the source files
  public String governor_raw;
  public String energy_pref_raw;
  
  public CPUInfo(int core_count, String[] cpu_dirs) {
    if (core_count < 1) {return;}
    this.min_frequency = new int[core_count];
    this.max_frequency = new int[core_count];
    this.governor = new String[core_count];
    this.energy_pref = new String[core_count];
    this.core_count = core_count;
    this.cores = cpu_dirs;
  }
  
  public boolean supportedGovernor(String governor) {return isInList(governor, available_governors);}
  
  public boolean cpuSupportsEnergyControl() {return energy_preferences != null;}
  public boolean supportedEnergyControl(String energy_mode) {return isInList(energy_mode, energy_preferences);}
  
  public String str_energyPrefs() {
    return cpuSupportsEnergyControl() ? energy_pref_raw : "N/A";
  }
  public String str_currentEnergyPref() {
    return cpuSupportsEnergyControl() ? energy_pref[0] : "N/A";
  }
  public String str_turboStatus() {
    if (!turbo_status_exists) {return "Unknown";}
    return turbo_enabled ? "Yes" : "No";
  }

  //Convenience functions for getting the CPU frequencies in MHz
  public float mhz_hardwareMinFreq() {return toMHz(this.hardware_min_frequency);}
  public float mhz_hardwareMaxFreq() {return toMHz(this.hardware_max_frequency);}
  public float mhz_currentMinFreq() {return toMHz(this.min_frequency[0]);}
  public float mhz_currentMaxFreq() {return toMHz(this.max_frequency[0]);}
  public float mhz_baseFreq() {return toMHz(this.hardware_base_frequency);}

  private float toMHz(int clock_speed) {return (float)clock_speed / 1000;}
  
  private boolean isInList(String keyword, String[] slist) {
    for (String element : slist) {
      if (keyword.equals(element)) {return true;}
    }
    return false;
  }
}
