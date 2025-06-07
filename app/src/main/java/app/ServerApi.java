package app;

import core.objects.LabWork;
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

public class ServerApi {
    private final String host;
    private final int    port;

    // Храним сам токен и разобранные из него роли
    private String jwtToken = "";
    private Set<String> roles = new HashSet<>();

    public ServerApi() {
        this("localhost", 34567);
    }

    public ServerApi(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Возвращает текущий JWT-токен */
    public String getToken() {
        return jwtToken;
    }

    /** Возвращает роли, извлечённые из последнего установленного токена */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Устанавливает токен и сразу же парсит claim "roles" из него.
     */
    public void setToken(String jwtToken) {
        this.jwtToken = jwtToken;
        try {
            Jws<Claims> parsed = JwtUtil.parseToken(jwtToken);
            @SuppressWarnings("unchecked")
            List<String> list = parsed.getBody().get("roles", List.class);
            if (list != null) {
                this.roles = new HashSet<>(list);
            } else {
                this.roles.clear();
            }
        } catch (Exception e) {
            // В случае ошибки разбора токена: тоже очищаем роли,
            // чтобы не оставались старые данные.
            this.roles.clear();
        }
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles == null ? new HashSet<>() : new HashSet<>(roles);
    }

    public CommandResponse<String> login(String user, String pass) {
        return send("login", new String[]{user, pass});
    }

    public CommandResponse<String> register(String user, String pass) {
        return send("register", new String[]{user, pass});
    }

    @SuppressWarnings("unchecked")
    private <T> CommandResponse<T> send(String cmd, Object payload) {
        try (var sock = new Socket(host, port);
             var oos = new ObjectOutputStream(sock.getOutputStream());
             var ois = new ObjectInputStream(sock.getInputStream()))
        {
            // В CommandRequest передаём: имя команды, объект-пейлоад, сам токен и роли
            oos.writeObject(new CommandRequest<>(cmd, payload, jwtToken, roles));
            return (CommandResponse<T>) ois.readObject();
        } catch (Exception e) {
            return new CommandResponse<>(
                    false,
                    "Communication error: " + e.getMessage(),
                    null
            );
        }
    }

    /** Запрашивает у сервера список LabWork (команда "show") */
    @SuppressWarnings("unchecked")
    public List<LabWork> fetchAll() {
        var resp = send("show", null);
        if (!resp.isSuccess() || resp.getPayload() == null) {
            return List.of();
        }
        return (List<LabWork>) resp.getPayload();
    }

    public boolean add(LabWork lw) {
        return send("add", lw).isSuccess();
    }

    public boolean update(int id, LabWork lw) {
        return send("update", new Object[]{id, lw}).isSuccess();
    }

    public boolean remove(long id) {
        return send("remove_by_id", Long.toString(id)).isSuccess();
    }

    public boolean clear() {
        return send("clear", null).isSuccess();
    }

    public boolean addIfMax(LabWork lw) {
        return send("add_if_max", lw).isSuccess();
    }

    public long countLessThanDifficulty(String diff) {
        var resp = send("count_less_than_difficulty", diff);
        if (!resp.isSuccess() || resp.getPayload() == null) {
            return 0L;
        }
        return ((Number)resp.getPayload()).longValue();
    }

    public double averageOfMinimalPoint() {
        var resp = send("average_of_minimal_point", null);
        if (!resp.isSuccess() || resp.getPayload() == null) {
            return 0.0;
        }
        return ((Number)resp.getPayload()).doubleValue();
    }

    public String info() {
        return send("info", null).getMessage();
    }

    public LabWork minById() {
        var resp = send("min_by_id", null);
        return resp.isSuccess() ? (LabWork)resp.getPayload() : null;
    }

    public String executeScript(String filename) {
        return send("execute_script", filename).getMessage();
    }
}
