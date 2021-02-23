package commands;
import java.io.File;
import java.io.Serializable;

public class SendFileCommandData implements Serializable {
     String fileName;
     Long FileSize;

    public SendFileCommandData(String fileName, Long fileSize) {
        this.fileName = fileName;
        FileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return FileSize;
    }

}
