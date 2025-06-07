package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class RemoveByIdCommand implements Command {
    private final LabWorkService service;

    public RemoveByIdCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String idStr)) {
            return new CommandResponse<>(false,
                    "Для remove_by_id нужно указать id в строковом виде",
                    null);
        }
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return new CommandResponse<>(false,
                    "Неверный формат id: " + idStr,
                    null);
        }

        boolean ok = service.remove(id, request.getLogin());
        if (ok) {
            return new CommandResponse<>(true,
                    "Элемент с id=" + id + " удалён.",
                    null);
        } else {
            return new CommandResponse<>(false,
                    "Не удалось удалить элемент (либо нет доступа, либо нет такого id).",
                    null);
        }
    }

    @Override
    public String getDescription() {
        return "remove_by_id <id> – удалить свой элемент по id";
    }
}
