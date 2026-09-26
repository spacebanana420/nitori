package nitori;

import nitori.cli.*;
import nitori.io.*;
import nitori.freebsd.SystemInfo;

public class main {
  public static void main(String[] args) {
    if (args.length == 0) {help.printSmallHelp(); return;}
    if (cli.askedForHelp(args)) {help.printHelp(); return;}
    stdout.PRINT_LEVEL = stdout.getPrintLevel(args);

    boolean isLinux = platform.isLinux();
    boolean isFreeBSD = !isLinux && platform.isFreeBSD();
    if (!isLinux && !isFreeBSD) {
      platform.printPlatformError();
      return;
    }
    boolean ranAnyTask = isLinux ? runTasks_linux(args) : runTasks_freebsd(args);
    if (!ranAnyTask) help.printSmallHelp();
  }
  
  private static boolean runTasks_linux(String[] args) {
    final boolean ran_presets = tasks.runPresetTasks(args);

    //Run the different tasks in parallel, they are not dependant on each other
    //Each task function returns a boolean telling whether the user tried to run it
    final boolean[] ran_tasks = new boolean[7];
    Thread[] t = new Thread[7];
    t[0] = new Thread(() -> {ran_tasks[0] = tasks.runCPUTasks(args);});
    t[1] = new Thread(() -> {ran_tasks[1] = tasks.runBatteryTasks(args);});
    t[2] = new Thread(() -> {ran_tasks[2] = tasks.runBacklightTasks(args);});
    t[3] = new Thread(() -> {ran_tasks[3] = tasks.runSuspendTasks(args);});
    t[4] = new Thread(() -> {ran_tasks[4] = tasks.runMemoryTask(args);});
    t[5] = new Thread(() -> {ran_tasks[5] = tasks.runProcessTasks(args);});
    t[6] = new Thread(() -> {ran_tasks[6] = tasks.runTemperatureTasks(args);});
    for (Thread thread : t) {thread.start();}
    for (Thread thread : t) {
      try{thread.join();}
      catch(InterruptedException e) {e.printStackTrace(); return true;}
    }

    //If no task was run at all, this function returns false, so that main() knows it has to print the help screen
    for (boolean status : ran_tasks) {if (status) return true;}
    return ran_presets;
  }

  private static boolean runTasks_freebsd(String[] args) {
    final SystemInfo info = new SystemInfo();

    //Run the different tasks in parallel, they are not dependant on each other
    //Each task function returns a boolean telling whether the user tried to run it
    final boolean[] ran_tasks = new boolean[3];
    Thread[] t = new Thread[3];
    t[0] = new Thread(() -> {ran_tasks[0] = tasks_freebsd.runCPUTasks(args, info);});
    t[1] = new Thread(() -> {ran_tasks[1] = tasks_freebsd.runBatteryTasks(args, info);});
    t[2] = new Thread(() -> {ran_tasks[2] = tasks_freebsd.runMemoryTasks(args, info);});
    for (Thread thread : t) {thread.start();}
    for (Thread thread : t) {
      try{thread.join();}
      catch(InterruptedException e) {e.printStackTrace(); return true;}
    }

    //If no task was run at all, this function returns false, so that main() knows it has to print the help screen
    for (boolean status : ran_tasks) {if (status) return true;}
    return false;
  }
}
