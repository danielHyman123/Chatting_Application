import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static ArrayList<PrintWriter> clients = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);


                synchronized (clients) {
                    clients.add(out);
                }
                username = in.readLine();
                names.add(username);

                String message;
                while ((message = in.readLine()) != null && !((message = in.readLine()).equals(username))) {
                    System.out.println(names + message);
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clients) {
                    clients.remove(out);
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients) {
                    writer.println(message);
                }
            }
        }
    }
}
