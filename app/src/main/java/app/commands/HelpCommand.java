package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.managers.CommandManager;

import java.util.Map;

public class HelpCommand implements Command {
    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public CommandResponse<String> executeCommand(CommandRequest<?> request) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Command> entry : manager.getCommands().entrySet()) {
            sb.append(entry.getKey())
                    .append(" : ")
                    .append(entry.getValue().getDescription())
                    .append("\n");
        }
        return new CommandResponse<>(true, sb.toString(), sb.toString());
    }

    @Override
    public String getDescription() {
        return "help : вывести справку по доступным командам";
    }
}
