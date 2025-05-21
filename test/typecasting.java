public class typecasting {
  public static void main(String[] args) {
    try {
      String s = "105.3";
      int i = Integer.parseInt(s);
      System.out.println(i);
      
      System.out.println(Byte.parseByte("270"));
    } catch(NumberFormatException e) {return;}
  }
}
