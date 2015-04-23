To compile the server:

1. Create a new Java web application project in netbeans
2. Under Source Packages paste the folder 'wschatserver'(or create a new package called wschatserver and put both java files inside that package)
3. Deploy code to glassfish server



To compile the client:
1. Create a new Java application project in netbeans
2. Under Source Packages paste the folder 'testingwebclients' (or create a new package called testingwebclients put the Java file inside.
3. Edit the wsdl location on line 34 of Main.java
4. Right click the netbean project and add a new 'web service client' then select the direct URL to the wsdl file.
5. Clean and build and then run Main.java





Using the client:


The first line entered will be used as the client's name, if the name is already taken or contains blank spaces then the name is rejected and the user is given another opportunity to enter a new name.

To send a private message the user need only to start the message with a '/' for example:

/Tom hello, great project Tom!

Would send a private message to Tom containing everything after the name. It is also important to note that names ARE case sensitive, so sending /tom would not be the same as /Tom or any other variation.






Notes to marker:

Private messaging has been implemented to allow people to send messages to offline users. This was intentional, and could be easily changed by making the privateMessage method in the ChatServer to check if the user was connected. However I felt this current implementation provided greater utility.  