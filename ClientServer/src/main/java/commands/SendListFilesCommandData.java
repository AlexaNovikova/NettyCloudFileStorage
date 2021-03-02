package commands;

import java.io.Serializable;
import java.util.ArrayList;

public class SendListFilesCommandData implements Serializable {

    ArrayList<String> filesList;

    public SendListFilesCommandData(ArrayList<String> files){
        this.filesList=files;
    }

    public ArrayList<String> getFiles() {
        return filesList;
    }
}
