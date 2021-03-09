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
        Long fileSize = fileToServer.length();
        try (InputStream fis = new FileInputStream(fileToServer)) {
            int ptr;
            while (fileSize > buffer.length) {
                ptr = fis.read(buffer);
                Command file = new Command().fileToServer(fileNameToServer,buffer, ptr);
                fileSize -= ptr;
                os.writeObject(file);
                os.flush();
            }
            byte[] bufferLast = new byte[Math.toIntExact(fileSize)];
            ptr = fis.read(bufferLast);
            Command file = new Command().fileToServer(fileNameToServer,bufferLast, ptr);
            os.writeObject(file);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
      os.flush();
    }
}