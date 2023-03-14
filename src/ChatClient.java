import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private ClientSocket clientSocket;
    private Scanner scanner;

    private AES256 aes256;

    private Integer type = 1;
    private int KEY_SIZE = 256;

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
            Boolean loop = true;
            do{
                System.out.print("Qual método de encriptação (1 - cesar; 2 - Vernam; 3 - AES;): ");
                try{
                    type = Integer.valueOf(scanner.nextLine());
                    loop = false;
                } catch (NumberFormatException e){
                    loop=true;
                    System.out.println("Numero inválido!");
                }

            }while (loop);

            if(type==3) {
                try {
                    aes256 = AES256.getInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            new Thread(this).start();
            messageLoop();
        } finally {
            clientSocket.close();
        }
    }


    private void messageLoop() throws IOException {
        String msg;
        String msgToSend;
        do {
            System.out.print("Digite uma mensagem (ou 'sair' para finalizar): ");
            msg = scanner.nextLine();
            if(msg.contains("/private")){
                String prefix = msg.split(" -> ")[0];
                String msgToEncrypt = msg.split(" -> ")[1];
                String msgEncrypted = generalEncrypter(msgToEncrypt, type);
                msgToSend = prefix + " -> " +msgEncrypted;
            }else {
                msgToSend = generalEncrypter(msg, type);
            }
            if(msg.equalsIgnoreCase("sair")){
                clientSocket.sendMsg(msg);
            }else {
                clientSocket.sendMsg(msgToSend);
            }
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
            if(!msg.contains(" -> ")){
                System.out.println(msg);
            }else {
                String prefix = msg.split(" -> ")[0];
                String msgToDecrypt = msg.split(" -> ")[1];
                String msgDecrypted = generalDecrypter(msgToDecrypt, type);
                System.out.println(prefix + " -> " + msgDecrypted);
            }
            System.out.print("Digite uma mensagem (ou 'sair' para finalizar): ");
        }
    }

    private String generalEncrypter(String msg, Integer cypherType){
        switch (cypherType) {
            case 1:
                return new Cesar().cesarEncrypt(msg);
            case 2:
                return new Vernam().verEncrypt(msg);
            case 3:
                try {
                    String key = aes256.getKey();
                    String iv = aes256.getIV();
                    String encryptedMessage = aes256.encrypt(msg);
                    return key + "#" + iv + "#" + encryptedMessage;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            default:
                return "Encriptador não encontrado.";
        }
    }

    private String generalDecrypter(String msg, Integer cypherType) {
        switch (cypherType){
            case 1:
                return new Cesar().cesarDecrypt(msg);
            case 2:
                return new Vernam().verDecrypt(msg);
            case 3:
                try {
                    String[] inputs = msg.split("#");
                    return aes256.decrypt(inputs[2], inputs[0], inputs[1]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            default:
                return "Decriptador nao encontrado.";
        }
    }


}
