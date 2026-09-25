package nitori;

import nitori.io.stdout;

//Number-related functions for converting strings to numeric variables
//In all string conversions, -1 is returned if conversion fails
public class numops {
  public static int toInt(String value) {
    try {return Integer.parseInt(value);}
    catch (NumberFormatException e) {
      stdout.error("Failed to convert the value "+value+" into a number (int)");
      return -1;
    }
  }

  public static byte toByte(String value) {
    try {return Byte.parseByte(value);}
    catch (NumberFormatException e) {
      stdout.error("Failed to convert the value "+value+" into a number (byte)");
      return -1;
    }
  }

  public static long toLong(String value) {
    try {return Long.parseLong(value);}
    catch (NumberFormatException e) {
      stdout.error("Failed to convert the value "+value+" into a number (long)");
      return -1;
    }
  }
}
