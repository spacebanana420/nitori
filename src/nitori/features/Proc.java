package nitori.features;

import java.util.ArrayList;
import java.io.File;
import nitori.io.*;


public class Proc {
  long pid;
  boolean has_cmd = false;
  String[] cmd = null;

  public Proc(long pid) {
    this.pid = pid;
    String command = fileio.readValue("/proc/"+pid+"/cmdline");
    if (command == null) {return;}

    //In /proc/<pid>/cmdline, a whole command is represented where each arugment ends with the escape character \000
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
    this.cmd = cmd.toArray(new String[0]);
    this.has_cmd = true;
  }

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
    //sort(procs);
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

/*  private static void sort(long[] processes) {
    for (int i = 0; i < processes.length; i++) {
      int higher_i = -1;
      for (int p = i+1; p < processes.length; p++) {
        if (processes[p] <= processes[i]) {continue;}
        if (higher_i == -1 || processes[p] > processes[higher_i]) {
          higher_i = p;
        }
      }
      if (higher_i == -1) {continue;}
      long temp = processes[higher_i];
      processes[higher_i] = processes[i];
      processes[i] = temp;
    }
  }*/
}