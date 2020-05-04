import org.libvirt.*;

import StatisticsTypes.CpuStats;
import StatisticsTypes.DiskIOStats;
import StatisticsTypes.MemStats;
import StatisticsTypes.NetworkIOStats;


public class LibVirtAPIInterface {
	private static final int NUMMEMSTATS = 13;
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
	
	public MemStats getMemoryStats(String name) {
		try {
			Domain testDomain = conn.domainLookupByName(name);
			MemoryStatistic[] mems = testDomain.memoryStats(NUMMEMSTATS);
			if (mems.length == 0) {
				return new MemStats();
			}
			DomainInfo info = testDomain.getInfo();
			long used = 0;
			for(int i = 0; i < mems.length;i++) {
				if(mems[i].getTag() == 7) {
					used = mems[i].getValue();
				}
			}
			
			long total = info.memory;
			return new MemStats(total, used);
		  
		} catch (LibvirtException e) {
			System.out.println("Error in getMemoryStats, is domain off?");
			System.out.println(e.getError());
		}
		return new MemStats();
	}
	
	public NetworkIOStats getNetworkStats(String name) {
		return null;
		
	}
	
	public DiskIOStats getDiskStats(String name) {
		return null;
		
	}
	
}
