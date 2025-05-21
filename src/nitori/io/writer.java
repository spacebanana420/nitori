package nitori.io;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

class writer {  
  static boolean writeValue(String path, String content) {
    byte[] string_data = content.getBytes();
    try {
      var output = new FileOutputStream(path);
      output.write(string_data);
      return true;
    } catch(IOException e) {return false;}
  }
  
  static String readValue(String path) {
    try {
      byte[] data = Files.readAllBytes(Path.of(path));
      return new String(data).trim();
    } catch(IOException e) {return null;}
  }
  
  static int valueToInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch(NumberFormatException e) {return -1;}
  }
  
  static byte valueToByte(String value) {
    try {
      return Byte.parseByte(value);
    } catch(NumberFormatException e) {return -1;}
  }
}
