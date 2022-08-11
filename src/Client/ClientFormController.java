package Client;

import java.io.IOException;
import java.net.Socket;

public class ClientFormController {
    Socket socket = null;
    public void initialize() throws IOException {
        socket = new Socket("localhost",5000);
    }
}
