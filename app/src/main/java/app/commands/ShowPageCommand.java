package app.commands;

import core.objects.LabWork;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

import java.util.List;

public class ShowPageCommand implements Command {
    private final LabWorkService service;
    private static final int PAGE_SIZE = 20;

    public ShowPageCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<List<LabWork>> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        int pageNum;
        // Попытаемся получить номер страницы из payload
        if (raw instanceof Number n) {
            pageNum = n.intValue();
        } else if (raw instanceof String s) {
            try {
                pageNum = Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                return new CommandResponse<>(false,
                        "Неверный формат номера страницы: " + s,
                        null);
            }
        } else {
            return new CommandResponse<>(false,
                    "Для show_page нужен номер страницы",
                    null);
        }

        if (pageNum < 1) {
            return new CommandResponse<>(false,
                    "Номер страницы должен быть >= 1",
                    null);
        }

        List<LabWork> all = service.fetchAll(request.getLogin());
        int total = all.size();
        int start = (pageNum - 1) * PAGE_SIZE;
        if (start >= total) {
            return new CommandResponse<>(false,
                    "Страница " + pageNum + " пуста (всего элементов: " + total + ")",
                    null);
        }
        int end = Math.min(total, start + PAGE_SIZE);
        List<LabWork> page = all.subList(start, end);
        String msg = String.format("Страница %d/%d (элементов %d–%d из %d)",
                pageNum,
                (total + PAGE_SIZE - 1) / PAGE_SIZE,
                start + 1, end,
                total);

        return new CommandResponse<>(true,
                msg,
                page);
    }

    @Override
    public String getDescription() {
        return "show_page <num> – вывести страницу с элементами";
    }
}
