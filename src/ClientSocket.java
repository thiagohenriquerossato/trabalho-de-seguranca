import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSocket {

    private  final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;


    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
    public SocketAddress getRemoteSocketAddress(){
        return this.socket.getRemoteSocketAddress();
    }

    public void close(){
        try {
            in.close();
            out.close();
            socket.close();
        }catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getMessage(){
        try {
            return in.readLine();
        }catch (IOException e){
            return null;
        }
    }

    public boolean sendMsg(String msg){
        out.println(msg);
        return !out.checkError();
    }
}
