package core.protocol;

import java.io.Serializable;

public class CommandResponse<T> implements Serializable {
    private final boolean success;
    private final String  message;
    private final T       payload;

    public CommandResponse(boolean success, String message, T payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public boolean isSuccess() { return success; }
    public String  getMessage() { return message; }
    public T       getPayload() { return payload; }
}
