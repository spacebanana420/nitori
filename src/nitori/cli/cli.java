package nitori.cli;

public class cli {
  public static boolean askedForHelp(String[] args) {return parser.hasArgument(args, "-h") || parser.hasArgument(args, "--help");}
}

class parser {
  static int findArgumentIndex(String[] args, String find_arg) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals(find_arg)) {return i;}
    }
    return -1;
  }
  
  static boolean hasArgument(String[] args, String find_arg) {return findArgumentIndex(args, find_arg) != -1;}
  
  static String getArgumentValue(String[] args, String find_arg) {
    int i = findArgumentIndex(args, find_arg);
    if (i == -1 || i == args.length-1) {return null;}
    String value = args[i+1].trim();
    if (value.length() == 0) {return null;}
    return value;
  }
}
