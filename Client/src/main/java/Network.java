import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import commands.*;
import java.io.*;
import java.net.Socket;

public class Network {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private static final int BUFFER_SIZE = 8189;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    private static String clientDid = "Client/src/Files/";
    private final Socket socket;
    public static Network instance;
    private static byte[] buffer;
    private static String clientNick;

    public static Network getInstance() throws IOException {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    private Network() throws IOException {
        socket = new Socket(HOST, PORT);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
        buffer = new byte[BUFFER_SIZE];
    }

    public void sendCommand(String textFromClient, MyCloudController cloudController) throws IOException {

        String[] msg = textFromClient.split(" ");
        String commandType = msg[0];
        String data;
        if (msg.length > 1) {
            data = textFromClient.split(" ", 2)[1];
        } else {
            data = "";
        }
        Command commandFromClient;
        switch (commandType) {
            case "/auth": {
                if (data.split(" ").length < 2) {
                    cloudController.showText("Не верно введена комманда - укажите /auth логин пароль");
                } else {
                    String login = data.split(" ", 2)[0];
                    String password = data.split(" ", 2)[1];
                    commandFromClient = new Command().authCommand(login, password);
                    os.writeObject(commandFromClient);
                }
                break;
            }
            case "/ls": {
                commandFromClient = new Command().listFilesCommand();
                os.writeObject(commandFromClient);
                File dir = new File(clientDid);
                StringBuilder sb = new StringBuilder(clientNick).append(" files ->  \n");
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        sb.append(file.getName()).append(" ");
                        if (file.isFile()) {
                            sb.append("[FILE} | ").append(file.length()).append(" bytes.\n");
                        } else {
                            sb.append("[DIR]\n");
                        }
                    }
                    cloudController.filesOnClient.clear();
                    cloudController.filesOnClient.appendText(sb.toString());
                }
                break;
            }
            case "/cd": {
                String directory = data;
                if (data.equals("")) {
                    cloudController.showText("Не верно введена комманда. Укажите путь (/cd директория)");
                } else {
                    commandFromClient = new Command().changeDirectory(directory);
                    os.writeObject(commandFromClient);
                }
                break;
            }

            case "/get": {
                if (data.equals("")) {
                    cloudController.showText("Не верно введена комманда. Укажите имя файла");
                } else {
                    String fileName = data;
                    commandFromClient = new Command().getFileFromServer(fileName);
                    os.writeObject(commandFromClient);
                }
                break;
            }

            case "/send": {
                if (data.equals("")) {
                    cloudController.showText("Не верно введена комманда. Укажите имя файла");
                } else {
                    String fileName = data;
                    File fileToServer = new File(clientDid + "/" + fileName);
                    if (!fileToServer.exists()) {
                        cloudController.showText("Файл не существует!");
                    } else {
                        Long fileSize = fileToServer.length();
                        Command fileToSend = new Command().sendFile(fileName, fileSize);
                        os.writeObject(fileToSend);

                    }
                }
                break;
            }

            default:
                cloudController.showText("Неизвестная команда. Повторите ввод. Для справки - Help/About.");
                break;
        }

        os.flush();
    }

    public void write(String message) throws IOException {
        os.writeUTF(message);
        os.flush();
    }

    public void setClientNick(String clientNick) {
        this.clientNick = clientNick;
    }

    public void getFile(SendFileCommandData sendFileCommandData, MyCloudController cloudController) throws IOException {
        int ptr = 0;
        Long fileSize = sendFileCommandData.getFileSize();
        String fileName = sendFileCommandData.getFileName();
        File newFile = new File(clientDid + fileName);
        try {
            try (FileOutputStream fos = new FileOutputStream(newFile, false)) {
                if (fileSize > buffer.length) {
                    while (fileSize > ptr) {
                        Command message = readObject();
                        FileInBuffer fileFromServer = (FileInBuffer) message.getData();
                        ptr = fileFromServer.getPtr();
                        buffer = fileFromServer.getBuffer();
                        fos.write(buffer, 0, ptr);
                        fileSize -= ptr;
                    }
                }
                byte[] bufferLast;
                while (fileSize > 0) {
                    Command message = readObject();
                    FileInBuffer fileFromServer = (FileInBuffer) message.getData();
                    ptr = fileFromServer.getPtr();
                    bufferLast = fileFromServer.getBuffer();
                    fos.write(bufferLast, 0, ptr);
                    fileSize -= ptr;
                }
            }
            cloudController.showText("Файл успешно получен с сервера!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public Command readObject() throws IOException, ClassNotFoundException {
        return (Command) is.readObject();
    }

    public void close() throws IOException {
        is.close();
        os.close();
        socket.close();
    }

    public void sendFile(String fileName, MyCloudController cloudController) {
        File fileToServer = new File(clientDid + "/" + fileName);
        Long fileSize = fileToServer.length();
        try (InputStream fis = new FileInputStream(fileToServer)) {
            int ptr = 0;
            while (fileSize > buffer.length) {
                ptr = fis.read(buffer);
                Command file = new Command().file(buffer, ptr);
                fileSize -= ptr;
                os.writeObject(file);
                os.flush();
            }
            byte[] bufferLast = new byte[Math.toIntExact(fileSize)];
            ptr = fis.read(bufferLast);
            Command file = new Command().file(bufferLast, ptr);
            os.writeObject(file);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudController.showText("Файл успешно отправлен на сервер!");
    }
}

