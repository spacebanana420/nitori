package nitori.io;

import java.io.File;

//Linux kernel suspension, supports userspace freeze, suspend to RAM, hibernation, etc
public class suspend {
  public static boolean suspendIsSupported() {
    return new File("/sys/power/state").isFile();
  }
  
  //public static void suspendSystem() {
    //fileio.writeValue("/sys/power/state", "mem");
  //}
  
  public static void suspendSystem(String state, String[] available_states) {
    boolean supported_state = false;
    for (String os_state : available_states) {
      if (state.equals(os_state)) {
        supported_state = true;
        break;
      }
    }
    if (!supported_state) {
      stdout.error("The specified sleep state " + state + " is not supported!");
      return;
    }
    fileio.writeValue("/sys/power/state", state);
  }
  
  public static String[] supportedStates() {
    String states = fileio.readValue("/sys/power/state");
    return fileio.extractWords(states);
  }
}
