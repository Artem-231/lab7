package app.commands;

import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class AddIfMaxCommand implements Command {
    private final LabWorkService service;

    public AddIfMaxCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof LabWork lw)) {
            return new CommandResponse<>(false,
                    "Для add_if_max ожидается объект LabWork",
                    null);
        }
        boolean ok = service.addIfMax(lw, request.getLogin());
        if (ok) {
            return new CommandResponse<>(true,
                    "Ваш элемент добавлен как максимальный среди ваших.",
                    null);
        } else {
            return new CommandResponse<>(false,
                    "Элемент не является максимальным, не добавлен.",
                    null);
        }
    }

    @Override
    public String getDescription() {
        return "add_if_max – добавить свой элемент, если он больше всех ваших";
    }
}
