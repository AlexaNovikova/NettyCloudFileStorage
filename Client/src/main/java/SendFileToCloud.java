import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.*;

public class SendFileToCloud {
    String fileToSend;
    byte[] buffer ;
    Network network;


    public SendFileToCloud(String fileToServer, Network network) {
      this.fileToSend=fileToServer;
      this.network=network;
      buffer=new byte[8189];


    }

    public void sendFile(ObjectEncoderOutputStream os) throws IOException {
        File fileToServer = new File(fileToSend);
        String fileNameToServer = fileToSend.replace(network.getClientDir(), "");
        try (InputStream fis = new FileInputStream(fileToServer)) {
            int ptr;
            while((ptr=fis.read(buffer))>=0) {
                Command file = new Command().fileToServer(fileNameToServer, buffer, ptr);
                os.writeObject(file);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Command update = new Command().listFilesCommand();
        os.writeObject(update);
        os.flush();

    }
}