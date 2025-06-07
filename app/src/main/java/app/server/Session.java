package app.server;

import app.managers.CommandManager;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Session implements Runnable {
    private final Socket client;
    private final CommandManager commandManager;

    public Session(Socket client, CommandManager commandManager) {
        this.client = client;
        this.commandManager = commandManager;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream in  = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream())
        ) {
            CommandRequest<?> request = (CommandRequest<?>) in.readObject();
            String command = request.getCommandName();

            CommandResponse<?> response;

            if ("login".equals(command) || "register".equals(command)) {
                response = commandManager.dispatch(request);
            } else {
                try {
                    Jws<Claims> jws = JwtUtil.parseToken(request.getLogin());
                    String realLogin = jws.getBody().getSubject();
                    @SuppressWarnings("unchecked")
                    List<String> rolesFromToken = (List<String>) jws.getBody().get("roles");
                    Set<String> roles = new HashSet<>(rolesFromToken);

                    CommandRequest<?> authReq = new CommandRequest<>(
                            request.getCommandName(),
                            request.getPayload(),
                            realLogin,
                            roles
                    );
                    response = commandManager.dispatch(authReq);
                } catch (Exception ex) {
                    response = new CommandResponse<>(
                            false,
                            "Неверный или просроченный JWT: " + ex.getMessage(),
                            null
                    );
                }
            }

            out.writeObject(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
