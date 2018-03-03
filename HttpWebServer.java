package throttleResources;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class HttpWebServer {
	private static int clientCount = 0;

	static Throttler throttler;
	static String pid = null;
	public HttpWebServer() {

	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		try {
			//args = new String[] { "9092" };
			if (args == null || args.length < 1) {
				System.out.println("Port number needs to be specified!");
				System.exit(0);
			}
			pid = args[0];
			throttler = new Throttler();
			int port = 9092;
			serverSocket = new ServerSocket(port);
			System.out.println("Parent server started on port : " + port);
			while (true) {
				// Main thread waits for socket to be read.
				Socket socket = serverSocket.accept();
				++clientCount;
				System.out.println("Clients connected: " + clientCount);
				// once child socket is open, it spawns a new child thread.
				ChildServerConcurrent childServer = new ChildServerConcurrent(socket);
				childServer.start();
				// System.out.println("Socket sent : "+socket);

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			serverSocket.close();
		}
	}

	static class ChildServerConcurrent extends Thread {

		final String CRLF = "\r\n";
		Socket socket;
		DataInputStream is;
		DataOutputStream os;

		public ChildServerConcurrent(Socket socket) {
			System.out.println("Child server started");
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				// System.out.println("Socket received: "+this.socket);
				is = new DataInputStream(this.socket.getInputStream());
				os = new DataOutputStream(this.socket.getOutputStream());

				String output = processGetRequest();
				os.write(output.getBytes());
				// This is where client input is processed and output is
				// given to the port

			} catch (IOException e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					os.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("deprecation")
		String processGetRequest() throws IOException {

			String output = null;
			while (true) {
				// Input is read from the client line by line.
				String headerLine = is.readLine();
				if (headerLine == null || headerLine.equals(CRLF) || headerLine.equals("")) {
					break;
				}

				// Individual input tokens are parsed here.
				StringTokenizer stoken = new StringTokenizer(headerLine);
				String requestType = stoken.nextToken();

				// Based on the request type, Response is printed along
				// with the requested url.
				if (requestType.equals("GET")) {
					System.out.println("Request Type:" + requestType);
					String fileName = stoken.nextToken();
					System.out.println("URL Requested:" + fileName);
					output = throttler.readBashScript(pid);
					output = constructResponse(output);
					System.out.println("Output:" + output);
					// sendFile(fileName);

				}
			}
			return output;
		}


		private String constructResponse(String json){
			StringBuilder sb = new StringBuilder();
			//String result = "HTTP/1.1 200 OK Content-Type:application/json Content-Length:"+ json.length()+" "+json;

			sb.append("HTTP/1.1 200 OK\r\n");
			sb.append("Content-Type:application/json\r\n");
			sb.append("Content-Length:" + String.valueOf(json.length() + 4)+"\r\n");
			sb.append("\r\n");
			sb.append("\r\n");
			sb.append("\r\n");
			sb.append(json);
			sb.append("\r\n");
			return sb.toString();
		}
		/*private void sendFile(String fileName) throws IOException {
			FileInputStream bis = null;
			File file = new File(BASE_PATH + fileName);
			if (file.exists()) {
				String statusLine = "HTTP/1.1 200 Ok" + CRLF;
				String contentType = "Content-type: " + contentType(fileName) + CRLF;
				writeBytes(statusLine);
				writeBytes(contentType);
				writeBytes(CRLF);
				bis = new FileInputStream(file);
				byte[] fileBytes = new byte[(int) file.length()];

				while (bis.read(fileBytes) != -1) {
					os.write(fileBytes, 0, (int) file.length());
				}
				bis.close();
			} else { // Create status line
				String statusLine = "HTTP/1.1 404 Not found" + CRLF;
				// response line // header line. Only interested in the type of
				// object we are sending back
				String contentType = "Content-type: " + contentType("html") + CRLF;
				// Entity body. HTML file with message Not Found as the text
				String entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
						+ "<BODY>Not Found</BODY></HTML>";
				writeBytes(statusLine);
				writeBytes(contentType);
				writeBytes(CRLF);
				writeBytes(entityBody);
			}
		}

		private void writeBytes(String data) throws IOException {
			os.writeBytes(data);
		}

		private String contentType(String fileName) {
			if (fileName.endsWith("html")) {
				return ("text/html");
			} else if (fileName.endsWith("jpg")) {
				return ("image/jpg");
			} else if (fileName.endsWith("jpeg")) {
				return ("image/jpeg");
			} else if (fileName.endsWith("gif")) {
				return ("image/gif");
			} else {
				return ("application/octet-stream");
			}
		}*/
	}
}

// References:
// http://www.java2s.com/Tutorials/Java/Graphics_How_to/Image/Transfer_Image_with_Socket_stream.htm
