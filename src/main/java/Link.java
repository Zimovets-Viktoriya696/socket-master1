import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vika on 18.07.17.
 */
public class Link {
    String  host;
    int port;
    CallBack callBack;
    Socket clientSocket = null;

    Link (String host, int port){
        this.host = host;
        this.port = port;
    }

    public void connect(){
        try {
            clientSocket = new Socket("localhost", 4444);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage (Message message){
        Gson gson = new Gson();
        String json = gson.toJson(message);
        int sizeOfMessageInt = json.length();
        byte[] sizeOfByts = BigInteger.valueOf(sizeOfMessageInt).toByteArray();
        byte[] massOfBytsOfMessage = json.getBytes();
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(sizeOfByts);
            outputStream.write(massOfBytsOfMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMessage (CallBack callBack){
        byte[] sizeOfMessage = new byte[4];
        while (true) {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                int size = inputStream.read(sizeOfMessage);
                byte[] message = new byte[size];
                inputStream.read(message);
                JSONObject testV=new JSONObject(new String(message));
                String dataInText = testV.getString("date");
                DateFormat data = new SimpleDateFormat("mm/dd/yyyy");
                try {
                    Date startDate = data.parse(dataInText);
                    String name = testV.getString("name");
                    String text = testV.getString("text");
                    Message message1 = new Message(startDate, name, text);
                    callBack.onMessage(message1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



