
import commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;


public class MyFileHandler extends SimpleChannelInboundHandler<Command> {
    private final String SERVER_DIR = "NettyServer"+File.separator+"src"+File.separator+"Files";
    private String serverDir ="NettyServer"+File.separator+"src"+File.separator+"Files";
    private static NettyServer server;
    private  String username;
    private byte[] buffer = new byte[8189];
    private String fileName;
    private Long fileSize;
    private File newFile;

    public MyFileHandler(NettyServer server, String username) {
        this.server = server;
        this.username= username;
        serverDir =  serverDir +File.separator+ username;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected!");
        File file = new File(serverDir);
        if(!file.exists())
        {
        new File(serverDir).mkdir();
        }
        Command command=new Command().successAuth(username);
        ctx.writeAndFlush(command);
        server.getClients().put(ctx, username);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnect!");
        server.getClients().remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {

        Command commandFromClient = command;
        switch (commandFromClient.getType()){

            case CD:{
                ChangeDirectoryCommandData changeDirectoryCommandData = (ChangeDirectoryCommandData)commandFromClient.getData();
                String directory = changeDirectoryCommandData.getPath();
                System.out.println("Получена команда CD "+ directory);
                File file = new File(serverDir);
                if (directory.trim().equals("...")) {
                    if (serverDir.equals(SERVER_DIR+File.separator+username)) {
                        return;
                    }
                    File parent = new File(file.getParent());
                    if (parent.exists()) {
                        serverDir= parent.getPath();
                    }
                }
                else {
                    file = new File(serverDir+File.separator+directory);
                    if (file.exists() && file.isDirectory()) {
                        serverDir = serverDir+File.separator+directory;
                    }
                }
                ArrayList<String>filesList = createListFiles();
                String serverDirToClient = serverDir.replace(SERVER_DIR+File.separator,"");
                Command commandToClient = new Command().sendListFiles(filesList,serverDirToClient);
                ctx.writeAndFlush(commandToClient);
                break;
            }

            case LS:{
                System.out.println("Получена команда LS");
                ListFilesCommandData listFilesCommandData = (ListFilesCommandData)commandFromClient.getData();
                ArrayList<String>filesList = createListFiles();
                String serverDirToClient = serverDir.replace(SERVER_DIR+File.separator, "");
                Command commandToClient = new Command().sendListFiles(filesList,serverDirToClient);
                ctx.writeAndFlush(commandToClient);
                break;
            }

            case GET: {
                System.out.println("Получена команда Get");
                GetFileCommandData getFileCommandData = (GetFileCommandData) commandFromClient.getData();
                String fileName = getFileCommandData.getFileName();
                File fileToSend = new File(serverDir + "/"+fileName);
                if (fileToSend.exists()&&fileToSend.isFile()) {
                    Long fileSize = fileToSend.length();
                    Command commandFile = new Command().sendFile(fileName, fileSize);
                    ctx.writeAndFlush(commandFile);
                    try (InputStream fis = new FileInputStream(fileToSend)) {
                        int ptr = 0;
                        while(fileSize>buffer.length){
                            ptr=fis.read(buffer);
                            Command fileToClient = new Command().file(buffer,ptr);
                            fileSize-=ptr;
                            ctx.writeAndFlush(fileToClient);
                        }
                        byte[] bufferLast = new byte[Math.toIntExact(fileSize)];
                        ptr=fis.read(bufferLast);
                        Command fileToClient = new Command().file(bufferLast,ptr);
                        ctx.writeAndFlush(fileToClient);
                        }
                }

                else if(fileToSend.isDirectory()){
                    Command commandToClient = new Command().error("Выбрана директория! Выберите файл.");
                    ctx.writeAndFlush(commandToClient);
                }
                else {
                    Command commandToClient = new Command().error("Файла не существует!");
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case SEND: {
                System.out.println("Получена команда Send");
                SendFileCommandData sendFileCommandData = (SendFileCommandData) commandFromClient.getData();
                fileName = sendFileCommandData.getFileName();
                fileSize = sendFileCommandData.getFileSize();
                newFile = new File(serverDir + "/" + fileName);
                if (newFile.exists()) {
                    Command commandToClient = new Command().error("Файл уже есть на сервере! Пересоздать файл - /renew");
                    ctx.writeAndFlush(commandToClient);
                } else {
                    Command commandToClient = new Command().getFileFromServer(fileName);
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case FILE: {
                int ptr = 0;
               // File newFile = new File(serverDir + "/" + fileName);
                try {
                    try (FileOutputStream fos = new FileOutputStream(newFile, true)) {
                        if (fileSize > buffer.length) {
                            FileInBuffer file = (FileInBuffer) commandFromClient.getData();
                            ptr = file.getPtr();
                            buffer = file.getBuffer();
                            fos.write(buffer, 0, ptr);

                        } else {
                            byte[] bufferLast;
                            FileInBuffer file = (FileInBuffer) commandFromClient.getData();
                            ptr = file.getPtr();
                            bufferLast = file.getBuffer();
                            fos.write(bufferLast, 0, ptr);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case CREATE:{
                System.out.println("Получена команда Create");
                CreateDidCommandData createDidCommandData = (CreateDidCommandData) commandFromClient.getData();
                String dirName = createDidCommandData.getDirName();
                String fullDirName = serverDir+File.separator+dirName;
                File file = new File(fullDirName);
                if(!file.exists()||(file.exists()&&!file.isDirectory()))
                {
                    new File(fullDirName).mkdir();
                    Command commandToClient = new Command().success(" Создана директория "+ dirName);
                    ctx.writeAndFlush(commandToClient);
                }
                else {
                    Command commandToClient = new Command().error("Директория с таким именем уже существует на сервере!");
                    ctx.writeAndFlush(commandToClient);
                }
            }

            case DELETE:{
                System.out.println("Получена команда Delete");
                DeleteFileCommandData deleteFileCommandData = (DeleteFileCommandData) commandFromClient.getData();
                String fileName = deleteFileCommandData.getFileName();
                File fileToDelete = new File(serverDir+File.separator+fileName);
                if (fileToDelete.exists()) {
                    if (!fileToDelete.isDirectory()) {
                        fileToDelete.delete();
                    } else if (fileToDelete.isDirectory()) {
                        deleteDirectory(fileToDelete);
                    }
                    Command commandToClient = new Command().success("Файл удален из хранилища!");
                    ctx.writeAndFlush(commandToClient);
                }
                else {
                    Command commandToClient = new Command().error("Файл не существует!");
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case ERROR:{
                System.out.println("Неизвестная команда");
               ErrorCommandData errorCommandData = (ErrorCommandData) commandFromClient.getData();
               String error = errorCommandData.getError();
               System.out.println(error+"\n");
               break;
            }

            default:{
                System.out.println("Получена неизвестная команда!");
                break;
            }
        }


    }

    public ArrayList<String > createListFiles(){
        File dir = new File(serverDir);
        File[] files = dir.listFiles();
        ArrayList<String> filesList = new ArrayList<>();
            filesList.add(" ... ");
        if (files!=null) {
            for (File file : files) {
                StringBuilder sb = new StringBuilder();
                sb.append(file.getName()).append(" ");
                if (file.isFile()) {
                    sb.append("[FILE} | ").append(file.length()).append(" bytes.\n");
                } else {
                    sb.append("[DIR]\n");
                }
                filesList.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        return filesList;
    }
    private void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDirectory(f);
                }
            }
        }
        file.delete();
    }

}
