package wschatserver;

import java.util.ArrayList;

import javax.jws.WebService;
import javax.jws.WebMethod;

/**
 * @file ChatServer.java
 * @author Thomas Vass
 * @date 28th March 2015
 * 
 * @brief Server code, responsible for users joining, leaving, talking,
 * listening, and sending private messages.
 */
@WebService
public class ChatServer {

    private static ArrayList<Client> clientList = new ArrayList<>();
    private static ArrayList<String> connectedUsers = new ArrayList<>();
    
    private final char PRIVATEMESSAGE = '/';
    private final char BLANK_CHAR = ' ';
    private final String EMPTY_STRING = "";
    
    /**
     * Join method which returns true if and only if a user by that name
     * does not already exist as a connected user.
     * @param name - the String of which the user wishes to be known by
     * @return - Boolean, True if and only if the user has been successfully
     * added
     */
    @WebMethod
    public Boolean join(String name){
        synchronized (clientList) {
            if(name.indexOf(BLANK_CHAR)!=-1){
                //name cannot contain any white spaces
                return false;
            }
            for(String client : connectedUsers){
               if(name.equals(client)){
                return false;
                }else{
                    addUser(name);
                    connectedUsers.add(name);
                    return true;
                } 
            }
            addUser(name);
            connectedUsers.add(name);
            return true;
            
        }
    }
    /**
     * Method for adding a user to the Client List
     * @param name the id (name) of the user.
     */
    private void addUser(String name){
        synchronized (clientList){
            Client newUser = new Client(name);
            for(Client user : clientList){
                if (user.getName().equals(name)){
                    //user with that name previously connected and open
                    user.reJoin();
                    return;
                }
            }
            //user has never existed so add them
            clientList.add(newUser);
        }
    }
    
    /**
     * Method allowing communication between users, first checks if the message
     * is a private one by detecting the first character. Depending on outcome
     * will either send the message directly to the user specified (if it is a 
     * private message) or to all connected users if it is not.
     * @param id - The id (name) of the user sending the message.
     * @param fullMessage - The message itself
     */
    @WebMethod
    public void talk(String id, String fullMessage){
        synchronized(clientList){
            //clientList.add(msg);
            if (fullMessage.charAt(0) == PRIVATEMESSAGE){
                if(privateMsg(id, fullMessage)){
                    sendMessage(id, "Private Message Sent.");
                }else{
                    sendMessage(id, "User not found.");
                }
            }else{
                for(Client user : clientList){
                    if(user.isConnected()){
                        user.addMsg(id+": "+ fullMessage);
                    }
                }
            }
        }
    }
    
    /**
     * Used in sending a message to a specific client, is private as it should
     * only be called when sending a private message so as to report to the
     * sender that either the message was successfully sent or not.
     * @param id - The id (name) of the user sending the message.
     * @param message - The message itself.
     * @return returns a boolean value to say that the message has been added.
     */
    private boolean sendMessage(String id, String message){
        for(Client user : clientList){
            if(user.getName().equals(id)){
                user.addMsg(message);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Method used for listening for messages sent to a specific user.
     * After receiving all messages the user's messages are removed from the
     * server.
     * @param id - The id (name) of the user who's messages we wish to read.
     * @return - The String of messages the user has.
     */
    @WebMethod
    public String listen(String id) {
        synchronized(clientList){
            try{
                if(clientList==null){
                    return EMPTY_STRING;
                }else{
                    //System.out.println(id);
                    for(Client user : clientList){
                        if(user.getName().equals(id)){
                            String temp = user.getMsg();
                            user.setMsg(EMPTY_STRING);
                            return temp;
                        }
                    }
                    return "really broken here bud";
                }
            }catch (Exception e){
                System.out.println("something went wrong here " +e);
                return EMPTY_STRING;
            }
        } 
    }
    /**
     * Method for sending a private message to a specific user, only called when
     * the talk method detects a potential private message
     * @param id - the id (name) of the user sending the message.
     * @param fullMessage - the contents of the message, ideally containing 
     * the intended recipient of the message and the message itself.
     * @return Boolean - returns True if and only if the message was sent.
     */
    private boolean privateMsg(String id, String fullMessage){
        synchronized (clientList) {
            
            String tellName = fullMessage.substring
                    (1, fullMessage.indexOf(BLANK_CHAR));
            
            String restOfMessage = fullMessage.substring
                    (fullMessage.indexOf(BLANK_CHAR), fullMessage.length());
            
            for(Client user : clientList){
                if(user.getName().equals(tellName)){
                    user.addMsg
                            ("[Private Message from "+id +"]"+ restOfMessage);
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * Leave method with removes a user from the connectedUsers and sets their
     * availability to not connected. Implemented in this way to allow private
     * messages to be sent to offline users so that they are received when 
     * reconnected.
     * @param id - the id (name) of the user who is disconnecting.
     */
    @WebMethod
    public void leave(String id){
        synchronized (connectedUsers) {
            connectedUsers.remove(connectedUsers.indexOf(id));
        }
        synchronized (clientList){
             for(Client user : clientList){
                if(user.getName().equals(id)){
                    user.leave();
                }
            }
        }
    }
    
}
