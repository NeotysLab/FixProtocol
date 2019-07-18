/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 * 
 * This file is part of the QuickFIX FIX Engine 
 * 
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 * 
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 * 
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package quickfix.banzai;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.banzai.ui.BanzaiFrame;
import quickfix.banzai.ui.OrderEntryPanel;

/**
 * Entry point for the Banzai application.
 */
public class Banzai {
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    /** enable logging for this class */
    private static Logger log = LoggerFactory.getLogger(Banzai.class);
    private static Banzai banzai;
    private boolean initiatorStarted = false;
    private Initiator initiator = null;
   // private JFrame frame = null;
    private String sender;
    private String target;
    
    
    
	public Banzai(String[] args) throws Exception {
        InputStream inputStream = null;
        InputStream inputStream1 = null;
     
        if (args.length== 4) {
    
        /*	inputStream = new BufferedInputStream( 
                      new FileInputStream( 
                      new File( "config/banzai.cfg" )));
        	inputStream1 = new BufferedInputStream( 
              new FileInputStream( 
              new File( "logfile.txt" )));
        	//System.out.println("Please enter args");
    		*/
      
    	
        //System.out.println(args.length);
        inputStream = new FileInputStream(args[0]);
        inputStream1 = new FileInputStream(args[1]);
        System.out.println("Config file:"+ args[0]);
        //System.out.println("Enter config file:"+ args[0]);
        System.out.println("Log file:"+ args[1]);
        System.out.println("Delay rate:"+ args[2]);
        System.out.println("Response file"+args[3]);
      	System.out.println("Args Accepted");
        SessionSettings settings = new SessionSettings(inputStream);
        
        inputStream.close();
       
        
        boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true")).booleanValue();
        
        OrderTableModel orderTableModel = new OrderTableModel();
        ExecutionTableModel executionTableModel = new ExecutionTableModel();
        BanzaiApplication application = new BanzaiApplication(orderTableModel, executionTableModel, args[3]);
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(false, true, true, false);
        //LogFactory logFactory = new ScreenLogFactory(false, false, true, false);
        MessageFactory messageFactory = new DefaultMessageFactory();

        
         initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory,
                messageFactory);
      
   //     frame = new BanzaiFrame(orderTableModel, executionTableModel, application);
     //   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.Read_Console(args[0]);
        System.out.println("Sender is ------>"+ this.getSender());
        System.out.println("Target is ------>"+ this.getTarget());
       // args 2 is delay here which is set in send msg function
        this.sendMsg(args[1], Long.parseLong(args[2]), application);
    }
       
	}
	
	public void msg_sending()
	{
		
	}
 /*   public void Logon_Request()
    {	
    	InputStream logon = null;
    System.out.println("=====Type Yes to request for logon=====");
    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();
    if (input.equals("Yes"))
    	System.out.println("=====You are allowed to send messages=====");
    }
    public void Logout_Request()
    {	
    	InputStream logout = null;
    System.out.println("=====Type Logout to request for logout=====");
    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();
    if (input.equals("Logout"))
    	System.out.println("=====You are Logged out=====");
    	System.out.println("=====Connection Terminated=====");
    }
    
   */ 
    public void sendMsg(String messageFile,  BanzaiApplication app) 
    {
    	System.out.println("Sending MSG-----------> " );
                
                BufferedReader reader;
        		try {
        			reader = new BufferedReader(new FileReader(
        					messageFile));
        			String line = reader.readLine();
        			while (line != null) {
        				// read next line
        				line = reader.readLine();
        				try {
        					System.out.println("Original Log Line-----------> " + line);
        					line = this.getChangedSenderTarget(line);
							
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        				Order order = new Order(line);
        				System.out.println(order.toString());

        				order.setSessionID(new SessionID(this.getSessionID(line)));
        				order.setID(this.getID(line));
        				order.setSymbol(this.getSymbol(line));
        				OrderEntryPanel.setSide(order, this.getSide(line));
        				order.setQuantity(this.getQty(line));
        				
        				try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        				app.send(order);
        				System.out.println("Modified Message-----------> " + line);
        				System.out.println("Order Side-----------> " + order.getSide());
        	            System.out.println("Order Quantity-----------> " + order.getQuantity());
        	            System.out.println("Order session-----------> " + order.getSessionID());
        	            System.out.println("Order ID-----------> " + order.getID());
        	            System.out.println("Order Symbol-----------> " + order.getSymbol());

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
    
    private String getSessionID(String s)
    {
    	return this.getFixVersion(s)+ ":" + this.getSender()+ "->" + this.getTarget();
    }
    
    
    public String getSender() {
    	return sender;
    }
    
    public void setSender(String sender) {
    	this.sender = sender;
    }
    public String getTarget() {
    	return target;
    }
    
    public void setTarget(String target) {
    	this.target = target;
    }
  //reading cfg file
    private void Read_Console(String cfg) throws IOException {

    	try {
    		 
    		 BufferedReader readconfig = new BufferedReader(new FileReader(System.getProperty("user.dir")+File.separator+cfg));
    		 String sendername = "SenderCompID";
    		 String compname = "TargetCompID";
    		 String get_value = readconfig.readLine();
    		 while (get_value != null)
    		 
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
    //reading log file
    private String getChangedSenderTarget (String log_line) {


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
    				 //sendervalue = log_line.substring(log_line.indexOf(sendertag + "=")+1 , )
    	 }
    			 	 //compvalue = log_line.substring(comptag.lastIndexOf("=")+1);
    		
    	 	log_line=log_line.replace(sendervalue, this.getSender());
    	 	  System.out.println("Original Sender  value is ------>"+ sendervalue);
    	 	  System.out.println("Sender value is ------>"+ this.getSender());
    	 	log_line=log_line.replace(compvalue, this.getTarget());
    	 	  System.out.println("Original target value is ------>"+ compvalue);
    	 	  System.out.println("Target value is ------>"+ this.getTarget());
    	 	  System.out.println("Modified Message-----------> " + log_line);
    	 return log_line;
    }	
    
   public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        } else {
            Iterator<SessionID> sessionIds = initiator.getSessions().iterator();
            while (sessionIds.hasNext()) {
                SessionID sessionId = (SessionID) sessionIds.next();
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    public void logout() {
        Iterator<SessionID> sessionIds = initiator.getSessions().iterator();
        while (sessionIds.hasNext()) {
            SessionID sessionId = (SessionID) sessionIds.next();
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    public void stop() {
        shutdownLatch.countDown();
    }

   // public JFrame getFrame() {
     //   return frame;
   // }

    public static Banzai get() {
        return banzai;
    }

    public static void main(String args[]) throws Exception {
     

/*    	try {
                  // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
           
        }*/
        banzai = new Banzai(args);
        if (!System.getProperties().containsKey("openfix")) {
        	//banzai.Logon_Request();
        	banzai.logon();
        	//banzai.Logout_Request();
        }
        shutdownLatch.await();
    }
}

