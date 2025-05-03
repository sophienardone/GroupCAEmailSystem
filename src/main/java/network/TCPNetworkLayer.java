package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class TCPNetworkLayer {
    // Fields for socket communication
    private Socket dataSocket;               // Socket used for communication
    private Scanner inputStream;            // Input stream to read data from the socket
    private PrintWriter outputStream;       // Output stream to send data over the socket
    private String hostname;                // Server hostname
    private int port;                       // Server port

    /**
     * Constructor to initialize with a hostname and port.
     * Used when the socket needs to be created later using connect().
     *
     * @param hostname the server address to connect to
     * @param port the port number on the server
     */
    public TCPNetworkLayer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Constructor to initialize with an existing socket.
     * Immediately sets up input/output streams.
     *
     * @param dataSocket an existing, already connected socket
     * @throws IOException if an I/O error occurs while setting up streams
     */
    public TCPNetworkLayer(Socket dataSocket) throws IOException {
        if (dataSocket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }

        this.dataSocket = dataSocket;
        setStreams();  // Setup input and output streams for communication
    }

    /**
     * Initializes input and output streams using the connected socket.
     *
     * @throws IOException if the socket's streams cannot be opened
     */
    private void setStreams() throws IOException {
        this.inputStream = new Scanner(dataSocket.getInputStream());         // For reading input
        this.outputStream = new PrintWriter(dataSocket.getOutputStream());   // For sending output
    }

    /**
     * Sends a message to the connected peer.
     *
     * @param message the text message to be sent
     */
    public void send(String message) {
        outputStream.println(message);   // Send the message
        outputStream.flush();            // Ensure the message is transmitted immediately
    }

    /**
     * Receives a line of text from the connected peer.
     *
     * @return the received message as a String
     */
    public String receive() {
        return inputStream.nextLine();   // Read the next line from the input stream
    }

    /**
     * Disconnects the socket connection and closes the streams.
     *
     * @throws IOException if an error occurs while closing resources
     */
    public void disconnect() throws IOException {
        if (this.dataSocket != null) {
            this.outputStream.close();   // Close the output stream
            this.inputStream.close();    // Close the input stream
            this.dataSocket.close();     // Close the socket
        }
    }

    /**
     * Establishes a connection to the server using the stored hostname and port,
     * and sets up the input/output streams.
     *
     * @throws IOException if the connection or stream setup fails
     */
    public void connect() throws IOException {
        dataSocket = new Socket(hostname, port);  // Create and connect the socket
        setStreams();                             // Initialize streams
    }

}

