

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MoveDirectory {
    File oldDirPath;
    File newDirPath;

    public MoveDirectory(File oldDirPath, File newDirPath) {
        this.oldDirPath = oldDirPath;
        this.newDirPath = newDirPath;
    }

    public String execute() {
        if (oldDirPath.equals(newDirPath)){
            return "Папка уже находится в указанной директории!";
        }
        File[] contents = oldDirPath.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    try {
                        File oldFile = new File(oldDirPath + File.separator + f.getName());
                        File newFile = new File(newDirPath + File.separator + f.getName());
                        if(oldFile.isFile())
                        {
                        MoveFile moveFile = new MoveFile(oldFile, newFile);
                        moveFile.execute();
                        oldFile.delete();
                        }
                        if (oldFile.isDirectory()){
                            newFile.mkdir();
                            MoveDirectory moveDirectory = new MoveDirectory(oldFile,newFile);
                            moveDirectory.execute();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        oldDirPath.delete();
        return null;
    }
}
