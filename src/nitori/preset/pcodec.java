package nitori.preset;

import nitori.io.*;
import java.util.ArrayList;
import java.io.File;

//Read and write presets
public class pcodec {
  public static void createPreset(String file_name, boolean is_root) {
    if (!is_root) {
      stdout.error("You need to run Nitori as root to create a preset!");
      return;
    }

    String preset_contents =
      """
      # Nitori preset file
      # You can define a set of commands to run and configurations to set by calling this preset
      # For example, you could make a power-saving preset which lowers the CPU clock speeds and laptop backlight brightness
      # Lines that start with "#" are comments and so they are ignored, remove this character to enable a setting
      # The following settings you can use are seen below
      
      # Set the CPU's minimum and maximum clock speeds in megahertz (MHz)
      #cpu_min=800
      #cpu_max=3800
      
      #Set the CPU's kernel governor
      #cpu_gov=powersave
      
      #Set the CPU's energy preference if supported
      #cpu_energy_mode=balance_performance
      
      #Reset the CPU's clock speeds to your hardware's limits (overrides cpu_min and cpu_max)
      #cpu_reset=true
      
      #Set the battery charge limit (in percentage %) if supported
      #battery_limit=60
      
      #Set the backlight brightness (in percentage %) if supported
      #backlight_brightness=30
      """;
    String file_path = "/etc/nitori/" + file_name + ".nitori";
    fileio.createDirectory("/etc/nitori/");

    if (file_name.isEmpty()) {
      stdout.error("You must provide a file name for the preset!");
      return;
    }
    if (fileio.fileExists(file_path)) {
      stdout.error("Could not create the preset file " + file_name + " because it already exists!");
      return;
    }

    boolean result = fileio.writeValue(file_path, preset_contents);
    if (result) {
      stdout.print("Preset file has been created at " + file_path + "\nYou can open it with a text editor to start configuring it!");
    }
  }

  public static void printPresetList() {
    File presets_f = new File("/etc/nitori/");
    if (!presets_f.isDirectory()) {
      stdout.print("No presets have been found since the path /etc/nitori/ does not exist yet!");
      return;
    }
    String[] subpaths = presets_f.list();
    if (subpaths == null) {
      stdout.print("No presets have been found in /etc/nitori/");
      return;
    }
    boolean found_preset = false;
    String message = "Avaliable presets in /etc/nitori/:";
    for (String path : subpaths) {
      if (path.contains(".nitori") && new File(path).isFile()) {
        message += "  * " + path.replaceFirst(".nitori", ""); //rework later into a proper file extension removal
        found_preset = true;
      }
    }
    if (!found_preset) {
      stdout.print("No presets have been found in /etc/nitori/");
      return;
    }
    stdout.print(message);
  }

  static NitoriPreset readPreset(String file_name) {
    String file_path = "/etc/nitori/" + file_name + ".nitori";
    fileio.createDirectory("/etc/nitori/");

    if (file_name.isEmpty()) {
      stdout.error("You must provide a file name for the preset!");
      return null;
    }
    if (!fileio.fileExists(file_path)) {
      stdout.error("Could not read the preset file " + file_name + " because it does not exist!");
      return null;
    }

    String file = fileio.readValue(file_path);
    return file == null ? null : new NitoriPreset(file);
  }
}

//Class to contain the settings of an individual preset file
class NitoriPreset {
  private String[] keys;
  private String[] values;
  boolean is_empty;

  NitoriPreset(String file) {
    var lines = new ArrayList<String>();
    String line = "";
    int len = file.length();
    for (int i = 0; i < len; i++) { //Separate a whole file by lines, ignoring comments #
      char c = file.charAt(i);
      if (c == '\n') {
        if (!line.isEmpty()) {lines.add(line);}
        line = "";
      }
      else if (c == '#') { //Skip all the characters until a new line is found
        for (int skip_i = i; skip_i < len; skip_i++) {
          if (file.charAt(i) == '\n') {i = skip_i+1; break;}
          i = len; //No new line existed so the entire rest of the file had to be skipped
        }
      }
      else {line += c;}
    }
    if (!line.isEmpty()) {lines.add(line);}

    var keys = new ArrayList<String>();
    var values = new ArrayList<String>();
    for (String l : lines) { //Get the keys (settings) and the values set for each key
      String[] setting = extractLineContents(l);
      if (setting != null) {
        keys.add(setting[0]);
        values.add(setting[1]);
      }
    }
    if (!keys.isEmpty() && !values.isEmpty()) {
      this.is_empty = false;
      this.keys = keys.toArray(new String[0]);
      this.values = values.toArray(new String[0]);
    }
  }

  String getValue(String key) {
    int key_i = -1;
    for (int i = 0; i < keys.length; i++) {
      if (key.equals(keys[i])) {key_i = i; break;}
    }
    return key_i != -1 ? values[key_i] : null;
  }

  boolean getValue_bool(String key) {
    String value = getValue(key);
    return value != null && value.equalsIgnoreCase("true");
  }

  int getValue_int(String key) {
    String value = getValue(key);
    return value != null ? fileio.valueToInt(value) : -1;
  }

  private String[] extractLineContents(String line) { //Array of 2 items, key and value respectively
    String key = "";
    String value = "";
    int value_start = -1;
    for (int i = 0; i < line.length(); i++) { //Get key
      char c = line.charAt(i);
      if (c == '=') {value_start = i+1; break;}
      key += c;
    }
    if (value_start == -1 || value_start == line.length()) {return null;}

    for (int i = value_start; i < line.length(); i++) { //Get value
      value += line.charAt(i);
    }
    key = key.trim();
    value = value.trim();
    if (key.isEmpty() || value.isEmpty()) {return null;}
    return new String[]{key, value};
  }
}
