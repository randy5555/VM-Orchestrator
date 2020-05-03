import org.libvirt.*;


public class LibVirtAPIInterface {
	private  Connect conn = null;
	
	public LibVirtAPIInterface() {
		try{
            conn = new Connect("qemu:///system");
        } catch (LibvirtException e) {
            System.out.println("exception caught:"+e);
            System.out.println(e.getError());
            conn = null;
        }
	}
	
	public void finalize() throws Throwable {
		if(conn != null) {
			conn.close();
		}
	}
	
	public CpuStats getCPUStats(String name) {
		CpuStats cpustats = new CpuStats();
		try{
			Domain testDomain = conn.domainLookupByName(name);
			VcpuInfo[] cpuinfo = testDomain.getVcpusInfo();
			for(VcpuInfo cpu : cpuinfo) {
				if(cpu.state == VcpuInfo.VcpuState.VIR_VCPU_RUNNING) {
					cpustats.appendTime(cpu.cpuTime);
					cpustats.appendCpus(1);
				}
			}
		} catch (LibvirtException e) {
            System.out.println("Error in getCPUStats, is domain off?");
            System.out.println(e.getError());
        }
		return cpustats;	
	}
	
	
	
	
	
}
