import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.*;

/**
 * Manages Virtual Machines on a linux system.
 *
 */
public class VM_Orchestrator {
	/*
	 * Remember not to commit any configuration login or password to github, as it is public!
	 * 
	 * Orchestrator returns JSON through the connected port. Return types are
	 * {"Response":"RunningVMs", Count: 3, "RunningVMs":["x","y","z"]}
	 * {"Response":"Error", "ErrorMessage":"x"}
	 * {"Response":"Success"}
	 * {"Response":"Connected"}
	 * {"Response":"Disconnected"}
	 * 
	 * */
	//System variables
	static int port = 9991; //default port, changes with config file
	static String SQLip;
	static String SQLuser;
	static String SQLpass;
	final static String welcomeMessage = "{\"Response\":\"Connected\"}";
	final static String closedMessage = "{\"Response\":\"Disconnected\"}";
	final static String successMessage = "{\"Response\":\"Success\"}";
	
	//Configuration list map, Map of <config name, switch position>
	static Map<String, Integer> configList = new HashMap<String, Integer> (
			Map.ofEntries(
					new AbstractMap.SimpleEntry<String, Integer>("port", 0),    
					new AbstractMap.SimpleEntry<String, Integer>("SQLip", 1),
					new AbstractMap.SimpleEntry<String, Integer>("SQLlogin", 2)
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
		setting = setting.replaceAll(" ", "");
		String[] configArray = setting.split("=");
		
		switch(configList.get(configArray[0])) {
			case 0:
				port = Integer.parseInt(configArray[1]);
				break;
			case 1:
				SQLip = configArray[1];
				break;
			case 2:
				byte[] decodedBytes = Base64.getDecoder().decode(configArray[1]);
				String decodedString = new String(decodedBytes);
				String[] userArray = decodedString.split(":");
				SQLuser = userArray[0];
				SQLpass = userArray[1];
		}
	}

	private static void startServer() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            Socket connectionSocket = serverSocket.accept();

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

                if(parseCommand(serverPrintOut, line)) {
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
						e.printStackTrace();
					}
					break;
				case "startVM":
					if(control.startVM(command.getParam(0))) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
				case "shutdownVM":
					if(control.shutdownVM(command.getParam(0))) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
				case "destroyVM":
					if(control.destroyVM(command.getParam(0))) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
				case "undefineVM":
					if(control.undefineVM(command.getParam(0))) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
				case "defineVM":
					XMLdefinition newXML = new XMLdefinition();
					//TODO insert correct parameters into the below function using control.getParam(x), control.getParam(y) etc.
					String XML = newXML.getVMCreationXML();
					if(control.defineVM(XML)) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
				case "createDiskImage":
					if(control.createDiskImage(command.getParam(0), Float.parseFloat(command.getParam(1))).compareTo(command.getParam(0)) == 0) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
				case "deleteDiskImage":
					if(control.deleteDiskImage(command.getParam(0))) output.println(successMessage);
					else output.println(buildErrorJson("Failed to start VM"));
					break;
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