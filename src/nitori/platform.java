package nitori;

import nitori.io.stdout;

//Platform-related code, for checking operating system and root login
public class platform {
  private static String os = System.getProperty("os.name");
  private static boolean isRoot = System.getProperty("user.home").equals("/root");

  public static String operatingSystem() {return os;}
  public static boolean isLinux() {return os.equals("Linux");}
  public static boolean isFreeBSD() {return os.equals("FreeBSD");}
  
  public static boolean isRoot() {return isRoot;}
  public static void printPlatformError() {stdout.error("Your operating system "+os+" is not supported by Nitori!\nNitori only works on Linux-based systems or FreeBSD!");}
  public static void printRootError_cpu() {stdout.error("You must be root to be able to modify CPU configurations!");}
  public static void printRootError_battery() {stdout.error("You must be root to be able to modify battery charge limits!");}
  public static void printRootError_backlight() {stdout.error("You must be root to be able to set the screen brightness!");}
  public static void printRootError_suspend() {stdout.error("You must be root to be able to suspend the system!");}
}
