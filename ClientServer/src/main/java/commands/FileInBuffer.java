package commands;
import java.io.Serializable;

public class FileInBuffer implements Serializable {
  private byte [] buffer;
  private int ptr;
  String fileName;


    public FileInBuffer(byte[] buffer, int ptr) {
        this.buffer = buffer;
        this.ptr=ptr;
    }

    public FileInBuffer(byte[] buffer, int ptr, String fileName) {
        this.buffer = buffer;
        this.ptr = ptr;
        this.fileName = fileName;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getPtr() {
        return ptr;
    }

    public String getFileName() {
        return fileName;
    }
}
