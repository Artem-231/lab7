package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class ClearCommand implements Command {
    private final LabWorkService service;

    public ClearCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        boolean ok = service.clear(request.getLogin());
        if (ok) {
            return new CommandResponse<>(true,
                    "Ваша коллекция очищена.",
                    null);
        } else {
            return new CommandResponse<>(false,
                    "Не удалось очистить коллекцию (возможно, она уже пуста).",
                    null);
        }
    }

    @Override
    public String getDescription() {
        return "clear – очистить свои элементы коллекции";
    }
}
