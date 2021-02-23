package commands;

import java.io.Serializable;

public class SendListFilesCommandData implements Serializable {
    String files;

    public SendListFilesCommandData(String files){
        this.files=files;
    }

    public String getFiles() {
        return files;
    }
}
