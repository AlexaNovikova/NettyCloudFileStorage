import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;


public class CloudApp extends Application {

    public Stage primaryStage;
    private Stage authStage;
    private Stage registerStage;
    private Network network;
    private MyCloudController myCloudController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        network = Network.getInstance();

        if (!network.connect()) {
            showErrorMessage("","Ошибка подключения к серверу");
            return;
        }

        openAuthDialog(primaryStage);
        createFileMessenger(primaryStage);

    }

    private void createFileMessenger(Stage primaryStage) throws IOException {
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(CloudApp.class.getResource("views/Cloud.fxml"));
        Parent root = mainLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("CloudApp");
        primaryStage.setResizable(false);
        Network network = Network.getInstance();
        myCloudController = mainLoader.getController();
        myCloudController.setNetwork(network);
        network.start(myCloudController);
        primaryStage.setOnCloseRequest(event -> {network.close();});
    }


    private void openAuthDialog(Stage primaryStage) throws IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(CloudApp.class.getResource("/views/auth.fxml"));
        Parent page = authLoader.load();
        authStage = new Stage();
        authStage.setTitle("Авторизация");
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        authStage.setScene(scene);
        authStage.show();
        AuthDialogController authDialogController = authLoader.getController();
        authDialogController.setNetwork(network);
        authDialogController.setCloudApp(this);

    }

    public void showFileMessenger() {
        authStage.close();
        primaryStage.show();
        primaryStage.setTitle(network.getClientNick());
        network.sendCommand("/ls",myCloudController);
        myCloudController.clientPath.setText(network.getClientDir());
        myCloudController.serverPath.setText(network.getServerDir());
    }

    public void showErrorMessage(String message, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Проблемы с соединением");
        alert.setHeaderText(errorMessage);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public MyCloudController getMyCloudController() {
        return myCloudController;
    }
}
