package com.huxton.common.utils;

public class NumberUtils {

  private static long DEFAULT_LONG = 0L;
  private static int DEFAULT_INTEGER = 0;
  private static double DEFAULT_DOUBLE = 0D;
  private static float DEFAULT_FLOAT = 0F;

  public static long getNumber(Long number) {

    return number == null ? DEFAULT_LONG : number;
  }

  public static int getNumber(Integer number) {

    return number == null ? DEFAULT_INTEGER : number;
  }

  public static double getNumber(Double number) {

    return number == null ? DEFAULT_DOUBLE : number;
  }

  public static double getNumber(Float number) {

    return number == null ? DEFAULT_FLOAT : number;
  }


  public static Long parseLong(String number) {
    try {
      return number == null ? null : Long.parseLong(number);
    } catch (Exception e) {
      return null;
    }
  }
  public static Integer parseInteger(String number) {
    try {
      return number == null ? null : Integer.parseInt(number);
    } catch (Exception e) {
      return null;
    }
  }
  public static Double parseDouble(String number) {
    try {
      return number == null ? null : Double.parseDouble(number);
    } catch (Exception e) {
      return null;
    }
  }
  public static Float parseFloat(String number) {
    try {
      return number == null ? null : Float.parseFloat(number);
    } catch (Exception e) {
      return null;
    }
  }



}
