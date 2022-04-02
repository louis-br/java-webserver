import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
        // Set the port number. 
        int port = 6789; 
        System.out.println("Hello world!");

        // Establish the listen socket.
        ServerSocket serverSocket = new ServerSocket(port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    serverSocket.close();
                    System.out.println("Shutdown");
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        // Process HTTP service requests in an infinite loop. 
        while (true) {
            try {
                // Listen for a TCP connection request. 
                Socket socket = serverSocket.accept();
    
                // Construct an object to process the HTTP request message.
                HttpRequest request = new HttpRequest(socket);
    
                // Create a new thread to process the request.
                Thread thread = new Thread(request);
    
                // Start the thread. 
                thread.start();
            } catch (SocketException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}

final class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception
    {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run()
    {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void processRequest() throws Exception
    {
        //Get a reference to the socket's input and output streams. 
        InputStream is = socket.getInputStream();

        OutputStream os = socket.getOutputStream();

        //Set up input stream filters. 
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Get the request line of the HTTP request message. 
        String requestLine = br.readLine();

        // Display the request line.
        System.out.println();
        System.out.println(requestLine); 

        // Get and display the header lines. 
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        os.close();
        br.close();
        socket.close();
    }
}