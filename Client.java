import java.io.*;
import java.net.*;
import java.util.Random;

public class Client {
    public static void main(String[] args) {
        
        String hostname = "in-csci-rrpc01.cs.iupui.edu"; //Server IP address
        int port = 6789; //Port to communicate
        int logicalClock = 0; //Logical client variable
        int iter = 0; //To know how many times client runs
        int temp; //Temporary variable for calculations
        
        // declaration section:
        // clientSocket: our client socket
        // os: output stream
        // is: input stream
        
        Socket clientSocket = null;
        DataOutputStream os = null;
        BufferedReader is = null;
        
        // Initialization section:
        // Try to open a socket on the given port
        // Try to open input and output streams
        
        try {
            clientSocket = new Socket(hostname, port);
            os = new DataOutputStream(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + hostname);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + hostname);
        }
        
        // If everything has been initialized then we want to write some data
        // to the socket we have opened a connection to on the given port
        
        if (clientSocket == null || os == null || is == null) {
            System.err.println( "Something is wrong. One variable is null." );
            return;
        }
        
        try {
            while ( true ) {
                iter++;
                if ( iter == 1001 ) //Number of iterations to run
                {
                    os.writeBytes("-2"); //If server recieves -2 as input, then the connection to this client will break
                    break;
                }
                System.out.println("\nIteration number: " + iter);
                Random rand = new Random();
                int n = rand.nextInt(3) + 1; //Random number between 1, 2, 3 will be chosen
                if(n == 1) {
                    logicalClock++;
                    //if the number is 1, increment the logical clock and send to server
                    
                    System.out.println("\nEvent 1 : Send the logical clock value to Server");
                    logicalClock = logicalClock * 2; // Encryption
                    os.writeBytes(  logicalClock + "\n" );
                    System.out.println("The incremented logical clock value, sent to Server is: " + logicalClock);
                }
                else if(n == 2){
                    temp = 0;
                    //if the number is 2, send 0 as logical clock to server and receive offset from server to adjust the logical clock
                    logicalClock++;
                    os.writeBytes(  temp + "\n" );
                    System.out.println("\nEvent 2 : Receive the offset from Server");
                    System.out.println("The incremented logical clock value is: " + logicalClock + ". But not sending to Server.");
                    String responseLine = is.readLine();
                    temp = Integer.parseInt(responseLine);
                    temp = temp/2; //Decryption
                    System.out.println("Offset received from the server and adjusting the Locgical Clock: " + temp);
                    logicalClock += temp;
                    System.out.println("The Logical Clock value after adding the offset from Server is: " + logicalClock);
                    if(logicalClock < 0)
                        logicalClock = 0;
                }
                else {
                    temp = 0;
                    //if the number is 3, then send 0 as logical clock to server and increment the logical clock
                    logicalClock++;
                    os.writeBytes(  temp + "\n" );
                    System.out.println("\nEvent 3 : Internal Event");
                    System.out.println("The incremented logical clock value is: " + logicalClock + ". But not sending to Server.");
                }}
            
            // clean up:
            // close the output stream
            // close the input stream
            // close the socket
            
            os.close();
            is.close();
            clientSocket.close();
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
