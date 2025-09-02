package nitori.features;

import java.util.ArrayList;
import nitori.io.*;

//Generic class used for parsing /proc/meminfo as well as /proc/<PID>/status
class MemData {
  final ArrayList<String> keys = new ArrayList<String>();
  final ArrayList<String> values = new ArrayList<String>();
  boolean is_empty = true;

  MemData(String path) {
    ArrayList<String> info = fileio.readLines(path);
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
    stdout.print_debug("List of memory keys " + keys + "\n\nList of memory values " + values);
  }

  long getValue(String key) {
    int value_i = -1;
    for (int i = 0; i < keys.size(); i++) {
      if (key.equals(keys.get(i))) {
        value_i = i;
        break;
      }
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

  //Get the key and value of a line and discard the kB unit at the end
  private static String[] getInfo(String line) {
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
}
