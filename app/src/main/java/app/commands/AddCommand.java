// src/main/java/app/commands/AddCommand.java
package app.commands;

import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class AddCommand implements Command {
    private final LabWorkService service;

    public AddCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<?> executeCommand(CommandRequest<?> request) {
        Object payload = request.getPayload();
        if (!(payload instanceof LabWork)) {
            return new CommandResponse<>(false,
                    "Для add ожидается объект LabWork",
                    null);
        }

        LabWork lw = (LabWork) payload;
        boolean ok = service.add(lw, request.getLogin());
        return new CommandResponse<>(ok,
                ok ? "Элемент добавлен" : "Не удалось добавить элемент",
                null);
    }

    @Override
    public String getDescription() {
        return "add – добавить новый элемент";
    }
}
