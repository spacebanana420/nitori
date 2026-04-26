package nitori.features;

import nitori.io.*;
import java.util.ArrayList;

//Operating system GPU memory stats
//Each GPUMemory instance represents the memory usage of a single GPU chip, many can be combined for multi-GPU setups
public class GPUMemory {
  //Memory is stored in kilobytes (MB)
  public float vram_total;
  public float vram_used;
  public float gtt_total;
  public float gtt_used;
  public float memory_used;

  public String name;
  private boolean is_empty = true;

  public static GPUMemory[] getGraphicsMemory() {
    String[] paths = fileio.getPaths("/sys/class/drm/");
    byte decimal_0 = (byte)'0';
    byte decimal_9 = (byte)'9';
    var GPUs = new ArrayList<GPUMemory>();

    //Filter paths to only get those representing a GPU
    //Valid names are card0, card1, card2, etc
    for (String path : paths) {
      if (path.length() < 5) continue; //card0 to card9 has 5 characters
      if (!path.startsWith("card")) continue;
      
      //Check if the characters after "card" are valid digits between 0 and 9
      boolean correctName = true;
      for (int i = 4; i < path.length(); i++) {
        byte decimal_char = (byte)path.charAt(i);
        if (decimal_char < decimal_0 || decimal_char > decimal_9) {
          correctName = false;
          break;
        }
      }
      if (!correctName) continue;
      GPUs.add(new GPUMemory(path));
    }
    return GPUs.toArray(new GPUMemory[0]);
  }

  //Get all VRAM and GTT values and convert from bytes to megabytes
  public GPUMemory(String cardLabel) {
    this.name = cardLabel;
    String basePath = "/sys/class/drm/"+cardLabel+"/device/";
    if (!fileio.directoryExists(basePath)) {
      stdout.error("Error retrieving GPU memory information, the card label " + cardLabel + " does not exist\nValid card labels include card0, card1, card2, etc");
      return;
    }

    this.is_empty = false;
    //These values are measured in bytes, will be converted to megabytes
    long vram_total = fileio.readLong(basePath+"mem_info_vram_total");
    long vram_used = fileio.readLong(basePath+"mem_info_vram_used");
    long gtt_total = fileio.readLong(basePath+"mem_info_gtt_total");
    long gtt_used = fileio.readLong(basePath+"mem_info_gtt_used");
    
    this.vram_total = (float)vram_total / 1000000;
    this.vram_used = (float)vram_used / 1000000;
    this.gtt_total = (float)gtt_total / 1000000;
    this.gtt_used = (float)gtt_used / 1000000;
  }

  public String getVramTotal() {return this.is_empty || this.vram_total < 0 ? "N/A" : this.vram_total + " MB";}
  public String getVramUsed() {return this.is_empty || this.vram_used < 0 ? "N/A" : this.vram_used + " MB";}
  public String getGttTotal() {return this.is_empty || this.gtt_total < 0 ? "N/A" : this.gtt_total + " MB";}
  public String getGttUsed() {return this.is_empty || this.gtt_used < 0 ? "N/A" : this.gtt_used + " MB";}
}
