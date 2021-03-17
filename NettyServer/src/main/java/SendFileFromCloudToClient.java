import commands.SendFileCommandData;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SendFileFromCloudToClient {

    File fileToSend;
    byte[] buffer;

    public SendFileFromCloudToClient (File fileToSend) throws IOException {
        this.fileToSend = fileToSend;
        buffer=new byte[8189];
    }

    public void createCommandAndSend (ChannelHandlerContext ctx) {

        try (InputStream fis = new FileInputStream(fileToSend)) {
            int ptr;
            while ((ptr=fis.read(buffer))>=0) {
                Command fileToClient = new Command().file(buffer, ptr);
                ctx.writeAndFlush(fileToClient);
          }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
