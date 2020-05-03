
public class CpuStats {
	private long time = 0;
	private int cpus = 0;
	
	public final long getTime() {
		return time;
	}

	public final int getCpus() {
		return cpus;
	}
	
	public void appendTime(long time) {
		this.time = this.time + time;
	}

	public void appendCpus(int cpus) {
		this.cpus = this.cpus + cpus;
	}
	
	public CpuStats() {
		
	}

	public CpuStats(long time, int cpus ) {
		this.time = time;
		this.cpus = cpus;
	}

	public String getJSON() {
		return "{\"Response\":\"Success\",\"cpus\":"+cpus+",\"time\":"+time+"}";
	}
}
