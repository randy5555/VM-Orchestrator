import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	public boolean startVM(String VMname){
		SystemCommand cmd = new SystemCommand();
		String result;
		
		try {
			result = cmd.executeCommand("virsh start " + VMname);
			BufferedReader reader = new BufferedReader(new StringReader(result));
			String line = reader.readLine();
			//Do something with line. Not all commands give a response. Not important for these at the moment.
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gently stop/shutdown a vm, like pushing power button, windows shuts down sort of deal.
	 *
	 */
	public boolean shutdownVM(String VMname){
		SystemCommand cmd = new SystemCommand();
		String result;
		try {
		result = cmd.executeCommand("virsh shutdown "+VMname);
		BufferedReader reader = new BufferedReader(new StringReader(result));
		String line = reader.readLine();
		//Do something with line. Not all commands give a response. Not important for these at the moment.
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Forcefully stop a vm, pulling the power plug.
	 *
	 */
	public boolean destroyVM(String VMname) {
		SystemCommand cmd = new SystemCommand();
		String result;
		
		try {
		result = cmd.executeCommand("virsh destroy "+VMname);
		BufferedReader reader = new BufferedReader(new StringReader(result));
		String line = reader.readLine();
		//Do something with line. Not all commands give a response. Not important for these at the moment.
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes a VM, the definition is wiped.
	 *
	 */
	public boolean undefineVM(String VMname) {
		SystemCommand cmd = new SystemCommand();
		String result;
		
		try {
		result = cmd.executeCommand("virsh undefine "+VMname);
		BufferedReader reader = new BufferedReader(new StringReader(result));
		String line = reader.readLine();
		//Do something with line. Not all commands give a response. Not important for these at the moment.
		} catch (Exception e) {
			return false;
		}
		deleteDiskImage(VMname);
		deleteVmXMLfile(VMname);
		
		return true;
	}
	
	/**
	 * Create and define a virtual machine using the XML definition.
	 *
	 */
	public boolean defineVM(XMLdefinition definition) {
		boolean img_created = createDiskImage(definition.getName(),definition.getDiskSizeGB());
		if(!img_created) {
			return false;
		}
		String xml_path = "/var/vm/" + definition.getName() + ".xml";
		String createVmXML = definition.getVMCreationXML();
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(xml_path));
			writer.write(createVmXML);
			writer.close();
		} catch (IOException e) {
			deleteDiskImage(definition.getName());
			deleteVmXMLfile(definition.getName());
		}
	    
		SystemCommand cmd = new SystemCommand();
		String img_create = "virsh define " + xml_path;
		String result;
		try {
			result = cmd.executeCommand(img_create);
			BufferedReader reader = new BufferedReader(new StringReader(result));
			String line = reader.readLine();
			//Do something with line. Not all commands give a response. Not important for these at the moment.
			} catch (Exception e) {
				deleteDiskImage(definition.getName());
				deleteVmXMLfile(definition.getName());
				return false;
			}
		
		//virsh define x
		return true;
	}
	
	/**
	 * Creates the disk image for the VM
	 *
	 */
	private boolean createDiskImage(String VMname, float SizeInGB) {
		//generate the path to the disk image, then create it.
		SystemCommand cmd = new SystemCommand();
		String path = "/mnt/vm/boot-disk-" + VMname + ".img";
		String img_create = String.format("qemu-img create -f qcow2 %s %.1fG", path, SizeInGB);
		String result;
		try {
			result = cmd.executeCommand(img_create);
			BufferedReader reader = new BufferedReader(new StringReader(result));
			String line = reader.readLine();
			//Do something with line. Not all commands give a response. Not important for these at the moment.
			} catch (Exception e) {
				deleteDiskImage(VMname);
				return false;
			}
		
		return true;
		
	}
	
	/**
	 * Deletes the disk image for the VM.
	 *
	 */
	private boolean deleteDiskImage(String VMname) {
		String path = "/mnt/vm/boot-disk-" + VMname + ".img";
		File file = new File(path); 
		return file.delete();
	}
	
	/**
	 * Deletes the VM definition XML file.
	 *
	 */
	private boolean deleteVmXMLfile(String VMname) {
		String path = "/mnt/vm/boot-disk-" + VMname + ".img";
		File file = new File(path); 
		return file.delete();
	}
}
