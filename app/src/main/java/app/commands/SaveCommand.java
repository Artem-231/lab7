package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import core.dao.LabWorkDao;

public class SaveCommand implements Command {
    private final LabWorkDao dao;

    public SaveCommand(LabWorkDao dao) {
        this.dao = dao;
    }

    @Override
    public CommandResponse<Void> executeCommand(CommandRequest<?> request) {
        // Для реляционного хранения save не нужен, коллекция сразу в БД.
        return new CommandResponse<>(true,
                "Коллекция хранится в базе, явное сохранение не требуется.",
                null);
    }

    @Override
    public String getDescription() {
        return "save_server : (только сервер) сохранить коллекцию";
    }
}
