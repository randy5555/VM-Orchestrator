package StatisticsTypes;

public class DiskIOStats {
	long reads = 0;
	long writes = 0;
	long readBytes = 0;
	long writeBytes = 0;
	
	public DiskIOStats() {
		
	}
	
	public DiskIOStats(long reads, long writes, long readBytes, long writeBytes) {
		this.reads = reads;
		this.writes = writes;
		this.readBytes = readBytes;
		this.writeBytes = writeBytes;
	}
	
	public long getReads() {
		return reads;
	}
	
	public long getWrites() {
		return writes;
	}
	
	public long getReadBytes() {
		return readBytes;
	}
	
	public long getWriteBytes() {
		return writeBytes;
	}
}
