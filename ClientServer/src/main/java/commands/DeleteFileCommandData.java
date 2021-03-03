package commands;

import java.io.Serializable;

public class DeleteFileCommandData implements Serializable {
    String fileName;

    public DeleteFileCommandData(String fileName){
        this.fileName=fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
