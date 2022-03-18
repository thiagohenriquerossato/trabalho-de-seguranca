import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private ClientSocket clientSocket;
    private Scanner scanner;

    public ChatClient(){
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        System.out.print("Escolha um nome de usuario: ");
        String userName = scanner.nextLine();
        try {
            clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, ChatServer.PORT));
            clientSocket.setUsername(userName);
            clientSocket.sendMsg("/setUserName " + userName);
            System.out.println("Client iniciado na porta: " + ChatServer.PORT);
            new Thread(this).start();
            messageLoop();
        } finally {
            clientSocket.close();
        }

    }


    private void messageLoop() throws IOException {
        String msg;
        do {
            System.out.print("Digite uma mensagem (ou 'sair' para finalizar): ");
            msg = scanner.nextLine();
            clientSocket.sendMsg(msg);
        }while (!msg.equalsIgnoreCase("sair"));
    }


    public static void main(String[] args) {

        try {
            ChatClient client = new ChatClient();
            client.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o client: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String msg;
        while ((msg=clientSocket.getMessage()) != null) {
            System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b                         ");
            System.out.println(msg);
            System.out.print("Digite uma mensagem (ou 'sair' para finalizar): ");

        }
    }
}
