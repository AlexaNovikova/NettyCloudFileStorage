
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


    public class AuthDialogController {

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

      //      String authErrorMessage = network.sendAuthCommand(login, password);
            String authMessage ="/auth "+login+ " "+ password;
            network.sendCommand(authMessage,cloudApp.getMyCloudController());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(network.authOk){
                cloudApp.showFileMessenger();
            } else {
                cloudApp.showErrorMessage(" ","Ошибка авторизации");
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
    }

