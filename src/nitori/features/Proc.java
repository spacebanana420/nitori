package nitori.features;

import java.util.ArrayList;
import java.io.File;
import nitori.io.*;


public class Proc {
  public long pid;
  public boolean has_cmd = false;
  public String[] cmd = null;

  public Proc(long pid) {
    this.pid = pid;
    String command = fileio.readValue("/proc/"+pid+"/cmdline");
    if (command == null) {return;}

    //Each arugment of the command line ends with the escape character \000
    var cmd = new ArrayList<String>();
    var arg = new StringBuilder();
    for (int i = 0; i < command.length(); i++) {
      char c = command.charAt(i);
      if (c == '\000') {
        cmd.add(arg.toString());
        arg = new StringBuilder();
      }
      else {arg.append(c);}
    }
    if (!arg.isEmpty()) {cmd.add(arg.toString());} //fileio.readValue() trims strings, removing the last \000 character
    this.cmd = cmd.toArray(new String[0]);
    this.has_cmd = this.cmd.length > 0;
  }

  public String getCMDstr() {
    if (!has_cmd) {return "N/A";}
    var strbuilder = new StringBuilder();
    for (String arg : cmd) {strbuilder.append(arg + " ");}
    return strbuilder.toString().trim();
  }

  public String getName() {return has_cmd ? cmd[0] : "N/A";}

  public static Proc[] getSystemProcesses() {
    String[] paths = new File("/proc/").list();
    if (paths == null) {
      stdout.error("Failed to find any processes or /proc/ does not exist!");
      return null;
    }

    ArrayList<String> processes = new ArrayList<String>();
    for (String path : paths) {
      File f = new File("/proc/"+path);
      if (!f.isDirectory()) {continue;}
      if (!isProcess(path)) {continue;}
      processes.add(path);
    }
    if (processes.size() == 0) {
      stdout.error("Failed to find any processes!");
      return null;
    }

    var procs = new ArrayList<Proc>();
    for (String p : processes) {
      try {
        long pid = Long.parseLong(p);
        procs.add(new Proc(pid));
      }
      catch(NumberFormatException e) {stdout.error("Failed to convert process of ID " + p);}
    }
    return procs.toArray(new Proc[0]);
  }

  //Process directories only have digits in their name
  private static boolean isProcess(String name) {
    int digit_min = (int)'0';
    int digit_max = (int)'9';
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (c < digit_min || c > digit_max) {
        return false;
      }
    }
    return true;
  }
}