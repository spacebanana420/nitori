package nitori.features;

import java.util.ArrayList;
import nitori.io.*;

//Generic class used for parsing /proc/meminfo as well as /proc/<PID>/status
//Used by Proc.java as well as MemoryInfo.java
class MemData {
  String[] keys = null;
  String[] values = null;
  boolean is_empty = true;

  MemData(String path, String... valid_keys) {
    var keys = new ArrayList<String>();
    var values = new ArrayList<String>();
    ArrayList<String> info = fileio.readLines(path);
    if (info == null) return;

    //Get keys and values from each line
    for (String line : info) {
      String[] key_value = getInfo(line); //Contains both a setting and its value
      if (key_value == null) continue;
      if (invalidKey(key_value[0], valid_keys)) continue;
      
      stdout.print_debug("Adding key " + key_value[0] + " and value " + key_value[1]);
      keys.add(key_value[0]);
      values.add(key_value[1]);
    }
    is_empty = keys.isEmpty();
    this.keys = keys.toArray(new String[0]);
    this.values = values.toArray(new String[0]);
    stdout.print_debug("List of memory keys " + keys + "\n\nList of memory values " + values);
  }

  long getValue(String key) {
    int value_i = -1;
    for (int i = 0; i < keys.length; i++) {
      if (key.equals(keys[i])) {
        value_i = i;
        break;
      }
    }
    if (value_i == -1) {
      stdout.error("Memory key " + key + " was not found!");
      return -1;
    }
    try {return Long.parseLong(values[value_i]);}
    catch(NumberFormatException e) {
      stdout.error("Failed to convert value of key " + key + " into a long");
      return -1;
    }
  }

  //Get the key and value of a line and discard the kB unit at the end
  private static String[] getInfo(String line) {
    StringBuilder key = new StringBuilder();
    StringBuilder value = new StringBuilder();
    int value_start = -1;

    //key
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == ':') {value_start = i+1; break;}
      key.append(c);
    }
    if (value_start == -1) {
      stdout.print_debug("Memory info parsing error at line " + line + "\n" + "Start of value was not found");
      return null;
    }

    //value
    for (int i = value_start; i < line.length(); i++) {value.append(line.charAt(i));}

    String key_final = key.toString().trim();
    String value_final = value.toString().replace(" kB", "").trim();
    return (key_final.isEmpty()) || value_final.isEmpty() ? null : new String[]{key_final, value_final};
  }

  //If the class was initialized with a set of valid keys, only those keys will be added to the class
  private static boolean invalidKey(String key, String... valid_keys) {
    if (valid_keys.length == 0) {return false;}
    for (String vkey : valid_keys) {if (key.equals(vkey)) {return false;}}
    stdout.print_debug("Skipping key " + key + " for not being in the list of accepted keys");
    return true;
  }
}
