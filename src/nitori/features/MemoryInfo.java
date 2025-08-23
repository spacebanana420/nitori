package nitori.features;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
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

  public MemoryInfo() {
    final ArrayList<String> keys = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();
    String info = fileio.readValue("/proc/meminfo");
    if (info == null) {return;}

    //Get keys and values from each line
    String line = "";
    for (int i = 0; i < info.length(); i++) {
      char c = info.charAt(i);
      if (c == '\n') {
        if (line.isEmpty()) {continue;}
        String[] key_value = getInfo(line);
        if (key_value == null) {continue;}
        keys.add(key_value[0]);
        values.add(key_value[1]);
      }
      else {line+=c;}
    }

    is_empty = keys.isEmpty();
    if (is_empty) {
      stdout.error("Error, no memory information was found and successfully parsed! System memory information is unavailable!");
      return;
    }
    memory_total = getValue("MemTotal", keys, values);
    memory_cached = getValue("MemTotal", keys, values);
    memory_available = getValue("Cached", keys, values);
    memory_free = getValue("MemFree", keys, values);
    memory_used = memory_total-memory_available;
  }

  //Get the key and value of a line and discard the kB unit at the end
  private String[] getInfo(String line) {
    String key = "";
    String value = "";
    int value_start = -1;

    //key
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == ':') {value_start = i+1; break;}
      key += c;
    }
    if (value_start == -1) {return null;}

    //value
    for (int i = 0; i < line.length(); i++) {value += line.charAt(i);}

    key = key.trim();
    value = value.replace(" kB", "").trim();
    return key.isEmpty() || value.isEmpty() ? null : new String[]{key, value};
  }

  private long getValue(String key, ArrayList<String> keys, ArrayList<String> values) {
    int value_i = -1;
    for (int i = 0; i < keys.size(); i++) {
      if (key.equals(keys.get(i))) {value_i = i;}
    }
    if (value_i == -1) {return -1;}
    try {return Long.parseLong(values.get(value_i));}
    catch(NumberFormatException e) {
      stdout.error("Failed to convert value of key" + key + " into a long");
      return -1;
    }
  }
}
