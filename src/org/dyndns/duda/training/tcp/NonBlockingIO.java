package org.dyndns.duda.training.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonBlockingIO {
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage: java <client port> <local server port>");
			System.exit(1);
		}
		int cPort = Integer.parseInt(args[0]);
		int sPort = Integer.parseInt(args[1]);
		SocketChannel ch = SocketChannel.open();
		Selector sel = sel = Selector.open();
		try {
			ch.socket().bind(new InetSocketAddress(cPort));
			ch.configureBlocking(false);
			// channel interested in performing read/write/connect
			ch.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE
					| SelectionKey.OP_CONNECT);
			// Unblocks when ready to read/write/connect
			sel.select();
			// Keys whose underlying channel is ready, the
			// operation this channel is interested in can be
			// performed without blocking.
			Iterator it = sel.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey key = (SelectionKey) it.next();
				it.remove();
				// Is underlying channel of key ready to connect?
				// if((key.readyOps() & SelectionKey.OP_CONNECT) != 0) {
				if (key.isConnectable()) {
					InetAddress ad = InetAddress.getLocalHost();
					System.out.println("Connect will not block");
					// You must check the return value of connect to make
					// sure that it has connected. This call being
					// non-blocking may return without connecting when
					// there is no server where you are trying to connect
					// Hence you call finishConnect() that finishes the
					// connect operation.
					if (!ch.connect(new InetSocketAddress(ad, sPort)))
						ch.finishConnect();
				}
				// Is underlying channel of key ready to read?
				// if((key.readyOps() & SelectionKey.OP_READ) != 0)
				if (key.isReadable())
					System.out.println("Read will not block");
				// Is underlying channel of key ready to write?
				// if((key.readyOps() & SelectionKey.OP_WRITE) != 0)
				if (key.isWritable())
					System.out.println("Write will not block");
			}
		} finally {
			ch.close();
			sel.close();
		}
	}
}
