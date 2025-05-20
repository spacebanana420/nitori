package nitori.io;

public class CPUInfo { //add energy_performance_preference and available preferences later
  public int[] min_frequency;
  public int[] max_frequency;
  public String[] governor;
  
  public int hardware_base_frequency;
  public int hardware_min_frequency;
  public int hardware_max_frequency;
  public String[] available_governors;
  
  public String[] cpu_dirs;
  public int core_count;
  
  public CPUInfo(int core_count, String[] cpu_dirs) {
    if (core_count < 1) {return;}
    this.min_frequency = new int[core_count];
    this.max_frequency = new int[core_count];
    this.governor = new String[core_count];
    this.core_count = core_count;
    this.cpu_dirs = cpu_dirs;
  }
  
  public boolean supportedGovernor(String governor) {
    for (String available_g : available_governors) {
      if (governor.equals(available_g)) {return true;}
    }
    return false;
  }
}
