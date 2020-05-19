package com.cts.swift.util;

public class SwiftParserUtil { 
  
  private SwiftParserUtil() {

  }
  
  private static String SPLIT_STRING = "\\r?\\n";
  
  public static String[] parseTag32A(String tag32A) {

    String[] arrayTag32A = new String[3];
    arrayTag32A[0] = tag32A.substring(0, 6);
    arrayTag32A[1] = tag32A.substring(6, 9);
    arrayTag32A[2] = tag32A.substring(9, tag32A.indexOf(','));

    return arrayTag32A;
  } 

  
  public static String[] parseTag50K(String tag50K) {
    return tag50K.split(SPLIT_STRING);
  }
  
  public static String[] parseTag59(String tag59) {
    return tag59.split(SPLIT_STRING);
  }

}
