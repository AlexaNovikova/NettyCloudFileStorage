import java.io.*;

public class MoveFile {
    FileInputStream fis;
    FileOutputStream fos;
    File oldPlaceFile;
    File newPlaceFile;
    byte[] buffer;

    MoveFile(File oldPlaceFile, File newPlaceFile) throws FileNotFoundException {
        this.newPlaceFile = newPlaceFile;
        this.oldPlaceFile = oldPlaceFile;
        fis = new FileInputStream(oldPlaceFile);
        fos = new FileOutputStream(newPlaceFile);
        buffer = new byte[8189];
    }

    public void execute() throws IOException {
            int ptr = 0;
            long fileSize = oldPlaceFile.length();
            while (fileSize > buffer.length) {
                ptr = fis.read(buffer);
                fileSize -= ptr;
                fos.write(buffer, 0, ptr);
            }
            byte[] bufferLast = new byte[Math.toIntExact(fileSize)];
            ptr = fis.read(bufferLast);
            fos.write(bufferLast, 0, ptr);
            fis.close();
            fos.close();
    }

}
