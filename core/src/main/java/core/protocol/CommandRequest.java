// core/src/main/java/core/protocol/CommandRequest.java
package core.protocol;

import java.io.Serializable;
import java.util.Set;

public class CommandRequest<T> implements Serializable {
    private final String commandName;
    private final T      payload;
    private final String login;
    private final Set<String> roles;   // ← сюда поместим роли из JWT

    public CommandRequest(String commandName, T payload, String login, Set<String> roles) {
        this.commandName = commandName;
        this.payload     = payload;
        this.login       = login;
        this.roles       = roles;
    }

    // старые конструкторы можно пометить @Deprecated

    public String getCommandName() { return commandName; }
    public T      getPayload()     { return payload; }
    public String getLogin()       { return login; }
    public Set<String> getRoles()  { return roles; }

    public <U> CommandRequest<U> withCommand(String newCommand) {
        return new CommandRequest<>(newCommand, null, login, roles);
    }
    public <U> CommandRequest<U> withPayload(U newPayload) {
        return new CommandRequest<>(commandName, newPayload, login, roles);
    }
}
