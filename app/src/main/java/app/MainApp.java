package app;

import core.utils.UTF8Control;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    private static Stage primaryStage;
    private static Locale locale = Locale.ENGLISH;

    public static void setLocale(Locale l) {
        locale = l;
    }
    public static Locale getLocale() {
        return locale;
    }
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("LabWork Manager");
        showLogin();
    }

    public static void showLogin() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "bundles.messages", locale, new UTF8Control()
            );
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/fxml/Login.fxml"),
                    bundle
            );
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Открывает главное окно после успешного логина/регистрации.
     * @param login     имя пользователя
     * @param jwtToken  JWT-токен
     * @param roles     роли пользователя (из токена)
     */
    public static void showMain(String login, String jwtToken, java.util.Set<String> roles) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "bundles.messages", locale, new UTF8Control()
            );
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/fxml/Main.fxml"),
                    bundle
            );
            Parent root = loader.load();

            MainController ctrl = loader.getController();
            ctrl.setCurrentUser(login, jwtToken, roles);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
