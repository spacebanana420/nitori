package nitori;

import nitori.cli.*;
import nitori.io.*;

public class main {
  public static void main(String[] args) {
    if (args.length == 0) {help.printSmallHelp(); return;}
    if (cli.askedForHelp(args)) {help.printHelp(); return;}
    stdout.PRINT_LEVEL = stdout.getPrintLevel(args);

    if (!supportedOS()) {
      stdout.print("Unsupported OS! Nitori only works on Linux-based operating systems!");
      return;
    }
    boolean ran_task = runTasks(args);
    if (!ran_task) {help.printSmallHelp();}
  }
  
  private static boolean runTasks(String[] args) {
    final boolean root = isRoot();
    final boolean ran_presets = tasks.runPresetTasks(args, root);

    //Run the different tasks in parallel, they are not dependant on each other
    final boolean[] ran_tasks = new boolean[6];
    Thread[] t = new Thread[6];
    t[0] = new Thread(() -> {ran_tasks[0] = tasks.runCPUTasks(args, root);});
    t[1] = new Thread(() -> {ran_tasks[1] = tasks.runBatteryTasks(args, root);});
    t[2] = new Thread(() -> {ran_tasks[2] = tasks.runBacklightTasks(args, root);});
    t[3] = new Thread(() -> {ran_tasks[3] = tasks.runSuspendTasks(args, root);});
    t[4] = new Thread(() -> {ran_tasks[4] = tasks.runMemoryTask(args);});
    t[5] = new Thread(() -> {ran_tasks[5] = tasks.runProcessTasks(args);});
    for (Thread thread : t) {thread.start();}
    for (Thread thread : t) {
      try{thread.join();}
      catch(InterruptedException e) {e.printStackTrace(); return true;}
    }
    
    return ran_presets || ran_tasks[0] || ran_tasks[1] || ran_tasks[2] || ran_tasks[3] || ran_tasks[4] || ran_tasks[5];
  }

  private static boolean supportedOS() {return System.getProperty("os.name").equals("Linux");}
  private static boolean isRoot() {return System.getProperty("user.home").equals("/root");}
}
