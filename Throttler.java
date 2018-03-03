package throttleResources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.jackson.map.ObjectMapper;

public class Throttler {

	private  final String PATH = new File("").getAbsolutePath();

	/**
	 * Default constructor.
	 */
	public Throttler() {
		System.out.println(PATH);
	}

	/**
	 * Execute the input file. Read contents of input file and save to inputData.
	 */
	public ProcessObject readInputFile(String pid) {
		String processString = "";

		try {
			String fileName = "sh " + PATH + "/script.sh " + pid;
			System.out.println("resource " + fileName);
			Process proc = Runtime.getRuntime().exec(fileName);
			BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			try {
				proc.waitFor();

			} catch (InterruptedException e) {

				System.out.println(e.getMessage());
			}
			while (read.ready()) {
				processString += read.readLine();
			}
			read.close();

			try {
				ObjectMapper mapper = new ObjectMapper();
				ProcessObject inputAttr = mapper.readValue(processString, ProcessObject.class);

				return inputAttr;

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Read contents of config file and save to configData.
	 */
	public ProcessObject readConfigFile() {
		String processString = "";
		try {
			String fileName = PATH + "/test.txt";
			BufferedReader read = new BufferedReader(new FileReader(fileName));
			while (read.ready()) {
				processString += read.readLine();
			}
			read.close();
			try {
				// System.out.println("config");
				ObjectMapper mapper = new ObjectMapper();
				ProcessObject configAttr = mapper.readValue(processString, ProcessObject.class);

				return configAttr;

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Compare each element of inputData and configData. If an inputData element >
	 * configData element, call sleepProcess().
	 */
	private boolean monitorProcess(ProcessObject input, ProcessObject textFile) {

		System.out.println("Input : " + input);
		System.out.println("Config : " + textFile);
		int pid = 0;
		boolean isExceeding = false;

		try {

			pid = Integer.parseInt(input.getPid());
			if (Double.parseDouble(input.getCpu()) > Double.parseDouble(textFile.getCpu()))
				isExceeding = true;
			if (Double.parseDouble(input.getMemory()) > Double.parseDouble(textFile.getMemory()))
				isExceeding = true;

			if (isExceeding) {
				sleepProcess(pid);
				return false;
			} else {
				wakeProcess(pid);
			}

		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}

	private void sleepProcess(int pid) {

		String command = "kill -TSTP " + pid;
		executeCommand(command);
	}

	private void wakeProcess(int pid) {

		String command = "kill -CONT " + pid;
		executeCommand(command);
	}

	public String readBashScript(String pid) {
		try {

			ProcessObject config = readConfigFile();
			System.out.println(config.getPid());
			ProcessObject input = readInputFile(pid);
			boolean state = monitorProcess(input, config);

			String json = input.getJson(state);
			return json;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "NoOutput";
	}

	private void executeCommand(String command) {

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader read = new BufferedReader(new InputStreamReader(p.getInputStream()));
			try {
				p.waitFor();
				while (read.ready()) {
					System.out.println(read.readLine());
				}
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
