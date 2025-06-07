package app.commands;

import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class MinByIdCommand implements Command {
    private final LabWorkService service;

    public MinByIdCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<LabWork> executeCommand(CommandRequest<?> request) {
        LabWork lw = service.minById(request.getLogin());
        if (lw == null) {
            return new CommandResponse<>(false,
                    "Коллекция пуста или нет доступа к элементам.",
                    null);
        }
        return new CommandResponse<>(true,
                "Минимальный по id элемент: " + lw,
                lw);
    }

    @Override
    public String getDescription() {
        return "min_by_id – вывести элемент с минимальным id";
    }
}
