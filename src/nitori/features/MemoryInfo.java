package nitori.features;

import java.lang.reflect.Array;
import java.util.ArrayList;
import nitori.io.*;

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
    final var keys = new ArrayList<String>();
    final var values = new ArrayList<String>();
    ArrayList<String> info = fileio.readLines("/proc/meminfo");
    if (info == null) {return;}

    //Get keys and values from each line
    for (String line : info) {
      String[] key_value = getInfo(line);
      if (key_value == null) {continue;}
      stdout.print_debug("Adding key " + key_value[0] + " and value " + key_value[1]);
      keys.add(key_value[0]);
      values.add(key_value[1]);
    }

    is_empty = keys.isEmpty();
    if (is_empty) {
      stdout.error("Error, no memory information was found and successfully parsed! System memory information is unavailable!");
      return;
    }
    stdout.print_debug("List of memory keys " + keys + "\n\nList of memory values " + values);
    memory_total = getValue("MemTotal", keys, values);
    memory_cached = getValue("Cached", keys, values);
    memory_available = getValue("MemAvailable", keys, values);
    memory_free = getValue("MemFree", keys, values);
    memory_used = memory_total-memory_available;

    swap_total = getValue("SwapTotal", keys, values);
    swap_free = getValue("SwapFree", keys, values);
    swap_cached = getValue("SwapCached", keys, values);
    system_uses_swap = swap_total != 0;
  }

  //Get the key and value of a line and discard the kB unit at the end
  private String[] getInfo(String line) {
    StringBuilder key = new StringBuilder();
    StringBuilder value = new StringBuilder();
    int value_start = -1;
    final String error_base = "Memory info parsing error at line " + line + "\n";

    //key
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == ':') {value_start = i+1; break;}
      key.append(c);
    }
    if (value_start == -1) {
      stdout.print_debug(error_base + "Start of value was not found");
      return null;
    }

    //value
    for (int i = value_start; i < line.length(); i++) {value.append(line.charAt(i));}

    String key_final = key.toString().trim();
    String value_final = value.toString().replace(" kB", "").trim();
    return (key_final.isEmpty()) || value_final.isEmpty() ? null : new String[]{key_final, value_final};
  }

  private long getValue(String key, ArrayList<String> keys, ArrayList<String> values) {
    int value_i = -1;
    for (int i = 0; i < keys.size(); i++) {
      if (key.equals(keys.get(i))) {value_i = i;}
    }
    if (value_i == -1) {
      stdout.error("Memory key " + key + " was not found!");
      return -1;
    }
    try {return Long.parseLong(values.get(value_i));}
    catch(NumberFormatException e) {
      stdout.error("Failed to convert value of key " + key + " into a long");
      return -1;
    }
  }
}
