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
//
        File[] contents = dirFile.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    try {
//                        File fileToSend = new File(f.getPath());
                        if(f.isFile())
                        {   String filePath = f.getPath();
                            System.out.println(filePath.replace(network.getClientDir()+File.separator,""));
                            Command commandToServer = new Command().sendFile(f.getPath().replace(network.getClientDir()+File.separator, ""), f.length());
                            os.writeObject(commandToServer);
                            os.flush();
//                            SendFileToCloud sendFileToCloud= new SendFileToCloud(filePath);
//                            sendFileToCloud.sendFile(os);
                        }
                        if (f.isDirectory()){

                            Command dirToSend = new Command().sendDirWithFiles(f.getPath().replace(network.getClientDir()+File.separator,""), 1L);
                            os.writeObject(dirToSend);
                            os.flush();
//                            Command createDir = new Command().createNewDir((f.getPath()).replace(network.getClientDir(), ""));
//                            os.writeObject(createDir);
//                            os.flush();
//                            SendDirWithFilesFromClient sendDirWithFilesFromClient= new SendDirWithFilesFromClient(f, network);
//                            sendDirWithFilesFromClient.sendDir(os);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            os.flush();
        }
    }
}
