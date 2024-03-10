package serverforchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerForChat {

    static List<ClientThread> clientList = new ArrayList();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(30333);
        while (true) {
            Socket sock = server.accept();
            System.out.println("new client connected, address: "
                    + sock.getInetAddress().getCanonicalHostName());
            clientList.add(new ClientThread(sock));
        }
    }

    static class ClientThread implements Runnable {

        BufferedReader reader;
        BufferedWriter writer;
        private Socket sock;
        private Thread self;
        private final int id;
        private static int clientCount;

        public ClientThread(Socket sock) throws IOException {
            id = clientCount++;
            this.sock = sock;
            self = new Thread(this);
            setupAndStart();
        }

        public void setupAndStart() throws IOException {
            reader = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));

            writer = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            self.start();

        }

        void send(String s) throws IOException {
            writer.write(s);
            writer.newLine();
            writer.flush();
        }

        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = reader.readLine();
                    if (str.equals(null)) {
                        break;
                    }
                    System.out.println("client connected id = " + id);
                    System.out.println(str);
                    if ("exit".equals(str)) {
                        break;
                    }
                    for (ClientThread ct : clientList) {
                        if (!ct.equals(this)) {
                            String message = id + ": " + str;
                            ct.send(message);
                        }

                    }
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
            System.out.println("client- " + id + " disconnected");
        }
    }
}
