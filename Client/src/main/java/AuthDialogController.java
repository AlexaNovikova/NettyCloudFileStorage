
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class AuthDialogController implements Initializable {

        @FXML
        public TextField loginField;
        @FXML public PasswordField passField;

        private Network network;
        private CloudApp cloudApp;

        @FXML
        public void checkAuth() {

            String login = loginField.getText();
            String password = passField.getText();
            if (login.isEmpty()|| password.isEmpty()) {
                cloudApp.showErrorMessage("Поля не должны быть пустыми", "Ошибка ввода");
                return;
            }

            String authMessage ="/auth "+login+ " "+ password;
            network.sendCommand(authMessage,cloudApp.getMyCloudController());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(network.authOk){
                cloudApp.showFileMessenger();
            }


        }
        @FXML
        private void registerOpen(){

        }

        public void setNetwork(Network network) {
            this.network = network;
        }

        public PasswordField getPassField() {
            return passField;
        }

        public TextField getLoginField() {
            return loginField;
        }

        public void setCloudApp(CloudApp cloudApp) {
            this.cloudApp = cloudApp;
        }

        // добавлено для тестирования! Временно

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginField.setText("sally");
        passField.setText("user1");
    }
}

