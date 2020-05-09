import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProcessRequest extends Thread {

    Socket socket = null;
    Site localSite = null;

    public ProcessRequest(Socket socket, Site site) {
        this.socket = socket;
        this.localSite = site;
    }

    public void run() {

        BufferedReader bufferedReader = null;
        PrintWriter out = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String command = "";
            String[] message = null;

            command = bufferedReader.readLine();
//            System.out.println(command);
            if (command != null) {
                if (command.charAt(0) == 'r') {
                    message = command.split(",");
                    System.out.println("Site-"+Integer.parseInt(message[1])+" has requested for Critical Section");
                    localSite.processCriticalSectionReq(Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                }

                if (command.charAt(0) == 't') {
                    message = command.split(",");
                    localSite.tokenQueue.clear();
                    int length=message.length;
                    for(int i=1;i<length;i++) {
                        localSite.tokenQueue.add(Integer.parseInt(message[i]));
                    }
                    localSite.token = 1;
                }

                if (command.charAt(0) == 'l') {
                    message = command.split(",");
                    System.out.println("Site-"+Integer.parseInt(message[1])+" has left the Critical Section");
                    localSite.updateLN(Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                    System.out.println("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                bufferedReader.close();
                // out.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

