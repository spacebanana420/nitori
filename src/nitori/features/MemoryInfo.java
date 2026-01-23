package nitori.features;

import nitori.io.*;

//Operating system memory statistics class
public class MemoryInfo {
  public boolean is_empty = true;

  //Memory is stored in kilobytes (kB)
  public long memory_total;
  public long memory_cached;
  public long memory_free;
  public long memory_available;
  public long memory_used;

  public boolean system_uses_swap;
  public long swap_total;
  public long swap_free;
  public long swap_cached;

  public MemoryInfo() {
    MemData data = new MemData("/proc/meminfo");
    is_empty = data.is_empty;
    if (is_empty) {
      stdout.error("Error, no memory information was found and successfully parsed! System memory information is unavailable!");
      return;
    }
    memory_total = data.getValue("MemTotal");
    memory_cached = data.getValue("Cached");
    memory_available = data.getValue("MemAvailable");
    memory_free = data.getValue("MemFree");
    memory_used = memory_total-memory_available;

    swap_total = data.getValue("SwapTotal");
    swap_free = data.getValue("SwapFree");
    swap_cached = data.getValue("SwapCached");
    system_uses_swap = swap_total != 0;
  }
}
