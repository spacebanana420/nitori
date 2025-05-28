package nitori.cli;

public class help {
  private static String title() {return "Nitori version 0.3";}
  
  public static void printHelp() {
    System.out.println(
      title()
      + "\nUsage: nitori <arguments>"
      
      + "\n\nList of arguments:"
      + "\n  * -h (--help)                              Displays this screen"
      
      + "\n\n[CPU-related arguments]"
      + "\n  * -cmin (--cpu-min) <clock speed>          Sets the minimum CPU clock speed in megahertz (MHz)"
      + "\n  * -cmax (--cpu-min) <clock speed>          Sets the maximum CPU clock speed in megahertz (MHz)"
      + "\n  * -cg (--cpu-governor) <governor>          Sets the CPU kernel governor"
      + "\n  * -ci (--cpu-info)                         Displays info about the system's CPU and supported speeds and governors"
      
      + "\n\n[Battery-related arguments]"
      + "\n  * -b (--battery-set) <percentage>          Sets the battery charge limit percentage if supported"
      + "\n  * -bi (--battery-info)                     Displays info about the system's battery"
      
      + "\n\n[Backlight-related arguments]"
      + "\n  * -l (--backlight-set) <percentage>        Sets the screen backlight brightness percentage"
      + "\n  * -li (--backlight-info)                   Displays the current backlight percentage"
      
      + "\n\n[Other arguments]"
      + "\n  * -q (--quiet)                             Disables printing to standard output"
      + "\n  * -v (--verbose)                           Displays more status messages on standard output"
      
      + "\n\n[Symbol definitions]"
      + "\n Hz  Hertz"
      + "\n MHz Megahertz"
      + "\n W   Watt"
      + "\n Wh  Watt-hour"
      + "\n A   Ampere"
      + "\n Ah  Ampere-hour"
      + "\n V   Volt"
    );
  }
  
  public static void printSmallHelp() {
    System.out.println(
      title()
      + "\nUsage: nitori <arguments>"
      
      + "\n\nRun \"nitori -h\" to see what you can do"
    );
  }
}
