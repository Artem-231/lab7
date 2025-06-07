// src/main/java/core/protocol/CommandType.java
package core.protocol;

/**
 * Типы всех поддерживаемых команд.
 */
public enum CommandType {
    REGISTER,
    LOGIN,

    HELP,
    INFO,
    SHOW,
    ADD,
    UPDATE,
    REMOVE_BY_ID,
    CLEAR,
    SAVE_SERVER,
    EXECUTE_SCRIPT,
    EXIT,
    HEAD,
    ADD_IF_MAX,
    HISTORY,
    AVERAGE_OF_MINIMAL_POINT,
    MIN_BY_ID,
    COUNT_LESS_THAN_DIFFICULTY,

    // команды управления ролями
    GRANT_ROLE,    // выдать роль USER или EDITOR или READER
    REVOKE_ROLE;   // отозвать роль у пользователя
}
