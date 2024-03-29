package org.dyndns.duda.training.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class JabberClientThread extends Thread {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private static int counter = 0;
	private int id = counter++;
	private static int threadcount = 0;

	public static int threadCount() {
		return threadcount;
	}

	public JabberClientThread(InetAddress addr) {
		System.out.println("Making client " + id);
		threadcount++;
		try {
			socket = new Socket(addr, MultiJabberServer.PORT);
		} catch (IOException e) {
			System.err.println("Socket failed");
			// If the creation of the socket fails, nothing needs to be cleaned up.
		}
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Enable auto-flush:
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);
			start();
		} catch (IOException e) {
			// The socket should be closed on any failures other than the socket
			// constructor:
			try {
				socket.close();
			} catch (IOException e2) {
				System.err.println("Socket not closed");
			}
		}
		// Otherwise the socket will be closed by the run() method of the thread.
	}

	public void run() {
		try {
			for (int i = 0; i < 25; i++) {
				out.println("Client " + id + ": " + i);
				String str = in.readLine();
				System.out.println(str);
			}
			out.println("END");
		} catch (IOException e) {
			System.err.println("IO Exception");
		} finally {
			// Always close it:
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Socket not closed");
			}
			threadcount--; // Ending this thread
		}
	}
}
