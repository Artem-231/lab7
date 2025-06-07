package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class InfoCommand implements Command {
    private final LabWorkService service;

    public InfoCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<String> executeCommand(CommandRequest<?> request) {
        // метод service.info(login) вернет строку с подробностями
        String info = service.info(request.getLogin());
        return new CommandResponse<>(true, info, info);
    }

    @Override
    public String getDescription() {
        return "info – вывести информацию о коллекции";
    }
}
