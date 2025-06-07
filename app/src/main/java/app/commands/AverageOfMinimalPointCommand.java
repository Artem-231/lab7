package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;
import app.service.LabWorkService;

public class AverageOfMinimalPointCommand implements Command {
    private final LabWorkService service;

    public AverageOfMinimalPointCommand(LabWorkService service) {
        this.service = service;
    }

    @Override
    public CommandResponse<Double> executeCommand(CommandRequest<?> request) {
        double avg = service.averageOfMinimalPoint(request.getLogin());
        return new CommandResponse<>(true,
                "Среднее minimalPoint среди ваших элементов = " + avg,
                avg);
    }

    @Override
    public String getDescription() {
        return "average_of_minimal_point – среднее значение minimalPoint среди ваших";
    }
}
