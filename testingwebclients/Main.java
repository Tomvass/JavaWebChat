package testingwebclients;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.xml.ws.WebServiceRef;

import wschatserver.ChatServerService;
import wschatserver.ChatServer;
        
/**
 * @file Main.java
 * @author Thomas Vass
 * @date 28th March 2015
 * 
 * @brief Main client side application, contains the gui interface.
 *  communicates with the server and repeatedly 'pings' the server with
 *  requests for messages at regular intervals.
 */

public class Main {
    
    /**
     * Absolute path to the wsdl file.
     */
    @WebServiceRef(wsdlLocation = 
            "http://localhost:8080//TestingWebApps/ChatServerService?wsdl")
    
    /**
     * Variables and objects concerning the gui
     */
    private JFrame frame;
    private JTextArea myText;
    private static JTextArea otherText;
    private JScrollPane myTextScroll;
    private JScrollPane otherTextScroll;
    private static TextThread otherTextThread;
    private String textString = "";
    
    /**
     *  Final variables for gui dimensions
     */
    private static final int HOR_SIZE = 400;
    private static final int VER_SIZE = 150;
    
    /**
     * Variables and objects concerning server communication
     */
    private ChatServerService service;
    private ChatServer port;
    private String id;
    private final char BACKSPACE = '\b';
    private final char RETURN = '\n';
    private boolean setup = true;
    
    
    /**
     * Method for initilising the gui as well as connecting to the server
     */
    private void initComponents() {
        
    	frame = new JFrame("Chat Client");
        myText = new JTextArea();
        
        myTextScroll = new JScrollPane(myText);			
        myTextScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		myTextScroll.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		myTextScroll.setMaximumSize(
		    new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		myTextScroll.setMinimumSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		myTextScroll.setPreferredSize(new java.awt.Dimension(
		    HOR_SIZE, VER_SIZE));

        myText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textTyped(evt);
            }
        });
        frame.getContentPane().add(myTextScroll, java.awt.BorderLayout.NORTH);
        
        otherText = new JTextArea();
        
        otherTextScroll = new JScrollPane(otherText);
        otherText.setBackground(new java.awt.Color(200, 200, 200));
        otherTextScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        otherTextScroll.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        otherTextScroll.setMaximumSize(
            new java.awt.Dimension(HOR_SIZE, VER_SIZE));
        otherTextScroll.setMinimumSize(
            new java.awt.Dimension(HOR_SIZE, VER_SIZE));
        otherTextScroll.setPreferredSize(new java.awt.Dimension(
		    HOR_SIZE, VER_SIZE));
        otherText.setEditable(false);
               
        frame.getContentPane().add(otherTextScroll,
            java.awt.BorderLayout.CENTER);
            
        frame.pack();
        frame.setVisible(true);
        /**
         * Attempt to connect to the server
         */
        try {
        service = new ChatServerService();
        port = service.getChatServerPort();
   

          /**
           * On window close the Client should disconnect successfully from
           * the server.
           */
          frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
          	  try {
          	      port.leave(id);
          	  }
          	  catch (Exception ex) {
          	      otherText.append("Exit failed.");
          	  }
          	  System.exit(0);
            }
          });
          
        }
        
        catch (Exception ex) {
            otherText.append("Failed to connect to server.");
        }
    }
    /**
     * Method for connecting to the server with a given name
     * @param name - The String of characters used to differentiate users.
     * @return - Returns true if connection is successful, false otherwise.
     */
    private boolean connectToServer (String name){
        try{
            if(port.join(name)){
                id = name;
                return true;
            }else{
                return false;
            }
        }catch (Exception ie){
            return false;
        }
    }
    /**
     * Method for listening for keyboard input and appending it to a string.
     * Sends the contents of the string when the return key is pressed.
     * If this is the first worse typed (i.e. setup is true) then this
     * sentence is considered to be the clients' desired username.
     * @param evt - Takes in the key pressed as a java.awt.event.KeyEvent object
     */
    private void textTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        //System.out.println("current id is: " +id);
        if (c == RETURN && setup){
            try{
                if(connectToServer(textString)){
                    System.out.println("User added");
                    setup = false;
                    otherTextThread = new TextThread(otherText,id,port);
                    textString = "";
                    otherTextThread.start();
                }else{
                    otherText.append
                            ("Name not recognized or already exists" +"\n");
                    textString = "";
                }
            }catch (Exception ie){
                
            }
        }else if(c == RETURN){
            try {
                   port.talk(id, textString);
                   System.out.println("message sent");
            }
            catch (Exception ie) {
                    otherText.append("Failed to send message.");
            }
            textString = "";
        } else if(c == BACKSPACE){
            textString = textString.substring(0, textString.length()-1);
        }else{
            textString = textString + c;
        }
            
    }
    
    /**
     * Main method for starting the program.
     * @param args 
     */
    public static void main(String[] args) {
    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    		Main client = new Main();
                @Override
    		public void run() {
    			client.initComponents();
    		}
    	});
    	
    }
}
/**
 * Thread responsible for pinging the server regularly. Effectively listens at
 * the given port for any new messages (those that are not equal to "").
 */
class TextThread extends Thread {

    ObjectInputStream in;
    JTextArea otherText;
    String id;
    ChatServer port;
    final int PING = 1000;
    
    TextThread(JTextArea other, String id, ChatServer port) throws IOException
    {
        otherText = other;
        this.id = id;
        this.port = port;
    }
    /**
     * Run method for the thread
     */
    @Override
    public void run() {
        while (true) {
            try {    
                String newText = port.listen(id);
                if (!newText.equals("")) {
                    otherText.append(newText );
                }
                Thread.sleep(PING);
            }
            catch (Exception e) {
                    otherText.append("Error reading from server. " +e +"\n");
            }  
        }
    }
}
