package commands;

import java.io.Serializable;

public class GetFileCommandData implements Serializable {
    String fileName;

    public GetFileCommandData(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
