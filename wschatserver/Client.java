package wschatserver;


/**
 * @file Client.java
 * @author Thomas Vass
 * @date 28th March 2015
 * 
 * @brief Code concerning client information such as their name, unread messages
 * and connected status, ChatServer contains a list of Clients to maintain
 * communication.
 */
public class Client {
    
    private String name;
    private String message;
    private boolean connected = true;
    
    private final char RETURN = '\n';
    private final String EMPTY_STRING = "";
    
    /**
     * Single parameter constructor to create a basic client
     * @param id 
     */
    public Client (String id){
        name = id;
        message = EMPTY_STRING;
    }
    
    /**
     * Multiple parameter constructor to create a client with a specific message 
     * @param id
     * @param msg 
     */
    public Client (String id, String msg){
        name = id;
        message = msg;
    }
    
    /**
     * Method to get the name of a specific client
     * @return String value of the name
     */
    public String getName(){
        return name;
    }
    
    /**
     * sets the client's current messages to the parameter given
     * @param msg the new messages
     */
    public void setMsg (String msg){
        message = msg;
    }
    
    /**
     * Appends a new message onto the String of messages for the specific client
     * @param msg The message to append.
     */
    public void addMsg (String msg){
        if (EMPTY_STRING.equals(message)){
            message = msg + RETURN;
        }else{
            String newMessage = (msg + RETURN);
            message += newMessage;    
        }
        
    }
    
    /**
     * Method to show the user as reconnected
     */
    public void reJoin(){
        connected = true;
    }
    
    /**
     * Method to show the user as disconnected
     */
    public void leave(){
       connected = false;
    }
    /**
     * Method to check if the user is connected
     * @return 
     */
    public boolean isConnected(){
        return connected;
    }
    
    /**
     * Method to get the current contents of the message String.
     * @return the message String.
     */
    public String getMsg(){
        return message;
    }
    
}
