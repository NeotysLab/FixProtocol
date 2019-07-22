package com.neotys.FIX;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import quickfix.field.MsgType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
 import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.MessageFactory;

import org.apache.commons.io.IOUtils;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.LocateReqd;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.StopPx;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

import com.neotys.FIX.customactions.FIxConnectAction;
import com.neotys.FIX.customactions.FixConnectActionEngine;
public class CLientApplication extends MessageCracker implements Application{
	private static volatile SessionID sessionID;
    static private TwoWayMap sideMap = new TwoWayMap();
    static private TwoWayMap typeMap = new TwoWayMap();
    static private TwoWayMap tifMap = new TwoWayMap();
    private String sender = null;
    private String target = null;
    private Initiator initiator ;
    private static String response;
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
 
	 
	@Override
	public void onCreate(SessionID sessionId) {
		/*
		 * This method is called when quickfix creates a new session. A session comes into and remains in existence for the life of the application. 
		 * Sessions exist whether or not a counter party is connected to it. As soon as a session is created, you can begin sending messages to it. 
		 * If no one is logged on, the messages will be sent at the time a connection is established with the counterparty.
		 */
		LocalDateTime dateStart = LocalDateTime.now();
		 System.out.println( dateStart + ": onCreate for sessionId : "
	                + sessionId);
		 
	}

	@Override
	public void onLogon(SessionID sessionId) {
		/* This callback notifies you when a valid logon has been established with a counter party. 
		 * This is called when a connection has been established and the FIX logon process has completed with both parties exchanging valid logon messages.
		 */
		LocalDateTime dateStart = LocalDateTime.now();
		 CLientApplication.sessionID = sessionId;
		 System.out.println(  dateStart+ " : OnLogon : sessionID : " + sessionId);
	}

	@Override
	public void onLogout(SessionID sessionId) {
		/*
		 * This callback notifies you when an FIX session is no longer online. 
		 * This could happen during a normal logout exchange or because of a forced termination or a loss of network connection.
		 */
		LocalDateTime dateStart = LocalDateTime.now();
		CLientApplication.sessionID = null;
		System.out.println(dateStart + " : OnLogout : sessionId : " +sessionId);
		
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		/* This callback provides you with a peek at the administrative messages that are being sent from your FIX engine to the counter party. 
		 * This is normally not useful for an application however it is provided for any logging you may wish to do. You may add fields in an adminstrative 
		 * message before it is sent.
		 */
		LocalDateTime dateStart = LocalDateTime.now();

		System.out.println(dateStart +" : toAdmin , Message : " + message + " , sessionID is : " + sessionId );
		
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		/*
		 * This callback notifies you when an administrative message is sent from a counterparty to your FIX engine. 
		 * This can be usefull for doing extra validation on logon messages such as for checking passwords. 
		 * Throwing a RejectLogon exception will disconnect the counterparty.
		 */
		LocalDateTime dateStart = LocalDateTime.now();

		System.out.println(dateStart+":  fromAdmin  : Message : " + message + " : sessionID : " + sessionId);
		
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		/*
		 * This is a callback for application messages that you are sending to a counterparty. 
		 * If you throw a DoNotSend exception in this function, the application will not send the message. 
		 * This is mostly useful if the application has been asked to resend a message such as an order that is no longer relevant for the current market. 
		 * Messages that are being resent are marked with the PossDupFlag in the header set to true; If a DoNotSend exception is thrown and the flag is set to true, 
		 * a sequence reset will be sent in place of the message. 
		 * If it is set to false, the message will simply not be sent. You may add fields before an application message before it is sent out.
		 */
 		LocalDateTime dateStart = LocalDateTime.now();
		System.out.println(dateStart+" :  toApp : Message : " + message + " : sessionID : " + sessionId);		
		// FixConnectActionEngine.PathOutputFile  ;
		//   String path="C:\\Users\\neoload\\Music\\FIX_messagesto_" + sessionId.toString() + "_" + dateStart.toString()+".cvs";
		   String path=response+"\\reponse1.cvs";
		   System.out.print("               " + path);
		File file = new File(path);
        try {
	        // If file doesn't exists, then create it
	        if (!file.exists()) {
	            file.createNewFile();
	        }

	       // FileWriter fw = new FileWriter(file.getAbsoluteFile());
	        BufferedWriter bw = new BufferedWriter( new FileWriter(file.getAbsoluteFile(),true));

	        // Write in file
	        bw.write(dateStart+" ; " + message.toString() +"\r\n");

	        // Close connection
	        bw.close();
	    }
	    catch(Exception e){
	        System.out.println(e);
	    }

	}


	@Override
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		/*
		 * This callback receives messages for the application. This is one of the core entry points for your FIX application. 
		 * Every application level request will come through here. If, for example, your application is a sell-side OMS, this is where you will get your new order requests. 
		 * If you were a buy side, you would get your execution reports here. If a FieldNotFound exception is thrown, the counterparty will receive a reject indicating 
		 * a conditionally required field is missing. The Message class will throw this exception when trying to retrieve a missing field, 
		 * so you will rarely need the throw this explicitly. You can also throw an UnsupportedMessageType exception. This will result in the counterparty getting a business 
		 * reject informing them your application cannot process those types of messages. 
		 * An IncorrectTagValue can also be thrown if a field contains a value that is out of range or you do not support.
		 */
		
		LocalDateTime dateStart = LocalDateTime.now();
		 //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd--HH:mm:ss");
			//LocalDateTime now = LocalDateTime.now();
			//dtf.format(now);
		System.out.println(dateStart+" : fromApp : Message : " + message + " : sessionID : " + sessionId);	
	//String path="C:\\Users\\neoload\\Music\\FIX_messagesfrom_"+ now.toString() + ".cvs";
	   String path=response+"\\reponse2.cvs";
       File file = new File(path);
      try {
       // If file doesn't exists, then create it
       if (!file.exists()) {
           file.createNewFile();
       }

      // FileWriter fw = new FileWriter(file.getAbsoluteFile());
       BufferedWriter bw = new BufferedWriter( new FileWriter(file.getAbsoluteFile(),true));

       // Write in file
       bw.write(dateStart +" : " + message.toString() +"\r\n");

       // Close connection
       bw.close();
   }
   catch(Exception e){
       System.out.println(e);
   }
	}
	
	public static void setResponse(String path3)
	{
	 response=path3;
	}
	
	
	
	public  void connector(String path) throws ConfigError, InterruptedException, SessionNotFound, IOException {
		
		
 		SessionSettings settings = new SessionSettings(path);
 	    Application application = new CLientApplication();
	    MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
	    LogFactory logFactory = new ScreenLogFactory( true, true, true, false);
	    quickfix.MessageFactory messageFactory = new DefaultMessageFactory();
	    LocalDateTime dateStarte = LocalDateTime.now();
	    initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
	    initiator.start();
		Read_Console(path);		 
	    while (sessionID == null) {
			LocalDateTime dateStart = LocalDateTime.now();
 	        Thread.sleep(1000);
	    }
	    LocalDateTime dateStart2 = LocalDateTime.now();
	    System.out.println(dateStart2 + " connector : the connection done! " );    
 	} 
	  
          public void disconnet() {
      		LocalDateTime dateStart = LocalDateTime.now();
        	System.out.println( dateStart+ " disconnet :  disconnected method created in the client application ");  
             initiator.stop();	
                     }
          private void send(quickfix.Message message, SessionID sessionID) {
        		  LocalDateTime dateStart = LocalDateTime.now();
              try {
                  Session.sendToTarget(message, sessionID);
                  //System.out.println(dateStart+": send :  Message is--------------->" +  message.toString()+ "\n");
               } catch (SessionNotFound e) {
                  System.out.println(e);
              }
          }
          public void logout() {
        	      Session.lookupSession(sessionID).logout();
        	      LocalDateTime dateStart33 = LocalDateTime.now();
        		  System.out.println( dateStart33+ " logout : logout done : sessionId : " + sessionID);	      
          }
          
          public void send(Order order) {
    	      LocalDateTime dateStart33 = LocalDateTime.now();
              String beginString = order.getSessionID().getBeginString();
              if (beginString.equals("FIX.4.0")) {
                  //System.out.println(dateStart33+" send : the type of message is  FIX.4.0 --------------->" +  sessionID);
                  send40(order);}
              else if (beginString.equals("FIX.4.1")) {
                  //System.out.println(dateStart33+" send : the type of message is  FIX.4.1--------------->" +  sessionID);
                  send41(order);
                  }
              else if (beginString.equals("FIX.4.2")) {
                 // System.out.println(dateStart33+" send : the type of message is  FIX.4.2 --------------->" +  sessionID);
                  send42(order);}
              else if (beginString.equals("FIX.4.3")) {
                //  System.out.println(dateStart33+" send : the type of message is  FIX.4.3 --------------->" +  sessionID);
                  send43(order);}
              else if (beginString.equals("FIX.4.4")) {
               //   System.out.println(dateStart33+" send: the type of message is  FIX.4.4--------------->" +  sessionID);
                  send44(order);}
              return;
          }

          public void send40(Order order) {
              quickfix.fix40.NewOrderSingle newOrderSingle = new quickfix.fix40.NewOrderSingle(
                      new ClOrdID(order.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
                      sideToFIXSide(order.getSide()), new OrderQty(order.getQuantity()),
                      typeToFIXType(order.getType()));

              send(populateOrder(order, newOrderSingle), CLientApplication.sessionID);
              
          }
          public void send41(Order order) {
              quickfix.fix41.NewOrderSingle newOrderSingle = new quickfix.fix41.NewOrderSingle(
                      new ClOrdID(order.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
                      sideToFIXSide(order.getSide()), typeToFIXType(order.getType()));
              newOrderSingle.set(new OrderQty(order.getQuantity()));
              send(populateOrder(order, newOrderSingle), CLientApplication.sessionID);
          }

          public void send42(Order order) {
              quickfix.fix42.NewOrderSingle newOrderSingle = new quickfix.fix42.NewOrderSingle(
                      new ClOrdID(order.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
                      sideToFIXSide(order.getSide()), new TransactTime(), typeToFIXType(order.getType()));
              newOrderSingle.set(new OrderQty(order.getQuantity()));
              send(populateOrder(order, newOrderSingle), CLientApplication.sessionID);
          }

          public void send43(Order order) {
              quickfix.fix43.NewOrderSingle newOrderSingle = new quickfix.fix43.NewOrderSingle(
                      new ClOrdID(order.getID()), new HandlInst('1'), sideToFIXSide(order.getSide()),
                      new TransactTime(), typeToFIXType(order.getType()));
              newOrderSingle.set(new OrderQty(order.getQuantity()));
              newOrderSingle.set(new Symbol(order.getSymbol()));
              send(populateOrder(order, newOrderSingle), CLientApplication.sessionID);
          }

          public void send44(Order order) {
              quickfix.fix44.NewOrderSingle newOrderSingle = new quickfix.fix44.NewOrderSingle(
                      new ClOrdID(order.getID()), sideToFIXSide(order.getSide()),
                      new TransactTime(), typeToFIXType(order.getType()));
              newOrderSingle.set(new OrderQty(order.getQuantity()));
              newOrderSingle.set(new Symbol(order.getSymbol()));
              newOrderSingle.set(new HandlInst('1'));
              send(populateOrder(order, newOrderSingle), CLientApplication.sessionID);
          }

          public quickfix.Message populateOrder(Order order, quickfix.Message newOrderSingle) {
              OrderType type = order.getType();
              if (type == OrderType.LIMIT)
                  newOrderSingle.setField(new Price(order.getLimit().doubleValue()));
              else if (type == OrderType.STOP) {
                  newOrderSingle.setField(new StopPx(order.getStop().doubleValue()));
              } else if (type == OrderType.STOP_LIMIT) {
                  newOrderSingle.setField(new Price(order.getLimit().doubleValue()));
                  newOrderSingle.setField(new StopPx(order.getStop().doubleValue()));
              }

              if (order.getSide() == OrderSide.SHORT_SELL
                      || order.getSide() == OrderSide.SHORT_SELL_EXEMPT) {
                  newOrderSingle.setField(new LocateReqd(false));
              }

              newOrderSingle.setField(tifToFIXTif(order.getTIF()));
              return newOrderSingle;
          }
          public Side sideToFIXSide(OrderSide side) {
              return (Side) sideMap.getFirst(side);
          }

          public OrderSide FIXSideToSide(Side side) {
              return (OrderSide) sideMap.getSecond(side);
          }

          public OrdType typeToFIXType(OrderType type) {
              return (OrdType) typeMap.getFirst(type);
          }

          public OrderType FIXTypeToType(OrdType type) {
              return (OrderType) typeMap.getSecond(type);
          }

          public TimeInForce tifToFIXTif(OrderTIF tif) {
              return (TimeInForce) tifMap.getFirst(tif);
          }

          public OrderTIF FIXTifToTif(TimeInForce tif) {
              return (OrderTIF) typeMap.getSecond(tif);
          }
          
          static {
              sideMap.put(OrderSide.BUY, new Side(Side.BUY));
              sideMap.put(OrderSide.SELL, new Side(Side.SELL));
              sideMap.put(OrderSide.SHORT_SELL, new Side(Side.SELL_SHORT));
              sideMap.put(OrderSide.SHORT_SELL_EXEMPT, new Side(Side.SELL_SHORT_EXEMPT));
              sideMap.put(OrderSide.CROSS, new Side(Side.CROSS));
              sideMap.put(OrderSide.CROSS_SHORT, new Side(Side.CROSS_SHORT));
              typeMap.put(OrderType.MARKET, new OrdType(OrdType.MARKET));
              typeMap.put(OrderType.LIMIT, new OrdType(OrdType.LIMIT));
         //   typeMap.put(OrderType.STOP, new OrdType(OrdType.STOP));
              typeMap.put(OrderType.STOP_LIMIT, new OrdType(OrdType.STOP_LIMIT));
              tifMap.put(OrderTIF.DAY, new TimeInForce(TimeInForce.DAY));
              tifMap.put(OrderTIF.IOC, new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
              tifMap.put(OrderTIF.OPG, new TimeInForce(TimeInForce.AT_THE_OPENING));
              tifMap.put(OrderTIF.GTC, new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
              tifMap.put(OrderTIF.GTX, new TimeInForce(TimeInForce.GOOD_TILL_CROSSING));

          }
          public void sendMsg(String path2) 
          {
        	  LocalDateTime dateStart = LocalDateTime.now();

            System.out.println(dateStart+ "   sendMsg: Sending MSG-----------> " );
        
                      BufferedReader reader;
              		try {
              			reader = new BufferedReader(new FileReader(
              					path2));
              			String line = reader.readLine();
              			while (line != null) {
              				// read next line
              				line = reader.readLine();
              				try {
               					line = this.getChangedSenderTarget(line);
      						} catch (Exception e1) {
      							// TODO Auto-generated catch block
      							e1.printStackTrace();
      						}
              				Order order = new Order(line);
                      	  LocalDateTime dateStart1 = LocalDateTime.now();

              				System.out.println(dateStart1 + " sendMsg : order : "+order.toString());

              				order.setSessionID(CLientApplication.sessionID);
              				order.setID(this.getID(line));
              				order.setSymbol(this.getSymbol(line));
              				//OrderEntryPanel.setSide(order, this.getSide(line));
              				order.setQuantity(this.getQty(line));
              				
              				try {
      							Thread.sleep(1000);
      						} catch (InterruptedException e) {
      							// TODO Auto-generated catch block
      							e.printStackTrace();
      						}
              			    send(order);
              			    /*
              				System.out.println("Modified Message-----------> " + line);
              				System.out.println("Order Side-----------> " + order.getSide());
              	            System.out.println("Order Quantity-----------> " + order.getQuantity());
              	            System.out.println("Order session-----------> " + order.getSessionID());
              	            System.out.println("Order ID-----------> " + order.getID());
              	            System.out.println("Order Symbol-----------> " + order.getSymbol());
                                       */
              				//System.out.println(order);
              				line = reader.readLine();
              			}
              			reader.close();
              		} catch (IOException e) {
              			e.printStackTrace();
              		}


      } 
          
          
          
          private String getSide(String s) {
          	String pattern = "(\\x0154|^54)=(.)\\x01";
          	Pattern p = Pattern.compile(pattern);
          	Matcher m = p.matcher(s);
          	m.find();
          	return m.group(2);
          }

          private String getSymbol(String s) {
          	String pattern = "(\\x0155|^55)=(\\w+)\\x01";
          	Pattern p = Pattern.compile(pattern);
          	Matcher m = p.matcher(s);
          	m.find();
          	return m.group(2);
          }
          private String getID(String s) {
          	String pattern = "(\\x0111|^11)=(\\w+)\\x01";
          	Pattern p = Pattern.compile(pattern);
          	Matcher m = p.matcher(s);
          	m.find();
          	return m.group(2);
          }
          private int getQty(String s) {
          	String pattern = "(\\x0138|^38)=(\\d+)\\x01";
          	Pattern p = Pattern.compile(pattern);
          	Matcher m = p.matcher(s);
          	m.find();
          	return Integer.valueOf(m.group(2));
          }
          
          private String getFixVersion(String s) {
          	String pattern = "(\\x018|^8)=([\\w\\.]+)\\x01";
          	Pattern p = Pattern.compile(pattern);
          	Matcher m = p.matcher(s);
          	m.find();
          	return m.group(2);
          }
          
     
          
          private String getChangedSenderTarget(String log_line) {
         	 String sendertag = "49" ;
         	 String comptag = "56";
         	 String sendervalue = null;
         	 String compvalue = null;

         	 if (log_line.contains(sendertag)) {
         		 String pattern = sendertag + "=(\\w+)\\x01";
         		 Pattern p = Pattern.compile(pattern);
         		 Matcher m = p.matcher(log_line);
         		 m.find();
         		 sendervalue = m.group(1);
         				 //sendervalue = log_line.substring(log_line.indexOf(sendertag + "=")+1 , )
         	 }
         	 
         	 if (log_line.contains(comptag)) {
         		 String pattern = comptag + "=(\\w+)\\x01";
         		 Pattern p = Pattern.compile(pattern);
         		 Matcher m = p.matcher(log_line);
         		 m.find();
         		 compvalue = m.group(1);
         	 }

          log_line=log_line.replace(sendervalue, this.getSender());
  	 	  log_line=log_line.replace(compvalue, this.getTarget());
         	 return log_line;
         }	 
          public String getSender() {
          	return sender;
          }
          public String getTarget() {
          	return target;
          }
          public void setSender(String sender) {
          	this.sender = sender;
          }
          public void setTarget(String target) {
          	this.target = target;
          }
          //reading cfg file
          private void Read_Console(String path) throws IOException {
        	  LocalDateTime dateStart = LocalDateTime.now();
        	System.out.print(dateStart + " Read_Console : path : " + path );
          	try {
          		 
          		 BufferedReader readconfig = new BufferedReader(new FileReader(path));
          		 String sendername = "SenderCompID";
          		 String compname = "TargetCompID";
      			 String get_value = readconfig.readLine();

          		 while (get_value!=null)
          		 	 { 
          		 		 if (get_value.contains(sendername))
          		 			 //get value of SenderCompID
          		 			 {
          		 			 setSender(get_value.substring(get_value.lastIndexOf("=")+1));
          		 			 }
          		 			 else if (get_value.contains(compname)) {	 
          		 			 setTarget(get_value.substring(get_value.lastIndexOf("=")+1));
          		 			 }
          		 		get_value = readconfig.readLine();

           		 	 }
          		 readconfig.close();
          		
          	}
           catch (FileNotFoundException e) {
          		// TODO Auto-generated catch block
          		e.printStackTrace();
          	}
          	
          }
          public void stop() {
              shutdownLatch.countDown();
          }

}
