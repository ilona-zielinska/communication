package org.dyndns.duda.training.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class JabberClient1 {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java JabberClient1 <client-port>");
			System.exit(1);
		}
		int clPrt = Integer.parseInt(args[0]);
		SocketChannel sc = SocketChannel.open();
		Selector sel = Selector.open();
		try {
			sc.configureBlocking(false);
			sc.socket().bind(new InetSocketAddress(clPrt));
			sc.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE
					| SelectionKey.OP_CONNECT);
			int i = 0;
			// Because of the asynchronous nature you do not know
			// when reading and writing is done, hence you need to
			// keep track of this, boolean written is used to
			// alternate between read and write. Whatever is written
			// is echoed and should be read.
			// boolean done is used to check when to break out of
			// the loop
			boolean written = false, done = false;
			// JabberServer.java to which this client connects writes
			// using BufferedWriter.println(). This method performs
			// encoding according to the defualt charset
			String encoding = System.getProperty("file.encoding");
			Charset cs = Charset.forName(encoding);
			ByteBuffer buf = ByteBuffer.allocate(16);
			while (!done) {
				sel.select();
				Iterator it = sel.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					sc = (SocketChannel) key.channel();
					if (key.isConnectable() && !sc.isConnected()) {
						InetAddress addr = InetAddress.getByName(null);
						boolean success = sc.connect(new InetSocketAddress(
								addr, JabberServer.PORT));
						if (!success)
							sc.finishConnect();
					}
					if (key.isReadable() && written) {
						if (sc.read((ByteBuffer) buf.clear()) > 0) {
							written = false;
							String response = cs
									.decode((ByteBuffer) buf.flip()).toString();
							System.out.print(response);
							if (response.indexOf("END") != -1)
								done = true;
						}
					}
					if (key.isWritable() && !written) {
						if (i < 10)
							sc.write(ByteBuffer.wrap(new String("howdy " + i
									+ '\n').getBytes()));
						else if (i == 10)
							sc.write(ByteBuffer.wrap(new String("END\n")
									.getBytes()));
						written = true;
						i++;
					}
				}
			}
		} finally {
			sc.close();
			sel.close();
		}
	}
}
