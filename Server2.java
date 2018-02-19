import java.io.*;
import java.net.*;

public class Server2 {
    public static void main(String args[]) {
        int port = 6789; //Port number to connect with the client
        Server2 server = new Server2( port );
        server.startServer();
    }
    
    // declare a server socket and a client socket for the server;
    // declare the number of connections
    
    ServerSocket echoServer = null;
    Socket clientSocket = null;
    int numConnections = 0; //Variable which holds the number of active clients
    int port;
    
    public Server2( int port ) {
        this.port = port;
    }
    
    public void stopServer() {
        System.out.println( "Server cleaning up." );
        System.exit(0);
    }
    
    public void startServer() {
        // Try to open a server socket on the given port
        // Note that we can't choose a port less than 1024 if we are not
        // privileged users (root)
        
        try {
            echoServer = new ServerSocket(port);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        
        System.out.println( "Server is started and is waiting for connections." );
        
        
        // Whenever a connection is received, start a new thread to process the connection
        // and wait for the next connection.
        
        while ( true ) {
            try {
                clientSocket = echoServer.accept();
                numConnections++;
                Server2Connection oneconnection = new Server2Connection(clientSocket, numConnections, this);
                
                new Thread(oneconnection).start();
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

class Server2Connection implements Runnable {
    static int logicalClock = 0; //Keeping the logical clock variable static because this needs to be same in all the instances of this program
    int offset = 0;
    int connectionCounter = 0;
    int temp;
    static int iter = 0;
    BufferedReader is;
    PrintStream os;
    DataOutputStream osd = null;
    Socket clientSocket;
    int id;
    Server2 server;
    
    public Server2Connection(Socket clientSocket, int id, Server2 server) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.server = server;
        System.out.println( "\nConnection " + id + " established with: " + clientSocket );
        logicalClock++;
        connectionCounter++;
        System.out.println( "Logical clock incremented to : " + logicalClock + ", because of the new connection.");
        try {
            osd = new DataOutputStream(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection " + id + " is closed.");
        }
    }
    
    public void run() {
        String line;
        try {
            
            while (true) {
                line = is.readLine(); //Receive input from client and convert it into integer
                int n = Integer.parseInt(line);
                n = n/2; //Decryption
                if ( n == -2 ) {
                    //If the received number is -2, then that connection will be closed
                    connectionCounter--;
                    if ( connectionCounter == 0 )
                        //If there are no more connections active, then the sever will be shut down
                        server.stopServer();
                    break;
                }
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    System.out.println(e);}
                iter++;
                logicalClock++; // Logical clock incremented for the message received
                //Writing the logical clock and iteration values to a text file
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                                                               new FileOutputStream("Server.txt", true), "utf-8"))) {
                    writer.write("\n" + iter + ". Logical Clock: " + logicalClock);
                }
                System.out.println("\nIteration Value: " + iter);
                System.out.println("Logical Clock value: " + logicalClock);
                
                if(n != 0) {
                    //When the client sends the correct logical clock value, write the same to the respective text file
                    System.out.println( "\nReceived " + n + " from Connection " + id + "." );
                    switch(id) {
                        case 1: try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                                                                               new FileOutputStream("Client1.txt", true), "utf-8"))) {
                            writer.write("\n" + iter + ". Logical Clock: " + logicalClock);
                        }
                            break;
                        case 2: try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                                                                               new FileOutputStream("Client2.txt", true), "utf-8"))) {
                            writer.write("\n" + iter + ". Logical Clock: " + logicalClock);
                        }
                            break;
                        case 3: try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                                                                               new FileOutputStream("Client3.txt", true), "utf-8"))) {
                            writer.write("\n" + iter + ". Logical Clock: " + logicalClock);
                        }
                            break;
                        case 4: try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                                                                               new FileOutputStream("Client4.txt", true), "utf-8"))) {
                            writer.write("\n" + iter + ". Logical Clock: " + logicalClock);
                        }
                            break;
                        default: System.out.println("No connections active");
                            break;
                    }
                    logicalClock++;
                    System.out.println( "Logical clock incremented to : " + logicalClock + ", because Server received a value from the connection " + id + ".");
                }
                else {
                    //Client sends zero as its logical clock value
                    System.out.println("No information sent by connection: " + id);
                    System.out.println( "Logical clock is not incremented. The current logical clock value is " + logicalClock );
                }
                 //Irrespective of the value sent by the client, the server adds the number to its logical clock and calculate the average and sends it back to the client after encryption
                temp = (logicalClock+n)/5;
                logicalClock = temp;
                offset = temp - n;
                System.out.println( "Sending " + offset + " to Connection " + id + " as the offset." );
                offset = offset*2; // Encryption
                osd.writeBytes(  offset + "\n" );
                logicalClock++;
                System.out.println( "Logical clock incremented to : " + logicalClock+ ", because Server sent an offset value to the connection " + id + ".");
            }
            
            System.out.println( "Connection " + id + " closed." );
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("\nAll the connections are closed.");
            server.stopServer();
        }
    }
}

