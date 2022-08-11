package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public void initialize(){
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                System.out.println("server start..");
                Socket accept = serverSocket.accept();
                System.out.println("client connected!");
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}
