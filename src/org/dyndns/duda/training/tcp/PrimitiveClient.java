package org.dyndns.duda.training.tcp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class PrimitiveClient {

	public static void main(String[] args) throws IOException {
		InetAddress addr = InetAddress.getByName("192.168.2.103");
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, JabberServer.PORT);
		try {
			System.out.println("socket = " + socket);
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			while (true) {
				Scanner scanner = new Scanner(System.in);
				String text = scanner.next();
				out.println(text);
			}
		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}
}
