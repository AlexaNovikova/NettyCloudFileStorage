
import commands.*;

import java.io.Serializable;
import java.util.ArrayList;

public class Command implements Serializable {

    private CommandType type;
    private Object data;

    public Command authCommand(String login, String password) {
        Command command = new Command();
        command.type = CommandType.AUTH;
        command.data = new AuthCommandData(login,password);
        return command;
    }

    public Command listFilesCommand(){
        Command command = new Command();
        command.type=CommandType.LS;
        command.data= new ListFilesCommandData();
        return command;
    }
    public Command changeDirectory(String directory){
        Command command = new Command();
        command.type=CommandType.CD;
        command.data= new ChangeDirectoryCommandData(directory);
        return command;
    }
    public Command unknownCommand(String error){
        Command command= new Command();
        command.type= CommandType.UNKNOWN;
        command.data= new UnknownCommandData(error);
        return command;
    }

    public Command successAuth (String login){
        Command command = new Command();
        command.type = CommandType.AUTH_OK;
        command.data = new CommandResultOK("Авторизация прошла успешно!", login);
        return command;
    }

    public Command sendListFiles(ArrayList<String> filesList, String serverDir){
        Command command = new Command();
        command.type=CommandType.LS_OK;
        command.data=new SendListFilesCommandData(filesList, serverDir);
        return command;
    }

    public Command getFileFromServer (String fileName){
        Command command = new Command();
        command.type = CommandType.GET;
        command.data = new GetFileCommandData(fileName);
        return command;
    }

    public Command sendFile (String fileName, Long fileSize){
        Command command = new Command();
        command.type = CommandType.SEND;
        command.data = new SendFileCommandData(fileName, fileSize);
        return  command;
    }

    public Command sendDirWithFiles (String fileName, Long fileSize){
        Command command = new Command();
        command.type = CommandType.SEND_DIR;
        command.data=new SendFileCommandData(fileName, fileSize);
        return command;
    }

    public Command getDirWithFiles (String fileName){
        Command command = new Command();
        command.type = CommandType.GET_DIR;
        command.data=new GetFileCommandData(fileName);
        return  command;
    }
    public Command file (byte[] buffer, int ptr){
        Command command= new Command();
        command.type=CommandType.FILE;
        command.data=new FileInBuffer(buffer,ptr);
        return command;
    }
    public Command fileToServer (String filename, byte[] buffer, int ptr){
        Command command= new Command();
        command.type=CommandType.FILE;
        command.data=new FileInBuffer(buffer,ptr, filename);
        return command;
    }
    public Command error (String error){
        Command command = new Command();
        command.type = CommandType.ERROR;
        command.data = new ErrorCommandData(error);
        return command;
    }

    public Command createNewDir(String dirName){
        Command command = new Command();
        command.type= CommandType.CREATE;
        command.data= new CreateDidCommandData(dirName);
        return command;
    }

    public Command success(String message){
        Command command = new Command();
        command.type = CommandType.OK;
        command.data = new CommandResultOK("Операция успешно выполнена!", message);
        return command;
    }

    public Command deleteFile(String fileName){
        Command command= new Command();
        command.type=CommandType.DELETE;
        command.data=new DeleteFileCommandData(fileName);
        return command;
    }

    public Command closeConnection (){
        Command command = new Command();
        command.type=CommandType.END;
        return command;
    }

    public Command moveFile(String oldPlaceFile, String newPlaceFile){
        Command command = new Command();
        command.type= CommandType.MOVE;
        command.data= new MoveFileCommandData(oldPlaceFile, newPlaceFile);
        return  command;
    }
    public CommandType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
