package nitori.io;

import java.io.OutputStream;
import java.io.IOException;

//Simple module for executing processes and handling standard input and output
public class process {
  public static Process exec(String... args) {
    try {
      var builder = new ProcessBuilder(args);
      return builder.start();
    }
    catch (IOException e) {return null;}
  }

  public static byte[] readOutput(Process process) {
    try {return process.getInputStream().readAllBytes();}
    catch (IOException e) {return null;}
  }

  public static boolean writeInput(Process process, byte[] data) {
    try {
      OutputStream stream = process.getOutputStream();
      stream.write(data);
      stream.close();
      return true;
    }
    catch (IOException e) {return false;}
  }
}
