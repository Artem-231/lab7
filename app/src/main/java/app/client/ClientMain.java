package app.client;

import app.managers.InputManager;
import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import core.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ClientMain {
    private static final String HOST = "localhost";
    private static final int    PORT = 34567;

    public static void main(String[] args) {
        new ClientMain().start();
    }

    public void start() {
        Scanner sc      = new Scanner(System.in);
        String  login   = null;
        String  password= null;
        String  jwt     = "";
        Set<String> roles = Set.of();
        SocketChannel chan = null;
        ObjectOutputStream oos = null;
        ObjectInputStream  ois = null;

        // 1) REGISTER / LOGIN
        while (true) {
            System.out.print("Введите команду (register/login): ");
            String[] parts = sc.nextLine().trim().split("\\s+");
            if (parts.length != 3 ||
                    !(parts[0].equalsIgnoreCase("register") || parts[0].equalsIgnoreCase("login")))
            {
                System.out.println("Неверно. Формат: register <login> <password> или login <login> <password>");
                continue;
            }
            login    = parts[1];
            password = parts[2];
            try {
                chan = connect();
                oos  = new ObjectOutputStream(chan.socket().getOutputStream());
                ois  = new ObjectInputStream(chan.socket().getInputStream());

                oos.writeObject(new CommandRequest<>(
                        parts[0],
                        new String[]{login,password},
                        "",
                        Set.of()
                ));
                @SuppressWarnings("unchecked")
                CommandResponse<String> authResp = (CommandResponse<String>) ois.readObject();
                System.out.println(authResp.getMessage());
                if (authResp.isSuccess() && authResp.getPayload() != null) {
                    jwt = authResp.getPayload();
                    Jws<Claims> jws = JwtUtil.parseToken(jwt);
                    @SuppressWarnings("unchecked")
                    List<String> rl = (List<String>) jws.getBody().get("roles");
                    roles = new HashSet<>(rl);
                    break;
                } else {
                    chan.close();
                }
            } catch (Exception ex) {
                System.out.println("Ошибка связи, retry через 2 сек: " + ex.getMessage());
                sleep(2000);
            }
        }

        mainLoop:
        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String cmd = line.split("\\s+")[0].toLowerCase();
            Object arg;
            switch (cmd) {
                case "add":
                    arg = InputManager.readLabWork(new Scanner(System.in), login);
                    break;
                case "update":
                    System.out.print("Введите id редактируемого элемента: ");
                    long updId = Long.parseLong(sc.nextLine().trim());
                    LabWork newData = InputManager.readLabWork(new Scanner(System.in), login);
                    arg = new Object[]{updId, newData};
                    break;
                default:
                    String[] p = line.split("\\s+", 2);
                    arg = p.length>1 ? p[1] : null;
            }

            try {
                oos.writeObject(new CommandRequest<>(
                        cmd,
                        arg,
                        jwt,
                        roles
                ));
                CommandResponse<?> resp = (CommandResponse<?>) ois.readObject();
                System.out.println(resp.getMessage());
                if ("exit".equals(cmd)) break;
            } catch (Exception ex) {
                System.out.println("Связь потеряна, ожидаю восстановления...");
                while (true) {
                    sleep(2000);
                    try {
                        chan = connect();
                        oos  = new ObjectOutputStream(chan.socket().getOutputStream());
                        ois  = new ObjectInputStream(chan.socket().getInputStream());
                        oos.writeObject(new CommandRequest<>(
                                "login",
                                new String[]{login, password},
                                "",
                                Set.of()
                        ));
                        @SuppressWarnings("unchecked")
                        CommandResponse<String> reAuth = (CommandResponse<String>) ois.readObject();
                        if (reAuth.isSuccess() && reAuth.getPayload()!=null) {
                            jwt = reAuth.getPayload();
                            Jws<Claims> jws = JwtUtil.parseToken(jwt);
                            @SuppressWarnings("unchecked")
                            List<String> rl = (List<String>) jws.getBody().get("roles");
                            roles = new HashSet<>(rl);
                            System.out.println("Переподключение успешно.");
                            break;
                        } else {
                            System.out.println("Не удалось переподключиться: "+reAuth.getMessage());
                        }
                    } catch (Exception rex) {
                        System.out.println("Сервер ещё не доступен...");
                    }
                }
            }
        }
    }

    private SocketChannel connect() throws Exception {
        SocketChannel ch = SocketChannel.open();
        ch.configureBlocking(false);
        ch.connect(new InetSocketAddress(HOST, PORT));
        while (!ch.finishConnect()) Thread.sleep(50);
        ch.configureBlocking(true);
        return ch;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
