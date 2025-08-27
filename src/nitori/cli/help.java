package nitori.cli;

public class help {
  private static String title() {return "Nitori version 1.0.1";}
  
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
      + "\n  * -ce (--cpu-energy-preference) <mode>     Sets the CPU energy balance preference if available"
      + "\n  * -ci (--cpu-info)                         Displays info about the system's CPU and supported speeds and governors"
      + "\n  * -cr (--cpu-reset)                        Sets the minimum and maximum clock speeds"

      + "\n\n[Memory-related arguments]"
      + "\n  * -m (--memory-info)                       Displays OS memory size, usage and availability"
      
      + "\n\n[Battery-related arguments]"
      + "\n  * -b (--battery-set) <percentage>          Sets the battery charge limit percentage if supported"
      + "\n  * -bi (--battery-info)                     Displays info about the system's battery"
      
      + "\n\n[Backlight-related arguments]"
      + "\n  * -l (--backlight-set) <percentage>        Sets the screen backlight brightness percentage"
      + "\n  * -li (--backlight-info)                   Displays the current backlight percentage"
      
      + "\n\n[Suspension-related arguments]"
      + "\n   * -s (--suspend)                           Suspends the system to RAM if available"
      + "\n   * -s (--suspend) <state>                   Suspends the system according to a supported given state"
      + "\n   * -sh (--hibernate)                        Suspends the system by hibernating to disk"
      + "\n   * -ss (--suspend-states)                   Lists the supported and available suspend states"

      + "\n\n[Preset-related arguments]"
      + "\n   * -p (--preset) <preset name>             Executes a Nitori preset if available"
      + "\n   * -pc (--create-preset) <preset name>     Creates a new preset file in /etc/nitori/"
      + "\n   * -pl (--list-presets)                    Lists available presets if any exists"
      
      + "\n\n[Other arguments]"
      + "\n  * -q (--quiet)                             Disables printing to standard output"
      + "\n  * -v (--verbose)                           Displays more status messages on standard output"
      + "\n  * --debug                                  Displays even more status messages on standard output"
      
      + "\n\n[Symbol definitions]"
      + "\n GB  Gigabytes"
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
