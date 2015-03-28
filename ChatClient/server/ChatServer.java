package server;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ChatServer extends JFrame {

	private JLabel infoLabel, portInfo, serverAdresse;

	// main-Methode
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.setSize(150, 80);
		server.setVisible(true);
		server.setDefaultCloseOperation(ChatServer.EXIT_ON_CLOSE);
		server.start();
	}

	public ChatServer() {
		super("Chat Server");
		infoLabel = new JLabel();
		add(infoLabel, BorderLayout.NORTH);
		serverAdresse = new JLabel();
		add(serverAdresse, BorderLayout.CENTER);
		portInfo = new JLabel();
		add(portInfo, BorderLayout.SOUTH);

	}

	private Map<String, PrintStream> outStreams = new TreeMap<>();

	public void deregister(String nick) {
		synchronized (outStreams) {
			outStreams.remove(nick);
		}
		pushUsers();
	}

	public void pushMessage(String message) {
		String command = "message|" + message;
		sendToClients(command);
	}

	private void pushUsers() {
		Set<String> nicks = outStreams.keySet();
		StringBuffer command = new StringBuffer("users|");
		for (Iterator<String> it = nicks.iterator(); it.hasNext();) {
			command.append(it.next());
			if (it.hasNext()) {
				command.append(",");
			}
		}

		sendToClients(command.toString());
	}

	public void register(String nick, PrintStream outStream) {
		synchronized (outStreams) {
			outStreams.put(nick, outStream);
		}
		pushUsers();
	}

	private void sendToClients(String command) {
		synchronized (outStreams) {
			for (PrintStream outStream : outStreams.values()) {
				outStream.println(command);
			}
		}
	}

	private void start() {
		System.out.println("Server gestartet");
		infoLabel.setText("Server gestartet");
		try (ServerSocket serverSocket = new ServerSocket(1234)) {
			portInfo.setText("Port: "
					+ String.valueOf(serverSocket.getLocalPort()));
			serverAdresse.setText(InetAddress.getLocalHost().getHostAddress());
			while (true) {
				System.out.println("Warte auf Verbindungen...");
				Socket socket = serverSocket.accept();

				System.out.println("Neue Verbindung mit Client "
						+ socket.getInetAddress());
				ClientListenerRunnable runnable = new ClientListenerRunnable(
						socket, this);
				Thread thread = new Thread(runnable);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server beendet");
	}
}
