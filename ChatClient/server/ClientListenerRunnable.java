package server;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientListenerRunnable implements Runnable {

  private ChatServer server;
  private Socket socket;

  public ClientListenerRunnable(Socket socket,
      ChatServer server) {
    this.socket = socket;
    this.server = server;
  }

  @Override
  public void run() {
    try (Scanner scanner = new Scanner(
        socket.getInputStream());
        PrintStream outStream = new PrintStream(
            socket.getOutputStream())) {
      String nick = scanner.nextLine();
      System.out.println("Nick von "
          + socket.getInetAddress() + " ist " + nick);
      server.register(nick, outStream);

      while (scanner.hasNextLine()) {
        System.out.println("Warte auf Nachricht von "
            + nick);
        String message = scanner.nextLine();
        server.pushMessage(nick + ": " + message);
      }
      server.deregister(nick);
    } catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      System.out.println("Verbindung mit Client "
          + socket.getInetAddress() + " getrennt.");
    }

  }
}
