public class MemStats {
	private long total = 0;
	private long used = 0;
	
	public final long getTotal() {
		return total;
	}

	public final long getUsed() {
		return used;
	}
	
	
	public MemStats() {
		
	}

	public MemStats(long total, long used ) {
		this.total = total;
		this.used = used;
	}
	
	public float getPercentageUsed() {
		if(total == 0 || used == 0) {
			return 0;
		}
		double fused = used;
		double ftotal = total;
		if(fused > ftotal) {
			ftotal = fused;
		}
		return (float) (fused / ftotal * 100);
	}

	public String getJSON() {
		return "{\"Response\":\"Success\",\"total\":"+getTotal()+",\"used\":"+getUsed()+",\"percentUsed\":"+getPercentageUsed()+"}";
	}
}
