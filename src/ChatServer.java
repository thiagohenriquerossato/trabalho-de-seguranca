import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {

    public static final int PORT = 3333;
    private ServerSocket serverSocket;
    private final List<ClientSocket> clients = new LinkedList<>();

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta: " + PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true){
            ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
            String username = clientSocket.getMessage().split(" ")[1];
            clientSocket.setUsername((username));
            System.out.println("Cliente " + clientSocket.getUsername() + " conectou!");

            clients.add(clientSocket);
            new Thread(()->clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket){
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null){
                if("sair".equalsIgnoreCase(msg)) return;
                if("/setUserName".equals(msg)) return;
                System.out.println("Mensagem de " + clientSocket.getUsername()
                        + " -> " + msg);
                sendMsgToAll(clientSocket, "Mensagem de " + clientSocket.getUsername()
                        + " -> " + msg);
            }
        }finally {
            clientSocket.close();
        }
    }
    private void sendMsgToAll(ClientSocket sender, String msg){
        Iterator<ClientSocket> iterator = clients.iterator();

        while (iterator.hasNext()){
            ClientSocket clientSocket = iterator.next();
            if(!sender.equals(clientSocket))
                if(!clientSocket.sendMsg(msg)){
                    iterator.remove();
                }
        }
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

}
