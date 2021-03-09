import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SendDirWithFilesFromCloud {

        File dirFromCloud;
        MyFileHandler myFileHandler;

        public SendDirWithFilesFromCloud (File dirFromCloud, MyFileHandler fileHandler) {
            this.dirFromCloud = dirFromCloud;
            this.myFileHandler= fileHandler;
        }

        public void execute(ChannelHandlerContext ctx) {
            String dirPath =dirFromCloud.getPath().replace(myFileHandler.getServerDir(), "");
            Command createDirOnClient = new Command().createNewDir(dirPath);
            ctx.writeAndFlush(createDirOnClient);
            File[] contents = dirFromCloud.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    if (!Files.isSymbolicLink(f.toPath())) {
                        try {
                            File fileFromServer = new File(dirFromCloud + File.separator + f.getName());
                            if (fileFromServer.isFile()) {
                                Long fileSize =fileFromServer.length();
                                Command commandFile = new Command().sendFile(dirPath+File.separator+ fileFromServer.getName(), fileSize);
                                ctx.writeAndFlush(commandFile);
                                SendFileFromCloudToClient sendFileFromCloudToClient = new SendFileFromCloudToClient(fileFromServer);
                                sendFileFromCloudToClient.createCommandAndSend(ctx);
//                                fileFromServer.delete();
                           }
                            if (fileFromServer.isDirectory()) {

                                SendDirWithFilesFromCloud sendDir = new SendDirWithFilesFromCloud(fileFromServer, myFileHandler);
                                sendDir.execute(ctx);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                }
//            dirFromCloud.delete();
        }
    }


