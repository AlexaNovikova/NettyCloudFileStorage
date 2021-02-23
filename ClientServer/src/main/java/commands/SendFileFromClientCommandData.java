package commands;

import java.io.Serializable;

public class SendFileFromClientCommandData implements Serializable {
    private String fileName;
    private Long fileSize;
    private FileInBuffer fileInBuffer;

    public SendFileFromClientCommandData(String fileName, Long fileSize, FileInBuffer fileInBuffer) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileInBuffer = fileInBuffer;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public FileInBuffer getFileInBuffer() {
        return fileInBuffer;
    }
}
