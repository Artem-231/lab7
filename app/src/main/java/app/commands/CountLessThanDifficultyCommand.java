package app.commands;

import core.enums.Difficulty;
import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class CountLessThanDifficultyCommand implements Command {
    private final LabWorkService service;

    public CountLessThanDifficultyCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<Long> executeCommand(CommandRequest<?> request) {
        Object raw = request.getPayload();
        if (!(raw instanceof String diffStr)) {
            return new CommandResponse<>(false,
                    "Для count_less_than_difficulty ожидается строка с именем Difficulty",
                    null);
        }

        Difficulty threshold;
        try {
            threshold = Difficulty.valueOf(diffStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandResponse<>(false,
                    "Неверное значение Difficulty: " + diffStr,
                    null);
        }

        long count = service.countLessThanDifficulty(threshold, request.getLogin());
        return new CommandResponse<>(true,
                "Найдено элементов с difficulty < " + threshold + ": " + count,
                count);
    }

    @Override
    public String getDescription() {
        return "count_less_than_difficulty – подсчитать элементы с difficulty меньше заданного";
    }
}
