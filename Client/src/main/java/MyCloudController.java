import commands.*;
import commands.CommandResultOK;
import commands.SendListFilesCommandData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyCloudController implements Initializable {

    public static Network network;
    public TextField input;
    public TextArea filesOnClient;
    public TextArea filesOnServer;
    public TextField resultOrAnswer;

    public void CommandToNetwork(ActionEvent actionEvent) throws IOException {
        String textFromClient = input.getText();
        network.sendCommand(textFromClient, this);
        input.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            network= Network.getInstance();
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Command message = network.readObject();
                        switch (message.getType()){
                            case AUTH_OK:{
                                CommandResultOK dataAuthOk = (CommandResultOK) message.getData();
                                String login  = dataAuthOk.getLogin();
                                String result =dataAuthOk.getResult();
                                network.setClientNick(login);
                                showText(result);
                                break;
                            }
                            case OK:{
                                CommandResultOK success = (CommandResultOK) message.getData();
                                String result =success.getResult();
                                showText(result);
                                break;
                            }
                            case LS_OK:{
                                SendListFilesCommandData listFilesFromServer = (SendListFilesCommandData) message.getData();
                                String listFiles = listFilesFromServer.getFiles();
                                showFilesOnCloud(listFiles);
                                break;
                            }
                            case SEND:{
                               SendFileCommandData sendFileCommandData = (SendFileCommandData) message.getData();
                               network.getFile(sendFileCommandData, this);
                               break;
                                }
                            case ERROR:{
                                ErrorCommandData errorCommandData = (ErrorCommandData)message.getData();
                                showText(errorCommandData.getError());
                                break;
                            }
                            case UNKNOWN:{
                                UnknownCommandData unknownCommandData =(UnknownCommandData)message.getData();
                                showText(unknownCommandData.getError());
                                break;
                            }
                            case GET:{
                                GetFileCommandData getFile = (GetFileCommandData)message.getData();
                                showText("Файл "+ getFile.getFileName()+ " будет отправлен на сервер.");
                                network.sendFile(getFile.getFileName(),this);
                                break;
                            }
                            }


                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showText(String text){
        Platform.runLater(() -> {
            resultOrAnswer.clear();
            resultOrAnswer.appendText(text + "\n");
        });
    }
    public  void showFilesOnClient (String files) {
        Platform.runLater(() -> {
            filesOnClient.clear();
            filesOnClient.appendText(files + "\n");
        });
    }

    public  void showFilesOnCloud(String files){
        Platform.runLater(() -> {
            filesOnServer.clear();
            filesOnServer.appendText(files+ "\n");
        });
    }
}
