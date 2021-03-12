
import commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;


public class MyFileHandler extends SimpleChannelInboundHandler<Command> {
    private final String SERVER_DIR = "NettyServer"+File.separator+"src"+File.separator+"Files";
    private String serverDir ="NettyServer"+File.separator+"src"+File.separator+"Files";
    private static NettyServer server;
    private  String username;
    private byte[] buffer = new byte[8189];
    private String fileName;
    private Long fileSize;

    public MyFileHandler(NettyServer server, String username) {
        this.server = server;
        this.username= username;
        serverDir =  serverDir +File.separator+ username;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyServer.logger.log(Level.INFO,"Client successfully passed authorization!");
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
        NettyServer.logger.log(Level.INFO,"Client disconnect!");
        server.getClients().remove(ctx);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyServer.logger.log(Level.SEVERE,"Channel throws exception");
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {

        Command commandFromClient = command;
        switch (commandFromClient.getType()){

            case CD:{
                ChangeDirectoryCommandData changeDirectoryCommandData = (ChangeDirectoryCommandData)commandFromClient.getData();
                String directory = changeDirectoryCommandData.getPath();
                NettyServer.logger.log(Level.INFO,"Получена команда CD "+ directory);
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
                NettyServer.logger.log(Level.INFO,"Получена команда LS ");
                ArrayList<String>filesList = createListFiles();
                String serverDirToClient = serverDir.replace(SERVER_DIR+File.separator, "");
                Command commandToClient = new Command().sendListFiles(filesList,serverDirToClient);
                ctx.writeAndFlush(commandToClient);
                break;
            }

            case GET: {
                NettyServer.logger.log(Level.INFO,"Получена команда GET ");
                GetFileCommandData getFileCommandData = (GetFileCommandData) commandFromClient.getData();
                String fileName = getFileCommandData.getFileName();
                File fileToSend = new File(serverDir +File.separator+fileName);
                if (fileToSend.exists()&&fileToSend.isFile()) {
                    Long fileSize = fileToSend.length();
                    Command commandFile = new Command().sendFile(fileName, fileSize,false);
                    ctx.writeAndFlush(commandFile);
                    SendFileFromCloudToClient sendFileFromCloud = new SendFileFromCloudToClient(fileToSend);
                    sendFileFromCloud.createCommandAndSend(ctx);
                    Command result = new Command().success("Файл успешно отправлен клиенту!");
                    ctx.writeAndFlush(result);
                }

                else if(fileToSend.isDirectory()){
                    SendDirWithFilesFromCloud sendDirWithFilesFromCloud= new SendDirWithFilesFromCloud(fileToSend,this);
                    sendDirWithFilesFromCloud.execute(ctx);
                    Command result = new Command().success("Директория с файлами успешно отправлена клиенту!");
                    ctx.writeAndFlush(result);
                }
                else {
                    Command commandToClient = new Command().error("Файла не существует!");
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case SEND: {
                NettyServer.logger.log(Level.INFO,"Получена команда SEND");
                SendFileCommandData sendFileCommandData = (SendFileCommandData) commandFromClient.getData();
                fileName = sendFileCommandData.getFileName();
                fileSize = sendFileCommandData.getFileSize();
                 File newFile = new File(serverDir + File.separator + fileName);
                if (newFile.exists()) {
                    Command commandToClient = new Command().error("Файл с таким именем уже есть на сервере!");
                    ctx.writeAndFlush(commandToClient);
                } else {
                    Command commandToClient = new Command().getFileFromServer(fileName);
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case SEND_DIR:{
                NettyServer.logger.log(Level.INFO,"Получена команда SEND_DIR");
                SendFileCommandData sendFileCommandData = (SendFileCommandData) commandFromClient.getData();
                fileName = sendFileCommandData.getFileName();
                fileSize = sendFileCommandData.getFileSize();
                File newDir= new File(serverDir + File.separator + fileName);
                if (newDir.exists()&&newDir.isDirectory()) {
                    Command commandToClient = new Command().error("Папка с таким именем уже есть на сервере!");
                    ctx.writeAndFlush(commandToClient);
                } else {
                    newDir.mkdir();
                    Command commandToClient = new Command().getDirWithFiles(fileName);
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case FILE: {
                FileInBuffer file = (FileInBuffer) commandFromClient.getData();
                String fileName = file.getFileName();
                File newFile = new File(serverDir + File.separator + fileName);
                int ptr = 0;
                try {
                    try (FileOutputStream fos = new FileOutputStream(newFile, true)) {

                            ptr = file.getPtr();
                            buffer = file.getBuffer();
                            fos.write(buffer, 0, ptr);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case CREATE:{
                NettyServer.logger.log(Level.INFO,"Получена команда CREATE");
                CreateDidCommandData createDidCommandData = (CreateDidCommandData) commandFromClient.getData();
                String dirName = createDidCommandData.getDirName();
                String fullDirName = serverDir+File.separator+dirName;
                File file = new File(fullDirName);
                if(!file.exists()||(file.exists()&&!file.isDirectory()))
                {
                    file.mkdir();
                    Command commandToClient = new Command().success(" Создана директория "+ dirName);
                    ctx.writeAndFlush(commandToClient);
                }
                else {
                    Command commandToClient = new Command().error("Директория с таким именем уже существует на сервере!");
                    ctx.writeAndFlush(commandToClient);
                }
                break;
            }

            case DELETE:{
                NettyServer.logger.log(Level.INFO,"Получена команда DELETE");
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
            case MOVE:{
                NettyServer.logger.log(Level.INFO,"Получена команда MOVE");
              MoveFileCommandData moveFileCommandData = (MoveFileCommandData) commandFromClient.getData();
              String fileName = moveFileCommandData.getOldFile();
              String oldFilePath = serverDir+File.separator+fileName;
              String newPathForFileFromClient = moveFileCommandData.getNewPLaceFile();
              String exactNewPath = SERVER_DIR+File.separator+newPathForFileFromClient;
              File oldFile = new File(oldFilePath);
              if (oldFile.exists()&&!oldFile.isDirectory()){
                  File newPath = new File (exactNewPath);
                  if (newPath.exists()&&newPath.isDirectory()){
                      File newFile = new File(exactNewPath+File.separator+fileName);
                      if(!newFile.exists()) {
                          MoveFile moveFile = new MoveFile(oldFile, newFile);
                          try {
                              moveFile.execute();
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                          oldFile.delete();
                          Command commandToClient = new Command().success("Файл успешно перемещен в новую директорию!");
                          ctx.writeAndFlush(commandToClient);
                      }
                      else if(newFile.exists()){
                              Command commandToClient = new Command().error("Файл с таким именем уже существует в выбранной папке!");
                              ctx.writeAndFlush(commandToClient);
                      }
                  }
                  if(!newPath.exists()||!newPath.isDirectory()){
                      Command commandToClient = new Command().error("Неверно указан путь!");
                      ctx.writeAndFlush(commandToClient);
                  }


              }
              else if(oldFile.isDirectory()){
                  File newDir = new File (exactNewPath+File.separator+fileName);
                  newDir.mkdir();
                  MoveDirectory moveDirectory = new MoveDirectory(oldFile,newDir);
                  String result = moveDirectory.execute();
                  Command commandToClient;
                  if (result==null){
                      commandToClient = new Command().success("Папка с файлами успешно перенесена в новую директорию!");
                  }
                  else {
                      commandToClient = new Command().error(result);
                  }
                  ctx.writeAndFlush(commandToClient);
              }
                break;
            }

            case ERROR:{

               ErrorCommandData errorCommandData = (ErrorCommandData) commandFromClient.getData();
               String error = errorCommandData.getError();
                NettyServer.logger.log(Level.WARNING,"Ошибка "+ error);
               break;
            }

            case END:{
          Command commandEndToClient = new Command().closeConnection();
          NettyServer.logger.log(Level.INFO,"Получена неизвестная команда END");
          ctx.writeAndFlush(commandEndToClient);
          ctx.close();
          break;
            }

            default:{
                NettyServer.logger.log(Level.WARNING,"Получена неизвестная команда");
                break;
            }
        }


    }



    public String getServerDir() {
        return serverDir;
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
