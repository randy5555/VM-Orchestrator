import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

/**
 * Manages Virtual Machines on a linux system.
 *
 */
public class VM_Orchestrator {
	
	//System variables
	static int port = 9991; //default port, changes with config file
	static String SQLip;
	static String SQLuser;
	static String SQLpass;
	final static String welcomeMessage = "Connected! Enter 'exit' to shutdown orchestrator";
	final static String closedMessage = "Connection Closed...";
	
    public static void main(String[] args) {
        if(getConfig()) {
        	startServer();
        }
    }
    	
    private static boolean getConfig() {
    	try {
    	      File config = new File("src/config.cfg");
    	      Scanner configReader = new Scanner(config);
    	      while (configReader.hasNextLine()) {
    	        String setting = configReader.nextLine();
    	        if(setting != "") {
    	        	setVariable(setting);
    	        }
    	      }
    	      configReader.close();
    	      return true;
    	    } catch (FileNotFoundException e) {
    	      System.out.println("Error: No config file!");
    	      return false;
    	    }
	}

	private static void setVariable(String setting) {
		setting.replaceAll(" ", "");
		String[] configArray = setting.split("=");
		//Set Port
		if(configArray[0].compareTo("port") == 0) {
			port = Integer.parseInt(configArray[1]);
		}
		//Set SQL ip address
		else if(configArray[0].compareTo("SQLip") == 0) {
			SQLip = configArray[1];
		}
		//Set SQL user name and password decoded from base64 user:pass
		else if(configArray[0].compareTo("SQLlogin") == 0) {
			byte[] decodedBytes = Base64.getDecoder().decode(configArray[1]);
			String decodedString = new String(decodedBytes);
			String[] userArray = decodedString.split(":");
			SQLuser = userArray[0];
			SQLpass = userArray[1];
		}
	}

	public static void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            Socket connectionSocket = serverSocket.accept();//needs to be in a loop
            
            //Create Input&Outputstreams for the connection
            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);
            
            //Welcome message
            serverPrintOut.println(welcomeMessage);

            boolean exit = false;

            while(!exit && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                parseCommand(serverPrintOut, line);

                if(line.toLowerCase().trim().equals("exit")) {
                    exit = true;
                    serverPrintOut.println(closedMessage);
                    serverPrintOut.close();
                    scanner.close();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	public static boolean parseCommand(PrintWriter output, String input) {
		//TODO: Write command parser. All command data will come in as input string, the current code works
		//input string should be in json format but the code doesn't currently enforce it for testing with telnet
		output.println("Command: " + input + " received");
		return true;
	}
}
