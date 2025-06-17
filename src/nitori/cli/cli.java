package nitori.cli;

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
  
  public static boolean quietOutput(String[] args) {return parser.hasArgument(args, "-q", "--quiet");}
  public static boolean verboseOutput(String[] args) {return parser.hasArgument(args, "-v", "--verbose");}
}

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
      if (i == -1 || i == args.length-1) {return null;}
      String value = args[i+1].trim();
      if (value.length() == 0) {return null;}
      return value;
    }
    return null;
  }
  
  //duplicate code with writer.java
  static int getArgumentInt(String[] args, String... find_arg) {
    String value = getArgumentValue(args, find_arg);
    if (value == null) {return -1;}
    try {return Integer.parseInt(value);}
    catch(NumberFormatException e) {return -1;}
  }
  
  static byte getArgumentByte(String[] args, String... find_arg) {
    String value = getArgumentValue(args, find_arg);
    if (value == null) {return -1;}
    try {return Byte.parseByte(value);}
    catch(NumberFormatException e) {return -1;}
  }
}
