
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
					break;
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
	
	public NetworkIOStats getNetworkStats(String name, String iface) {
		try {
			Domain testDomain = conn.domainLookupByName(name);
			
	        long rx = 0;
	        long tx = 0;
	        
	        final DomainInterfaceStats ifStats = testDomain.interfaceStats(iface);
            rx += ifStats.rx_bytes;
            tx += ifStats.tx_bytes;
	         
	        return new NetworkIOStats(rx,tx);
			} catch (LibvirtException e) {
				System.out.println("Error in getNetworkStats, is domain off?");
				System.out.println(e.getError());
			}
		return new NetworkIOStats();
	}
	
	public DiskIOStats getDiskStats(String name, String BlockDevice) {
		try {
			Domain testDomain = conn.domainLookupByName(name);
			long io_rd = 0;
            long io_wr = 0;
            long bytes_rd = 0;
            long bytes_wr = 0;
            
            final DomainBlockStats blockStats = testDomain.blockStats(BlockDevice);
            io_rd += blockStats.rd_req;
            io_wr += blockStats.wr_req;
            bytes_rd += blockStats.rd_bytes;
            bytes_wr += blockStats.wr_bytes;
            
            return new DiskIOStats(io_rd, io_wr, bytes_rd, bytes_wr);
		} catch (LibvirtException e) {
			System.out.println("Error in getDiskStats, is domain off?");
			System.out.println(e.getError());
		}
		return new DiskIOStats();
		
	}
}
