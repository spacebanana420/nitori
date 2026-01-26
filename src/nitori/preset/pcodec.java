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
      "# Nitori preset file"
      + "\n# You can define a set of commands to run and configurations to set by calling this preset"
      + "\n# For example, you could make a power-saving preset which lowers the CPU clock speeds and laptop backlight brightness"
      + "\n# Lines that start with \"#\" are comments and so they are ignored, remove this character to enable a setting"
      + "\n# The following settings you can use are seen below"
      
      + "\n\n# Set the CPU's minimum and maximum clock speeds in megahertz (MHz)"
      + "\n#cpu_min=800"
      + "\n#cpu_max=3800"

      + "\n\n#Set the CPU's kernel governor"
      + "\n#cpu_gov=powersave"

      + "\n\n#Set the CPU's energy preference if supported"
      + "\n#cpu_energy_mode=balance_performance"

      + "\n\n#Reset the CPU's clock speeds to your hardware's limits (overrides cpu_min and cpu_max)"
      + "\n#cpu_reset=true"

      + "\n\n#Set the battery charge limit (in percentage %) if supported"
      + "\n#battery_limit=60"

      + "\n\n#Set the backlight brightness (in percentage %) if supported"
      + "\n#backlight_brightness=30"
    ;
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
      if (path.contains(".nitori") && new File("/etc/nitori/"+path).isFile()) {
        message += "\n  * " + path.replaceFirst(".nitori", ""); //rework later into a proper file extension removal
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
  boolean is_empty = true;

  NitoriPreset(String file) {
    ArrayList<String> lines = fileio.strToLines(file, '#');
    var keys = new ArrayList<String>();
    var values = new ArrayList<String>();
    for (String l : lines) { //Get the keys (settings) and the values set for each key
      String[] setting = extractLineContents(l);
      if (setting != null) {
        stdout.print_debug("Found setting in preset\n  * Key: " + setting[0] + "\n  * Value: " + setting[1]);
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
    if (value == null) return -1;

    try {return Integer.parseInt(value);}
    catch (NumberFormatException e) {return -1;}
  }

  private String removeComments(String line) {
    StringBuilder newline = new StringBuilder();
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == '#') {break;}
      newline.append(c);
    }
    return newline.toString();
  }

  private String[] extractLineContents(String line) { //Array of 2 items, key and value respectively
    StringBuilder key = new StringBuilder();
    StringBuilder value = new StringBuilder();
    int value_start = -1;

    //Get key
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == '=') {value_start = i+1; break;}
      key.append(c);
    }
    if (value_start == -1 || value_start == line.length()) {return null;}

    //Get value
    for (int i = value_start; i < line.length(); i++) {value.append(line.charAt(i));}

    String key_str = key.toString().trim();
    String value_str = value.toString().trim();
    if (key.length() == 0 || value.length() == 0) {return null;}
    return new String[]{key_str, value_str};
  }
}
