import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {

    public static final int PORT = 3333;
    private ServerSocket serverSocket;
    private List<String> userNames = new LinkedList<>();
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
            userNames.add(username);
            System.out.println("Cliente " + clientSocket.getUsername() + " conectou!");

            clients.add(clientSocket);
            new Thread(()->clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket){
        String msg;
        String msgFormated;
        String dest;

        try {
            while ((msg = clientSocket.getMessage()) != null){
                if("sair".equalsIgnoreCase(msg)) return;
                if("/setUserName".equals(msg)) return;
                msgFormated = "Mensagem de " + clientSocket.getUsername()
                        + " -> " + msg;
                System.out.println(msgFormated);
                if( msg.contains("/private")){
                    String receptor = msg.split(" ")[1];
                    msg = msg.split("-> ")[1];
                    msgFormated = "Mensagem de " + clientSocket.getUsername()
                            + " -> " + msg;
                    sendMsgToUser(clientSocket, receptor, msgFormated);
                }else{
                    sendMsgToAll(clientSocket, msgFormated);
                }

            }
        }finally {
            clientSocket.close();
        }
    }

    private void sendMsgToUser(ClientSocket sender, String dest, String msg) {
        Boolean sent = false;
        for(ClientSocket clientSocket: clients){
            if(clientSocket.getUsername().equals(dest)){
                sent = true;
                clientSocket.sendMsg(msg);
            }
        }
        if(!sent){
            sender.sendMsg("Mensagem não enviada! Usuario não está conectado!");
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
