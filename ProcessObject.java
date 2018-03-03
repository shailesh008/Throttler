package throttleResources;

public class ProcessObject {
	private String pid;
	private String cpu;
	private String memory;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	@Override
	public String toString() {
		return "{\"pid\" : \"" + pid + "\",\"cpu\" : \"" + cpu + "\", \"memory\" : \"" + memory + "\"}";

	}

	public String getJson(boolean isRunning) {
		String state = isRunning ? "Running" : "Sleeping";
		return "{\"pid\" : \"" + pid + "\",\"cpu\" : \"" + cpu + "\", \"memory\" : \"" + memory + "\", \"state\" : \""
				+ state + "\"}";

	}

}
