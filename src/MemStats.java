public class MemStats {
	private long total = 0;
	private long free = 0;
	
	public final long getTotal() {
		return total;
	}

	public final long getFree() {
		return free;
	}
	
	
	public MemStats() {
		
	}

	public MemStats(long total, long free ) {
		this.total = total;
		this.free = free;
	}
	
	public float getPercentageUsed() {
		if(total == 0 || free == 0) {
			return 0;
		}
		long used = total-free;
		double fused = used;
		double ftotal = total;
		return (float) (fused / ftotal * 100);
	}

	public String getJSON() {
		return "{\"Response\":\"Success\",\"total\":"+getTotal()+",\"free\":"+getFree()+",\"percentUsed\":"+getPercentageUsed()+"}";
	}
}
