import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Class for executing external system commands.
 *
 */
public class SystemCommand {
	int lastExitCode = 0;
	
	public final int getLastExitCode() {
		return lastExitCode;
	}

	public SystemCommand() {
		
	}
	
	public String executeCommand(String command) throws Exception {
		StringWriter bufferedOutput = new StringWriter();
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", command);
		
		try {
			Process process = processBuilder.start();
			BufferedReader reader =  new BufferedReader(new InputStreamReader(process.getInputStream()));
			//Read the output from the command if there is any.
			String line;
			while ((line = reader.readLine()) != null) {
				bufferedOutput.write(line);
				bufferedOutput.write("\n");
            }
			
			//Wait for the process. Exit code is not that useful, but its saved in case.
			lastExitCode = process.waitFor();
			
		} catch (IOException e) {
			throw new Exception("Command Failure.");
		} catch (InterruptedException e) {
			throw new Exception("Command Failure.");
		}
		
		return bufferedOutput.toString();
	}
	
}
