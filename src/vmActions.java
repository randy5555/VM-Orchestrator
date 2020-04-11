import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Class for each of the expected system commands executed for the project
 *
 */
public class vmActions {
	public vmActions() {
		
	}
	
	/**
	 * A list of running VM's on the system.
	 *
	 */
	public ArrayList<String> getRunningVMs() throws Exception {
		ArrayList<String> runningVMs = new ArrayList<String>();
		SystemCommand cmd = new SystemCommand();
		
		String result = cmd.executeCommand("virsh list");
		BufferedReader reader = new BufferedReader(new StringReader(result));
		String line = reader.readLine();
		int count = 0;
		while (line != null) {
			if(count >= 2) {
				String trimmed = line.trim().replaceAll("\\s{2,}", " ");
				String[] splitted = trimmed.split(" ");
				runningVMs.add(splitted[1]);
			}
			line = reader.readLine();
			count++;
		}
		
		return runningVMs;
	}
	
	/**
	 * A virtual machine can be in an off state, start it with this command.
	 *
	 */
	public boolean startVM(String VMname) {
		//virsh start x
		return true;
	}
	
	/**
	 * Gently stop/shutdown a vm, like pushing power button, windows shuts down sort of deal.
	 *
	 */
	public boolean shutdownVM(String VMname) {
		//virsh shutdown x
		return true;
	}
	
	/**
	 * Forcefully stop a vm, pulling the power plug.
	 *
	 */
	public boolean destroyVM(String VMname) {
		//virsh destroy x
		return true;
	}
	
	/**
	 * Deletes a VM, the definition is wiped.
	 *
	 */
	public boolean undefineVM(String VMname) {
		//virsh undefine x
		return true;
	}
	
	/**
	 * Create and define a virtual machine using the XML definition.
	 *
	 */
	public boolean defineVM(String XMLdefinition) {
		//create disk image
		//virsh define x
		return true;
	}
	
	public String createDiskImage(String VMname, float SizeInGB) {
		//generate the path to the disk image, then create it.
		return VMname;
		
	}
	
	public boolean deleteDiskImage(String path) {
		return true;
	}
}
