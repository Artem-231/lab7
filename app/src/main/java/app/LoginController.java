package app;

import core.protocol.CommandResponse;
import core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class LoginController {
    @FXML private TextField       usernameField;
    @FXML private PasswordField   passwordField;
    @FXML private Button          loginButton;
    @FXML private Button          registerButton;
    @FXML private ComboBox<String> langCombo;
    @FXML private Label           messageLabel;

    private ServerApi api;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        // Локаль берём из MainApp.getLocale()
        bundle = ResourceBundle.getBundle(
                "bundles.messages", MainApp.getLocale(), new core.utils.UTF8Control()
        );

        api = new ServerApi();

        // настройка выбора языка
        langCombo.getItems().setAll("en", "ru", "mk", "pl");
        langCombo.setValue(localeToCode(MainApp.getLocale()));
        langCombo.setOnAction(evt -> {
            String code = langCombo.getValue();
            MainApp.setLocale(codeToLocale(code));
            MainApp.showLogin();
        });

        loginButton.setOnAction(e -> handleAuth("login"));
        registerButton.setOnAction(e -> handleAuth("register"));
    }

    private void handleAuth(String cmd) {
        String u = usernameField.getText().trim();
        String p = passwordField.getText().trim();

        if (u.isEmpty() || p.isEmpty()) {
            messageLabel.setText(bundle.getString("login.failure"));
            return;
        }

        CommandResponse<String> resp =
                cmd.equals("login") ? api.login(u, p)
                        : api.register(u, p);

        if (resp.isSuccess() && resp.getPayload() != null) {
            try {
                String jwt = resp.getPayload();
                Jws<Claims> jws = JwtUtil.parseToken(jwt);
                @SuppressWarnings("unchecked")
                List<String> rl = (List<String>) jws.getBody().get("roles");
                Set<String> roles = new HashSet<>(rl);

                api.setToken(jwt);
                api.setRoles(roles);

                MainApp.showMain(u, jwt, roles);
            } catch (Exception ex) {
                messageLabel.setText(bundle.getString("error.token_parse"));
            }
        } else {
            messageLabel.setText(resp.getMessage());
        }
    }

    private String localeToCode(Locale l) {
        switch (l.getLanguage()) {
            case "ru": return "ru";
            case "mk": return "mk";
            case "pl": return "pl";
            default:   return "en";
        }
    }
    private Locale codeToLocale(String code) {
        switch (code) {
            case "ru": return new Locale("ru");
            case "mk": return new Locale("mk");
            case "pl": return new Locale("pl");
            default:   return Locale.ENGLISH;
        }
    }
}
