package com.cts.swift.util;

public class InputMessageReader {

  private String inputMessage;

  public InputMessageReader(String message) {

    this.inputMessage = message;
  }

  public String getSwiftMsgBlock(int blockNumber) {

    String mtMsgBlock = "";

    switch (blockNumber) {
      case 1:
        mtMsgBlock = getSwiftMessage(inputMessage.indexOf("{1:"), inputMessage.indexOf("{2:"));
        break;
      case 2:
        mtMsgBlock = getSwiftMessage(inputMessage.indexOf("{2:"), inputMessage.indexOf("{3:"));
        break;
      case 3:
        mtMsgBlock = getSwiftMessage(inputMessage.indexOf("{3:"), inputMessage.indexOf("{4:"));
        break;
      case 4:
        mtMsgBlock = getSwiftMessage(inputMessage.indexOf("{4:"), inputMessage.indexOf("{5:"));
        break;
      default:
        mtMsgBlock = "Invalid Message Block Numer Provided";
        break;
    }
    
    return mtMsgBlock;

  }

  private String getSwiftMessage(int startIndex, int endIndex) {

    return inputMessage.substring(startIndex + 3, endIndex - 1);
  }

}
