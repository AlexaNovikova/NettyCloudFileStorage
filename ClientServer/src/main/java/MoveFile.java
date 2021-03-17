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
        int ptr;

        while ((ptr = fis.read(buffer)) >= 0) {
            fos.write(buffer, 0, ptr);
        }

        fis.close();
        fos.close();
    }

}
