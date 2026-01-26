package nitori.cli;
import nitori.io.stdout;

//Read the CLI, obtain the presence or values of a CLI argument
public class cli {
  public static boolean askedForHelp(String[] args) {return parser.hasArgument(args, "-h", "--help");}
  
  public static int[] cpuFrequencies(String[] args) {
    int min = parser.getArgumentInt(args, "-cmin", "--cpu-min");
    int max = parser.getArgumentInt(args, "-cmax", "--cpu-max");
    return new int[]{min, max};
  }
  
  public static String cpuGovernor(String[] args) {return parser.getArgumentValue(args, "-cg", "--cpu-governor");}
  public static String cpuEnergy(String[] args) {return parser.getArgumentValue(args, "-ce", "--cpu-energy-preference");}
  public static boolean cpuInfo(String[] args) {return parser.hasArgument(args, "-ci", "--cpu-info");}
  public static boolean cpuReset(String[] args) {return parser.hasArgument(args, "-cr", "--cpu-reset");}
  
  public static byte batteryPercentage(String[] args) {return parser.getArgumentByte(args, "-b", "--battery-set");}
  public static boolean batteryInfo(String[] args) {return parser.hasArgument(args, "-bi", "--battery-info");}
  
  public static byte backlightPercentage(String[] args) {return parser.getArgumentByte(args, "-l", "--backlight-set");}
  public static boolean backlightInfo(String[] args) {return parser.hasArgument(args, "-li", "--backlight-info");}
  public static boolean saveBacklight(String[] args) {return parser.hasArgument(args, "-ls", "--backlight-save");}
  public static boolean restoreBacklight(String[] args) {return parser.hasArgument(args, "-lr", "--backlight-restore");}
  
  public static String suspendSystem(String[] args) {
    int i = parser.findArgumentIndex(args, "-s");
    if (i == -1) {i = parser.findArgumentIndex(args, "--suspend");}
    if (i == -1) {return null;}
    
    if (!parser.checkValue(args, i)) {return "mem";}
    return args[i+1].trim();
  }
  public static boolean hibernateSystem(String[] args) {return parser.hasArgument(args, "-sh", "--hibernate");}
  public static boolean suspendStates(String[] args) {return parser.hasArgument(args, "-ss", "--suspend-states");}

  public static boolean memoryInfo(String[] args) {return parser.hasArgument(args, "-m", "--memory-info");}

  public static String runPreset(String[] args) {return parser.getArgumentValue(args, "-p", "--preset");}
  public static String createPreset(String[] args) {return parser.getArgumentValue(args, "-pc", "--create-preset");}
  public static boolean listPresets(String[] args) {return parser.hasArgument(args, "-pl", "--list-presets");}
  
  public static boolean quietOutput(String[] args) {return parser.hasArgument(args, "-q", "--quiet");}
  public static boolean verboseOutput(String[] args) {return parser.hasArgument(args, "-v", "--verbose");}
  public static boolean debugOutput(String[] args) {return parser.hasArgument(args, "--debug");}

  public static boolean countProcesses(String[] args) {return parser.hasArgument(args, "-proc", "--process-count");}
  public static boolean listProcesses(String[] args) {return parser.hasArgument(args, "-procl", "--process-list");}
  public static String findProcess(String[] args) {return parser.getArgumentValue(args, "-procf", "--process-find");}
}

//Internal CLI parsing
class parser {
  static int findArgumentIndex(String[] args, String find_arg) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals(find_arg)) {return i;}
    }
    return -1;
  }
  
  static boolean hasArgument(String[] args, String... find_arg) {
    for (String arg : find_arg) {
      int i = findArgumentIndex(args, arg);
      if (i != -1) {return true;}
    }
    return false;
  }
  
  static String getArgumentValue(String[] args, String... find_arg) {
    for (String arg : find_arg) {
      int i = findArgumentIndex(args, arg);
      if (i == -1) {return null;}
      if (!checkValue(args, i)) {
        stdout.error("The argument " + args[i] + " must be followed by a value!");
        return null;
      }
      String value = args[i+1].trim();
      if (value.isEmpty()) {return null;}
      return value;
    }
    return null;
  }
  
  //-1 is used to determine the CLI argument was not used or was not followed by a value
  //-2 is used to determine that it was indeed used but it is incorrect
  static int getArgumentInt(String[] args, String... find_arg) {
    String value = getArgumentValue(args, find_arg);
    if (value == null) {return -1;}
    if (value.length() > 9) {
      stdout.error("The value " + value + " passed as a CLI argument is invalid, it is too big to represent a valid number!");
      return -2;
    }
    int num = strToInt(value);
    if (num >= 0) {return num;} //Negative values are not necessary anywhere
    stdout.error("The value " + value + " passed as a CLI argument is invalid, it must be positive!");
    return -2;
  }
  
  static byte getArgumentByte(String[] args, String... find_arg) {
    int num_i = getArgumentInt(args, find_arg);
    if (num_i == -1) {return -1;}
    byte num_b = (byte)(num_i);
    
    //Byte.parseByte would have accepted shorts, integers, longs, etc as bytes
    //Resulting in values above 255 representing valid byte values, leading to incorrect behavior
    return num_b == num_i ? num_b : -128;
  }
  
  static boolean checkValue(String[] args, int i) {
    if (i == args.length-1) {return false;}
    String value = args[i+1];
    return !value.isEmpty() && value.charAt(0) != '-';
  }
  
  private static int strToInt(String num) {
    try {return Integer.parseInt(num);}
    catch(NumberFormatException e) {return -1;}
  }
}
