package StatisticsTypes;

public class NetworkIOStats {
	long rx = 0;
	long tx = 0;
	
	public NetworkIOStats() {
		
	}
	
	public NetworkIOStats(long rx, long tx) {
		this.rx = rx;
		this.tx = tx;
	}
	
	public long getTransmit() {
		return tx;
	}
	
	public long getRecieve() {
		return rx;
	}
	
	public String getJSON() {
		return "{\"Response\":\"Success\",\"tx\":"+getTransmit()+",\"rx\":"+getRecieve()+"}";
	}
	
}
