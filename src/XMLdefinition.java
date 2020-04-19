import java.util.UUID;

/**
 * Class to generate XML configurations.
 *
 */
public class XMLdefinition {
	public XMLdefinition() {
		
	}
	
	String creationXMLTemplate = "<domain type='kvm'>\r\n" + 
			"	<name>%s</name>\r\n" + 
			"	<uuid>%s</uuid>\r\n" + 
			" 	<memory>125829120</memory>\r\n" + 
			"	<currentMemory>%d</currentMemory>\r\n" + 
			"	<vcpu current='%d'>24</vcpu>\r\n" + 
			"	<iothreads>1</iothreads>\r\n" + 
			"	<os>\r\n" + 
			"		<type>hvm</type>\r\n" + 
			"			<boot dev='hd'/>\r\n" + 
			"  			<boot dev='cdrom'/>\r\n" + 
			"  			<bootmenu enable='yes' timeout='8000'/>\r\n" + 
			"	</os>\r\n" + 
			"	<features>\r\n" + 
			"		<acpi/>\r\n" + 
			"		<apic/>\r\n" + 
			"	</features>\r\n" + 
			"	<clock offset='utc'/>\r\n" + 
			"	<network>\r\n" + 
			"		<name>default</name>\r\n" + 
			"		<uuid>2fe51d88-fe24-4f83-a883-c81364c0c234</uuid>\r\n" + 
			"		<bridge name='virbr0' stp='on' delay='0' />\r\n" + 
			"		<ip address='192.168.122.2' netmask='255.255.255.0'>\r\n" + 
			"			<dhcp>\r\n" + 
			"				<range start='192.168.122.2' end='192.168.122.254' />\r\n" + 
			"			</dhcp>\r\n" + 
			"    		</ip>\r\n" + 
			"	</network>\r\n" + 
			"\r\n" + 
			"	<devices>\r\n" + 
			"		<emulator>/usr/bin/kvm</emulator>\r\n" + 
			"\r\n" + 
			"		<disk type='file' device='disk'>\r\n" + 
			"			<driver name='qemu' type='qcow2'/>\r\n" + 
			"			<source file='/mnt/vm/boot-disk-%s.img'/>\r\n" + 
			"			<target dev='vda' bus='virtio'/>\r\n" + 
			"			<address type='pci' domain='0x0000' bus='0x00' slot='0x04' function='0x0'/>\r\n" + 
			"		</disk>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"		<disk type='file' device='cdrom'>\r\n" + 
			"			<driver name='qemu' type='raw'/>\r\n" + 
			"			<source file='/mnt/vm/iso/%s'/>\r\n" + 
			"			<target dev='hdc' bus='ide'/>\r\n" + 
			"			<readonly/>\r\n" + 
			"			<address type='drive' controller='0' bus='1' target='0' unit='0'/>\r\n" + 
			"		</disk>\r\n" + 
			"\r\n" + 
			"		<interface type='network'>\r\n" + 
			"			<source network='default'/>\r\n" + 
			"			<target dev='vnet2'/>\r\n" + 
			"			<model type='virtio'/>\r\n" + 
			"		</interface>\r\n" + 
			"\r\n" + 
			"		<controller type='ide' index='0'>\r\n" + 
			"			<address type='pci' domain='0x0000' bus='0x00' slot='0x01' function='0x1'/>\r\n" + 
			"		</controller>\r\n" + 
			"	\r\n" + 
			"		<input type='mouse' bus='ps2'/>\r\n" + 
			"		<input type='keyboard' bus='ps2'/>\r\n" + 
			"		<graphics type='vnc' port='%d' websocket='%d' listen='192.168.1.19' passwd='asouihfdiuk' keymap='en-us'/>\r\n" + 
			"		\r\n" + 
			"		<video>\r\n" + 
			"			<model type='vmvga' vram='16384' heads='1'>\r\n" + 
			"			</model>\r\n" + 
			"		</video>\r\n" + 
			"\r\n" + 
			"		<serial type='pty'>\r\n" + 
			"		      <source path='/dev/pts/1'/>\r\n" + 
			"     	              <target port='0'/>\r\n" + 
			"      	 	      <alias name='serial0'/>\r\n" + 
			"    		</serial>\r\n" + 
			"    		<console type='pty' tty='/dev/pts/1'>\r\n" + 
			"      			<source path='/dev/pts/1'/>\r\n" + 
			"      			<target type='serial' port='0'/>\r\n" + 
			"      			<alias name='serial0'/>\r\n" + 
			"    		</console>\r\n" + 
			"\r\n" + 
			"	</devices>\r\n" + 
			"</domain>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"";
	
	/**
	 * Generate an xml file for creation of virtual machines.
	 *
	 */
	public String getVMCreationXML(String name, int memory, int cpus, String os, int port) {
		String uuid = UUID.randomUUID().toString();
		int port1 = port;
		int port2 = port+1;
		String xml = String.format(creationXMLTemplate,name,uuid,memory,name,os,port1,port2);
		
		return xml;
	}
}
