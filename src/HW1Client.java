
import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

public class HW1Client {

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Usage: java EchoClient <host name> <port number>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		int udpPortNumber = Integer.parseInt(args[2]);
		URL url = null;

		try (Socket echoSocket = new Socket(hostName, portNumber);
				Socket nechoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())); 
				BufferedReader into = new BufferedReader(new InputStreamReader(nechoSocket.getInputStream()));// inputstreamreader
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				DatagramSocket ds = new DatagramSocket(udpPortNumber);
				DatagramSocket ds2 = new DatagramSocket()
						
		) {
			
			InetAddress ip = InetAddress.getByName(hostName);
			byte[] buffer = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ip, portNumber);
			ds.send(dp);

			String res = "";

			//Accept msgs from TCP 
			while ((res = in.readLine()) != null) {
				System.out.println("Recieved msg from TCP " + res);
			}
			
			//Accept data from UDP
			byte[] buf = new byte[1024];
			int i = 1;
			while (true) {
				//System.out.println("----");
				//ip = InetAddress.getByName(hostName);
				if (i == 50) { 	//add delay to the last packet

					try {
						Thread.sleep(5000);
						buf = new byte[50];
						DatagramPacket dpReceive = new DatagramPacket(buf, buf.length);
						ds.receive(dpReceive);
						String str = new String(dpReceive.getData());
						buf = new byte[50];
						System.out.println("Recieved msg from UDP " + str);
						
						//send ack via UDP
						String str1 = "ACK rec";
						byte[] ackbuf = new byte[50];
						DatagramPacket dpAck = new DatagramPacket(str1.getBytes(), str1.getBytes().length, ip, 8081);
						ds.send(dpAck);
						
						//Accept data from TCP since there is delay
						res = into.readLine();	
						System.out.println("Recieved msg from TCP "+res);
						i++; 				//counter to keep track of incoming packets
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					buf = new byte[50];
					DatagramPacket dpReceive = new DatagramPacket(buf, buf.length);
					ds.receive(dpReceive);
					String str = new String(dpReceive.getData());
					buf = new byte[50];
					System.out.println("Recieved msg from UDP " + str);

					String str1 = "ACK rec";
					byte[] ackbuf = new byte[50];
					DatagramPacket dpAck = new DatagramPacket(str1.getBytes(), str1.getBytes().length, ip, 8081);
					ds2.send(dpAck);
					i++;					//counter to keep track of incoming packets

				}
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);

		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
		}

	}

}
