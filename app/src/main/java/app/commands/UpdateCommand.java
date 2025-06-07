package app.commands;

import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class UpdateCommand implements Command {
    private final LabWorkService service;

    public UpdateCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof Object[] arr)
                || arr.length != 2
                || !(arr[0] instanceof Number)
                || !(arr[1] instanceof LabWork)) {
            return new CommandResponse<>(false,
                    "Для update нужен массив [id, LabWork]",
                    null);
        }
        int id       = ((Number) arr[0]).intValue();
        LabWork data = (LabWork) arr[1];

        boolean ok = service.update(id, data, request.getLogin());
        if (ok) {
            return new CommandResponse<>(true,
                    "Элемент с id=" + id + " обновлён.",
                    null);
        } else {
            return new CommandResponse<>(false,
                    "Не удалось обновить элемент (нет прав или нет такого id).",
                    null);
        }
    }

    @Override
    public String getDescription() {
        return "update <id> {element} – обновить свой элемент по id";
    }
}
