package nitori.io;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;


//Used for reading and writing to the files which serve as an interface for kernel features
public class fileio {
  public static boolean writeValue(String path, String content) {
    byte[] string_data = content.getBytes();
    stdout.print_debug("[fileio debug] Writing to " + path);
    try {
      var output = new FileOutputStream(path);
      output.write(string_data);
      output.close();
      return true;
    } catch(IOException e) {
      if (content.length() > 10) {
        stdout.error("Failed to write file at path " + path);
      }
      else{stdout.error("Failed to write file at path " + path + " with value " + content);}
      return false;
    }
  }

  public static String readValue(String path) {
    try {
      byte[] data = Files.readAllBytes(Path.of(path));
      return new String(data).trim();
    }
    catch(IOException e) {
      stdout.error("Failed to read file at path " + path);
      return null;
    }
  }

  public static ArrayList<String> readLines(String path) {
    String file = readValue(path);
    if (file == null) {return null;}
    return getStrLines(file, false, '\0');
  }
  public static ArrayList<String> readLines(String path, char comment_char) {
    String file = readValue(path);
    if (file == null) {return null;}
    return getStrLines(file, true, comment_char);
  }
  public static ArrayList<String> strToLines(String str, char comment_char) {return getStrLines(str, true, comment_char);}

  public static boolean fileExists(String path) {return new File(path).isFile();}
  public static void createDirectory(String path) {new File(path).mkdirs();}

  public static int valueToInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch(NumberFormatException e) {return -1;}
  }

  public static byte valueToByte(String value) {
    try {
      return Byte.parseByte(value);
    } catch(NumberFormatException e) {return -1;}
  }
  
  //Some files have values separated by words
  //For example: performance powersave
  public static String[] extractWords(String line) {
    var words = new ArrayList<String>();
    String buffer = "";
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == ' ' && !buffer.isEmpty()) {
        words.add(buffer);
        buffer = "";
      }
      else {buffer += c;}
    }
    if (!buffer.isEmpty()) {words.add(buffer);}
    return words.toArray(new String[0]);
  }

  //Separating a string by lines, optional removal of comments
  private static ArrayList<String> getStrLines(String str, boolean remove_comments, char comment_char) {
    var lines = new ArrayList<String>();
    StringBuilder line = new StringBuilder();

    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '\n' && line.length() != 0) {
        String line_str = remove_comments ? removeComments(line.toString(), comment_char) : line.toString();
        lines.add(line_str);
        line = new StringBuilder();;
      }
      else {line.append(c);}
    }
    if (line.length() != 0) {lines.add(remove_comments ? removeComments(line.toString(), comment_char) : line.toString());}
    return lines;
  }

  private static String removeComments(String line, char comment_char) {
    StringBuilder newline = new StringBuilder();
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == comment_char) {break;}
      newline.append(c);
    }
    return newline.toString();
  }
}
