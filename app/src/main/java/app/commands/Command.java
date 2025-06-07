package app.commands;

import core.protocol.CommandRequest;
import core.protocol.CommandResponse;

public interface Command {
    /**
     * Выполнить команду.
     * @param request запрос с именем команды, payload и login'ом пользователя
     * @return ответ
     */
    CommandResponse<?> executeCommand(CommandRequest<?> request);

    /**
     * Короткое описание (для help / menu).
     */
    String getDescription();
}
