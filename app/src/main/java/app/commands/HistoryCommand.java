package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;

import java.util.Deque;

public class HistoryCommand implements Command {
    private final Deque<String> history;

    public HistoryCommand(Deque<String> history) {
        this.history = history;
    }

    @Override
    public CommandResponse<String> executeCommand(CommandRequest<?> request) {
        if (history.isEmpty()) {
            return new CommandResponse<>(true, "История команд пуста.", "");
        }
        StringBuilder sb = new StringBuilder("Последние команды:\n");
        for (String cmd : history) {
            sb.append(cmd).append("\n");
        }
        return new CommandResponse<>(true, sb.toString(), sb.toString());
    }

    @Override
    public String getDescription() {
        return "history – вывести последние 10 команд";
    }
}
