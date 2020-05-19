package com.cts.swift.parse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.cts.swift.constants.Constants;
import com.cts.swift.model.mt.MT103;
import com.cts.swift.model.pacs008.AccountIdentification4Choice;
import com.cts.swift.model.pacs008.ActiveCurrencyAndAmount;
import com.cts.swift.model.pacs008.CashAccount38;
import com.cts.swift.model.pacs008.CreditTransferTransaction43;
import com.cts.swift.model.pacs008.Document;
import com.cts.swift.model.pacs008.FIToFICustomerCreditTransferV09;
import com.cts.swift.model.pacs008.GenericAccountIdentification1;
import com.cts.swift.model.pacs008.LocalInstrument2Choice;
import com.cts.swift.model.pacs008.ObjectFactory;
import com.cts.swift.model.pacs008.PartyIdentification135;
import com.cts.swift.model.pacs008.PaymentIdentification13;
import com.cts.swift.model.pacs008.PaymentTypeInformation28;
import com.cts.swift.model.pacs008.PostalAddress24;
import com.cts.swift.util.InputMessageReader;
import com.cts.swift.util.SwiftParserUtil;

public class MT2MXConverter {

  private static final String INPUT_FILE = Constants.Swift_Input_Message;

  private static final String OUTPUT_FILE = Constants.PACS_008_Output;

  private static final ObjectFactory objectFactory = new ObjectFactory();

  public static void main(String[] args) throws JAXBException {

    InputMessageReader inputMessageReader = new InputMessageReader(readFile(INPUT_FILE));

    String inputMessageDataBlock = inputMessageReader.getSwiftMsgBlock(4);

    String applicationHeaderBlock = inputMessageReader.getSwiftMsgBlock(2);

    System.out.println("Application Header Block : " + applicationHeaderBlock);

    String messageType = getMessageType(applicationHeaderBlock);
    System.out.println("Message Type : " + messageType);

    SwiftParser swiftParser = new SwiftParser(inputMessageDataBlock);
    
    MT103 mt103 = null;
    switch (messageType) {
      case Constants.MT_103:
        // call MT 103 parser
        mt103 = swiftParser.parseMT103();
        break;
      case Constants.MT_202:
        // call MT 202 parser
        break;
      default:
        // call default parser
        break;
    }

    Document document = objectFactory.createDocument();
    FIToFICustomerCreditTransferV09 fIToFICustomerCreditTransferV09 = objectFactory
        .createFIToFICustomerCreditTransferV09();

    PaymentIdentification13 paymentIdentification13 = objectFactory.createPaymentIdentification13();
    PaymentTypeInformation28 paymentTypeInformation28 = objectFactory.createPaymentTypeInformation28();
    PartyIdentification135 partyIdentification135 = objectFactory.createPartyIdentification135();
    PartyIdentification135 partyIdentification = objectFactory.createPartyIdentification135();
    LocalInstrument2Choice localInstrument2Choice = objectFactory.createLocalInstrument2Choice();

    CashAccount38 cashAccount38 = objectFactory.createCashAccount38();
    CashAccount38 cashAccount = objectFactory.createCashAccount38();
    AccountIdentification4Choice accountIdentification4Choice = objectFactory.createAccountIdentification4Choice();
    AccountIdentification4Choice accountIdentification4Choice2 = objectFactory.createAccountIdentification4Choice();

    GenericAccountIdentification1 genericAccountIdentification1 = objectFactory.createGenericAccountIdentification1();
    GenericAccountIdentification1 genericAccountIdentification2 = objectFactory.createGenericAccountIdentification1();

    PostalAddress24 postalAddress24 = objectFactory.createPostalAddress24();

    // Prtry
    localInstrument2Choice.setPrtry(mt103.getTag23B().trim());

    List<CreditTransferTransaction43> cttList = new ArrayList<>();

    CreditTransferTransaction43 creditTransferTransaction = new CreditTransferTransaction43();

    ActiveCurrencyAndAmount activeCurrencyAndAmount = new ActiveCurrencyAndAmount();
    String tag32A[] = SwiftParserUtil.parseTag32A(mt103.getTag32A());
    // IntrBkSttlmAmt
    activeCurrencyAndAmount.setValue(new BigDecimal(tag32A[2]));
    creditTransferTransaction.setIntrBkSttlmAmt(activeCurrencyAndAmount);

    // InstrId
    paymentIdentification13.setInstrId(mt103.getTag20().trim());
    creditTransferTransaction.setPmtId(paymentIdentification13);

    paymentTypeInformation28.setLclInstrm(localInstrument2Choice);
    creditTransferTransaction.setPmtTpInf(paymentTypeInformation28);

    String tag50k[] = SwiftParserUtil.parseTag50K(mt103.getTag50K());
    // Dbtr-Name
    partyIdentification135.setNm(tag50k[1]);
    // ardLine
    List<String> adrLine = new ArrayList<>();
    adrLine.add(tag50k[2] + "\n" + tag50k[3]);
    postalAddress24.setAdrLine(adrLine);
    partyIdentification135.setPstlAdr(postalAddress24);
    creditTransferTransaction.setDbtr(partyIdentification135);

    // Debt Account
    genericAccountIdentification1.setId(tag50k[0]);
    accountIdentification4Choice.setOthr(genericAccountIdentification1);
    cashAccount38.setId(accountIdentification4Choice);
    creditTransferTransaction.setDbtrAcct(cashAccount38);

    String tag59[] = SwiftParserUtil.parseTag59(mt103.getTag59());
    // Cdtr - Name
    partyIdentification.setNm(tag59[1]);
    creditTransferTransaction.setCdtr(partyIdentification);
    // Cred Account
    genericAccountIdentification2.setId(tag59[0]);
    accountIdentification4Choice2.setOthr(genericAccountIdentification2);
    cashAccount.setId(accountIdentification4Choice2);
    creditTransferTransaction.setCdtrAcct(cashAccount);

    cttList.add(creditTransferTransaction);

    fIToFICustomerCreditTransferV09.setCdtTrfTxInf(cttList);

    document.setFIToFICstmrCdtTrf(fIToFICustomerCreditTransferV09);

    JAXBContext jaxbContext = JAXBContext.newInstance(Constants.PACS_008_Model);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    JAXBElement<Document> jaxbElement = new JAXBElement<>(new QName(Constants.DOCUMENT_ROOT), Document.class, document);

    StringWriter sw = new StringWriter();

    jaxbMarshaller.marshal(jaxbElement, sw);

    OutputStream outputStream = null;

    try {
      outputStream = new FileOutputStream(OUTPUT_FILE);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    jaxbMarshaller.marshal(jaxbElement, outputStream);
  }

  /**
   * Method to return content of the file passed as input
   * 
   * @param filePath
   * @return file content
   */
  private static String readFile(String filePath) {

    String swiftInputMessage = "";

    try {

      swiftInputMessage = new String(Files.readAllBytes(Paths.get(filePath)));

    } catch (IOException e) {
      e.printStackTrace();
    }

    return swiftInputMessage;
  }

  /**
   * EX inputMessage of I101YOURBANKXJKLU3003 - method have to return 101
   * 
   * @param messageBlock
   * @return message type from input string
   */
  private static String getMessageType(String messageBlock) {

    return messageBlock.substring(1, 4);
  }

}
