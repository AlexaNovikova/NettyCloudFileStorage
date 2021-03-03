package commands;

import java.io.Serializable;
import java.util.ArrayList;

public class SendListFilesCommandData implements Serializable {

    ArrayList<String> filesList;
    String ServerDir;

    public SendListFilesCommandData(ArrayList<String> files, String serverDir){

        this.filesList=files;
        this.ServerDir=serverDir;
    }

    public ArrayList<String> getFiles() {
        return filesList;
    }

    public String getServerDir() {
        return ServerDir;
    }
}
