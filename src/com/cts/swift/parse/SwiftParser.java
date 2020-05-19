package com.cts.swift.parse;

import com.cts.swift.constants.Constants;
import com.cts.swift.model.mt.MT103;

public class SwiftParser {

  private String swiftMessage;

  public SwiftParser(String message) {

    this.swiftMessage = message;
  }

  public MT103 parseMT103() {

    // Mandatory Tags
    String tag20Value = "";
    String tag23BValue = "";
    String tag32AValue = "";
    String tag50aValue = "";
    String tag59Value = "";
    String tag71AValue = "";

    MT103 mt103 = new MT103();

    String[] arrOfStr = swiftMessage.split(":");

    for (int i = 0; i < arrOfStr.length; i++) {

      if (arrOfStr[i].equals(Constants.MT_20_TAG)) {
        tag20Value = arrOfStr[i + 1];
        mt103.setTag20(tag20Value);
        System.out.println("Tag 20 Value - " + tag20Value);
      } else if (arrOfStr[i].equals(Constants.MT_23B_TAG)) {
        tag23BValue = arrOfStr[i + 1];
        mt103.setTag23B(tag23BValue);
        System.out.println("Tag 23B Value - " + tag23BValue);
      } else if (arrOfStr[i].equals(Constants.MT_32A_TAG)) {
        tag32AValue = arrOfStr[i + 1];
        mt103.setTag32A(tag32AValue);
        System.out.println("Tag 32A Value - " + tag32AValue);
      } else if (arrOfStr[i].equals(Constants.MT_50K_TAG)) {
        tag50aValue = arrOfStr[i + 1];
        mt103.setTag50K(tag50aValue);
        System.out.println("Tag 50 Value - " + tag50aValue);
      } else if (arrOfStr[i].equals(Constants.MT_59_TAG)) {
        tag59Value = arrOfStr[i + 1];
        mt103.setTag59(tag59Value);
        System.out.println("Tag 59 Value - " + tag59Value);
      } else if (arrOfStr[i].equals(Constants.MT_71A_TAG)) {
        tag71AValue = arrOfStr[i + 1];
        mt103.setTag71A(tag71AValue);
        System.out.println("Tag 71A Value - " + tag71AValue);
      }
    }
    return mt103;
  }
}