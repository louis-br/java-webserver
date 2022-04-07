import java.io.* ;
import java.net.* ;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.* ;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
        // Set the port number. 
        int port = 6789; 
        System.out.println("Listening on port " + port);

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

    private static String contentType(String fileName) 
    {
        try {
            return Files.probeContentType(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println(e);
        }
        return "application/octet-stream";
    }

    public static void sendBytes(FileInputStream fis, OutputStream os)
    throws Exception
    {
        
        // Construct a 1K buffer to hold bytes on their way to the socket. 
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream.
        while((bytes = fis.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes);
        }
    }

    public void processRequest() throws Exception
    {
        //Get a reference to the socket's input and output streams. 
        InputStream is = socket.getInputStream();

        OutputStream os = socket.getOutputStream();

        //Set up input stream filters. 
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
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

        // PART B

        // Extract the filename from the request line. 
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // Skip over the method, which should be "GET"
        String fileName = tokens.nextToken();

        //Prepend a "." so that file request is within the current directory. 
        fileName = "." + fileName;

        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        // Construct the response message
        String statusLine = "";
        String contentTypeLine = "";
        String entityBody = "";
        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-Type: " +
                contentType( fileName ) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" +
                "<HEAD><TITLE>Not Found</TITLE><HEAD>" +
                "<BODY><h1>4&#x2639;4</h1>Not Found</BODY><HTML>";
        }

        bw.write(statusLine);
        bw.write(contentTypeLine);
        bw.write(CRLF);
        bw.write(entityBody);
        bw.flush();

        // Send the entity body. 
        if (fileExists) {
            sendBytes(fis, os);
            //OutputStreamWriter ostest = new OutputStreamWriter(os);
            fis.close();
        }

        br.close();
        bw.close();
        os.close();
        socket.close();
    }
}