package app.commands;

import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

import java.util.List;

public class ShowCommand implements Command {
    private final LabWorkService service;

    public ShowCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResponse<List<LabWork>> executeCommand(CommandRequest<?> request) {
        // Передаём логин в сервис, чтобы он вернул только доступные пользователю элементы
        List<LabWork> all = service.fetchAll(request.getLogin());
        return new CommandResponse<>(true,
                "Список элементов",
                all);
    }

    @Override
    public String getDescription() {
        return "show – вывести все элементы коллекции";
    }
}
