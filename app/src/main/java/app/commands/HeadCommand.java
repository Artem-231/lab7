package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import core.dao.LabWorkDao;
import core.objects.LabWork;

import java.util.List;

public class HeadCommand implements Command {
    private final LabWorkDao dao;

    public HeadCommand(LabWorkDao dao) {
        this.dao = dao;
    }

    @Override
    public CommandResponse<LabWork> executeCommand(CommandRequest<?> request) {
        List<LabWork> all = dao.fetchAll();
        if (all.isEmpty()) {
            return new CommandResponse<>(false, "Коллекция пуста.", null);
        }
        LabWork first = all.get(0);
        return new CommandResponse<>(true,
                "Первый элемент коллекции: " + first,
                first);
    }

    @Override
    public String getDescription() {
        return "head – вывести первый элемент коллекции";
    }
}
