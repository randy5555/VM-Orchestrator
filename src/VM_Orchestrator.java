import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.*;

/**
 * Manages Virtual Machines on a linux system.
 * 
 * Orchestrator returns JSON through the connected port. Return types are
	 * {"Response":"RunningVMs", Count: 3, "RunningVMs":["x","y","z"]}
	 * {"Response":"Error", "ErrorMessage":"x"}
	 * {"Response":"Success"}
	 * {"Response":"Connected"}
	 * {"Response":"Disconnected"}
 *
 */
public class VM_Orchestrator {
	/*
	 * Remember not to commit any configuration login or password to github, as it is public!
	 * 
	 * */
	//System variables
	static int port = 9991; //default port, changes with config file
	final static String welcomeMessage = "{\"Response\":\"Connected\"}";
	final static String closedMessage = "{\"Response\":\"Disconnected\"}";
	final static String successMessage = "{\"Response\":\"Success\"}";
	
	//Configuration list map, Map of <config name, switch position>
	static Map<String, Object> configList = new HashMap<String, Object> (
			Map.ofEntries(
					new AbstractMap.SimpleEntry<String, Object>("port", (Integer)0)
			)
	);
	
	//Command list map, Map of <command name, number of parameters to expect>
	static Map<String, Integer> commandList = new HashMap<String, Integer> (
			Map.ofEntries(
					new AbstractMap.SimpleEntry<String, Integer>("getRunningVMs", 0),    
					new AbstractMap.SimpleEntry<String, Integer>("startVM", 1),
					new AbstractMap.SimpleEntry<String, Integer>("shutdownVM", 1),
					new AbstractMap.SimpleEntry<String, Integer>("destroyVM", 1),
					new AbstractMap.SimpleEntry<String, Integer>("undefineVM", 1),
					new AbstractMap.SimpleEntry<String, Integer>("defineVM", 1),
					new AbstractMap.SimpleEntry<String, Integer>("createDiskImage", 2),
					new AbstractMap.SimpleEntry<String, Integer>("deleteDiskImage", 1),
					new AbstractMap.SimpleEntry<String, Integer>("exit", 0)
			)
	);
	
    public static void main(String[] args) {
    	
    	String config_path = "src/config.cfg";
    	if(args.length >= 1) {
    		config_path = args[0];
    	}
        if(loadConfig(config_path)) {
        	startServer();
        }
    }
    	
    /**
     * Loads and parses configuration file.
     */
    private static boolean loadConfig(String config_path) {
    	try {
    	      File config = new File(config_path);
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
    	    	Util.println("Error: No config file!");
    	      return false;
    	    }
	}

	private static void setVariable(String setting) {
		setting = setting.replaceAll(" ", "");
		String[] configArray = setting.split("=");
		
		if(configList.get(configArray[0]) instanceof Integer) {
			configList.put(configArray[0], Integer.parseInt(configArray[1]));
		} else {
			configList.put(configArray[0], configArray[1]);
		}
		
		
	}

	private static void startServer() {
		port = (Integer)configList.get("port");
		
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			while(!serverSocket.isClosed()) {
				Socket connectionSocket = serverSocket.accept();
				SocketAddress remoteAddress = connectionSocket.getRemoteSocketAddress();
				if(remoteAddress != null) {
					Util.println("New connection accepted from " + remoteAddress.toString());
				}
				new Thread(() -> handleConnection(connectionSocket)).start();
			}
			Util.println("Server socket was closed.");
		} catch (IOException e) {
			Util.println("Error: Unable to start a server on port " + port);
		}
	}
	
	private static void handleConnection(Socket connectionSocket) {
		//Create Input/Output streams
		try {
			InputStream inputToServer = connectionSocket.getInputStream();
			OutputStream outputFromServer = connectionSocket.getOutputStream();
			
			Scanner scanner = new Scanner(inputToServer, "UTF-8");
			PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);
			
			//Welcome message
			//serverPrintOut.println(welcomeMessage);
			
			boolean exit = false;
			
			while(!exit && connectionSocket.isConnected() && scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if(parseCommand(serverPrintOut, line)) {
					exit = true;
					serverPrintOut.println(closedMessage);
					serverPrintOut.close();
					scanner.close();
					inputToServer.close();
					connectionSocket.close();
				}
			}
		} catch (IOException e) {
			Util.println("Error: a failure on a client connection occured.");
		}
		if(!connectionSocket.isClosed()) {
			try {connectionSocket.close();}catch(IOException e) {}//make sure socket is closed.
		}
		Util.println("Connection exited.");
	}
	
	public static boolean parseCommand(PrintWriter output, String input) {
		boolean exit = false;
		Command command = new Command();
		
		try {
			command = new Gson().fromJson(input, Command.class);
			
			if(commandList.containsKey(command.getCommand())) {
			if(commandList.get(command.getCommand()) == command.getParamNum()) {
				
				vmActions control = new vmActions();
				switch(command.getCommand()) {
				case "getRunningVMs":
					try {
						output.println(buildRunningVM_JSON(control.getRunningVMs()));
					} catch (Exception e) {
						output.println(buildErrorJson(e.toString()));
					}
					break;
				case "startVM":
					if(control.startVM(command.getParam(0))) {
						output.println(successMessage);
					} else {
						output.println(buildErrorJson("Failed to start VM"));
					}
					break;
				case "shutdownVM":
					if(control.shutdownVM(command.getParam(0))) {
						output.println(successMessage);
					} else {
						output.println(buildErrorJson("Failed to shutdown VM"));
					}
					break;
				case "destroyVM":
					if(control.destroyVM(command.getParam(0))) {
						output.println(successMessage);
					} else {
						output.println(buildErrorJson("Failed to destroy VM"));
					}
					break;
				case "undefineVM":
					if(control.undefineVM(command.getParam(0))) {
						output.println(successMessage);
					} else {
						output.println(buildErrorJson("Failed to undefine VM"));
					}
					break;
				case "defineVM":
					String vmName = command.getParam(0);
					Integer memoryKB = Integer.parseInt(command.getParam(1));
					Integer nCPUs = Integer.parseInt(command.getParam(2));
					Integer OSID = Integer.parseInt(command.getParam(3));
					Integer VNCPort = Integer.parseInt(command.getParam(4));
					Float diskSizeGB = Float.parseFloat(command.getParam(5));
					
					XMLdefinition newXML = new XMLdefinition(vmName, memoryKB, nCPUs, OSImages.getOSImageName(OSID), VNCPort, diskSizeGB);

					if(control.defineVM(newXML)) {
						output.println(successMessage);
					} else {
						output.println(buildErrorJson("Failed to define VM"));
					}
					break;
//				case "createDiskImage":
//					if(control.createDiskImage(command.getParam(0), Float.parseFloat(command.getParam(1))).compareTo(command.getParam(0)) == 0) output.println(successMessage);
//					else output.println(buildErrorJson("Failed to start VM"));
//					break;
//				case "deleteDiskImage":
//					if(control.deleteDiskImage(command.getParam(0))) output.println(successMessage);
//					else output.println(buildErrorJson("Failed to start VM"));
//					break;
				case "exit":
					exit = true;
					break;
				}
			}
			else {
				output.println(buildErrorJson("Incorrect number of params for command: " + command.getCommand()));
			}
			
		}
		else {
			output.println(buildErrorJson("Command \"" + command.getCommand() + "\" does not exist"));
		}
		}
		catch(com.google.gson.JsonSyntaxException e) {
			output.println(buildErrorJson("Commands must be in correct JSON format!"));
			e.printStackTrace();
		}
		return exit;
	}

	private static String buildRunningVM_JSON(ArrayList<String> stringList) {
		String json = "{\"Response\":\"RunningVMs\", Count: " + stringList.size() + ", \"RunningVMs\":[";
		for(String line : stringList) {
			// "yes
			json = json + jsonReplace(line);
		}
		json = json + "]}";
		return json;
	}
	
	private static String buildErrorJson(String message) {
		return "{\"Response\":\"Error\", \"ErrorMessage\":\"" + jsonReplace(message) + "\"}";
		
	}
	
	private static String jsonReplace(String input) {
		// \stuff \\stuff
		input = input.replace("\\", "\\\\");
		input = input.replace("\"", "\\\"");
		return input;
	}
}

class Command {
	private String command;
	private String[] params;
	
	public String getCommand() {
		return command;
	}
	
	public Integer getParamNum() {
		if(params != null) return params.length;
		else return 0;
	}
	
	public String[] getParams() {
		return params;
	}
	
	public String getParam(int index) {
		if(index + 1 <= params.length && index >= 0) return params[index];
		else return "";
	}
}