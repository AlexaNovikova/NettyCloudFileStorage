import commands.SendFileCommandData;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SendDirWithFilesFromClient {
     File dirFile;
    Network network;


    public SendDirWithFilesFromClient(File dirFile, Network network) {
        this.network=network;
        this.dirFile=dirFile;

    }

    public void sendDir(ObjectEncoderOutputStream os) throws IOException {

        File[] contents = dirFile.listFiles();

        if (contents != null) {

            for (File f : contents) {

                if (!Files.isSymbolicLink(f.toPath())) {
                    try {
                        if(f.isFile())
                        {
                            Command commandToServer = new Command().sendFile(f.getPath().replace(network.getClientDir()+File.separator, ""), f.length(),false);
                            os.writeObject(commandToServer);
                            os.flush();
                        }
                        if (f.isDirectory()) {

                            Command dirToSend = new Command().sendDirWithFiles(f.getPath().replace(network.getClientDir() + File.separator, ""), 1L);
                            os.writeObject(dirToSend);
                            os.flush();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            Command update = new Command().listFilesCommand();
            os.writeObject(update);
            os.flush();

        }

    }

}
