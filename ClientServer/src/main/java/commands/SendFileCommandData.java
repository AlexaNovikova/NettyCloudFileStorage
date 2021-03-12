package commands;
import java.io.File;
import java.io.Serializable;

public class SendFileCommandData implements Serializable {
     String fileName;
     Long FileSize;
     Boolean fromDir;

    public SendFileCommandData(String fileName, Long fileSize, boolean fromDir) {
        this.fileName = fileName;
        FileSize = fileSize;
        this.fromDir=fromDir;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return FileSize;
    }

}
