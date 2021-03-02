import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyCloudController implements Initializable {

    public  Network network;
    public TextField input;
    public TextArea filesOnServer;
    public TextField resultOrAnswer;
    public ListView <String>filesClientList;
    public ListView <String> filesCloudList;
    public ImageView sendBtn;
    public ImageView getBtn;
    public ImageView updateBtn;
    private String selectedFile;
    private String selectedFileOnCloud;
    private static final String clientParent = "Client"+File.separator+"src"+File.separator+ "Files";


    public void CommandToNetwork(ActionEvent actionEvent) throws IOException {
        String textFromClient = input.getText();
        network.sendCommand(textFromClient, this);
        input.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filesClientList.setItems(FXCollections.observableArrayList());
        filesClientList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = filesClientList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
               filesClientList.requestFocus();
                if (! cell.isEmpty()) {
                    int index = cell.getIndex();
                        selectionModel.select(index);
                        selectedFile = cell.getItem();
                    if (event.getClickCount()==2){
                        changeDir(selectedFile);
                    }
                    if(event.getButton()== MouseButton.SECONDARY){
                        showSelectAction(selectedFile);
                    }
                    event.consume();
                }
            });

            return cell ;
        });

        filesCloudList.setItems(FXCollections.observableArrayList());
        filesCloudList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = filesCloudList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                filesCloudList.requestFocus();
                if (! cell.isEmpty()) {
                    int index = cell.getIndex();
                    selectionModel.select(index);
                    selectedFileOnCloud = cell.getItem();
                    if (event.getClickCount()==2){
                        try {
                            changeDirOnCloud(selectedFileOnCloud);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(event.getButton()== MouseButton.SECONDARY){
                        showSelectAction(selectedFileOnCloud);
                    }
                    event.consume();
                }
            });

            return cell ;
        });

    }

    private void changeDirOnCloud(String selectedFileOnCloud) throws IOException {
        network.sendCommand("/cd "+selectedFileOnCloud, this);
    }

    private void showSelectAction(String name) {
        Alert selectAction = new Alert(Alert.AlertType.CONFIRMATION);
        selectAction.setTitle("Select.");
        selectAction.setHeaderText("Choose the required action with selected file.");
        ButtonType delete = new ButtonType("Delete");
        ButtonType send = new ButtonType("Send");
        ButtonType move = new ButtonType("Move");
        ButtonType exit = new ButtonType("Back");
        selectAction.getButtonTypes().clear();
        selectAction.getButtonTypes().addAll(delete,send,move,exit);
        Optional<ButtonType> option = selectAction.showAndWait();
        if (option.get()==exit){
            selectAction.close();
        }
        else if (option.get()==delete){
            //             delete(selectedFile);
        }
        else if (option.get()==move){
            //             move(selectedFile);
        }
        else if(option.get()==send) {
            String fileName = name.split(" ")[0];
            File fileToServer = new File(clientParent + File.separator + fileName);
            if (fileToServer.exists() && !fileToServer.isDirectory()) {
                Long fileSize = fileToServer.length();
                Command fileToSend = new Command().sendFile(fileName, fileSize);
                try {
                    network.write(fileToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeDir(String selectedFile) {
        File file = new File(network.getClientDir());
        if (selectedFile.trim().equals("...")) {
            if (network.getClientDir().equals(clientParent)) {
                return;
            }
                File parent = new File(file.getParent());
                 if (parent.exists()) {
                    network.setClientDirDirect(parent.getPath());
                }
            }
        else {
            file = new File(network.getClientDir()+File.separator+ selectedFile.split(" ")[0]);
            if (file.exists() && file.isDirectory()) {
                network.setClientDir(selectedFile.split(" ")[0]);
            }
        }
            ArrayList<String> files = network.createListFiles();
            showFilesOnClient(files);
        }


    public void showText(String text){
        Platform.runLater(() -> {
            resultOrAnswer.clear();
            resultOrAnswer.appendText(text + "\n");
        });
    }
//    public  void showFilesOnClient (String files) {
//        Platform.runLater(() -> {
//            filesOnClient.clear();
//            filesOnClient.appendText(files + "\n");
//            filesClientList.setItems(FXCollections.observableArrayList(files));
//        });
//    }

    public  void showFilesOnClient (List<String> files) {
        Platform.runLater(() -> {
//            filesOnClient.clear();
//            filesOnClient.appendText(files + "\n");
            filesClientList.getItems().clear();
            filesClientList.setItems(FXCollections.observableArrayList(files));
        });
    }
    public  void showFilesOnCloud (List<String> files) {
        Platform.runLater(() -> {
//            filesOnClient.clear();
//            filesOnClient.appendText(files + "\n");
            filesCloudList.getItems().clear();
            filesCloudList.setItems(FXCollections.observableArrayList(files));
        });
    }
    public  void showFilesOnCloud(String files){
        Platform.runLater(() -> {
            filesOnServer.clear();
            filesOnServer.appendText(files+ "\n");
        });
    }


    public void sendCommand(ActionEvent mouseEvent) throws IOException {
        if (selectedFile.isEmpty()){
            return;
        }
        String fileName = selectedFile.split(" ")[0];

        network.sendCommand("/send "+fileName, this);
    }

    public void getCommand(ActionEvent actionEvent) throws IOException {
        if (selectedFile.isEmpty()){
            return;
        }
        String fileName = selectedFile.split(" ")[0];
        network.sendCommand("/get "+ fileName, this);
    }

    public void updateCommand(ActionEvent actionEvent) {
    }

    public void changeStyleOnMouseEnterBtnGet(MouseEvent mouseEvent) {
       setActiveButtonStyle(getBtn);
    }
    public void changeStyleOnMouseExitBtnGet(MouseEvent mouseEvent) {
        getBtn.setEffect(null);
    }
    public void changeStyleOnMouseEnterBtnSend(MouseEvent mouseEvent) {
       setActiveButtonStyle(sendBtn);
    }
    public void changeStyleOnMouseExitBtnSend(MouseEvent mouseEvent) {
        sendBtn.setEffect(null);
    }
    public void changeStyleOnMouseEnterBtnUpdate(MouseEvent mouseEvent) {
       setActiveButtonStyle(updateBtn);
    }
    public void changeStyleOnMouseExitBtnUpdate(MouseEvent mouseEvent) {
        updateBtn.setEffect(null);
    }
    public void setActiveButtonStyle(ImageView imageView){
        Glow glow = new Glow();
        glow.setLevel(0.9);
        imageView.setEffect(glow);
        Lighting lighting= new Lighting();
       imageView.setEffect(lighting);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
