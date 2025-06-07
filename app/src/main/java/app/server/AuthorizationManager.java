// src/main/java/app/server/AuthorizationManager.java
//package app.server;
//
//import core.protocol.CommandType;
//
//import java.util.EnumMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class AuthorizationManager {
//    private static final Map<CommandType, Set<String>> permissions = new EnumMap<>(CommandType.class);
//
//    static {
//        // чтение для всех ролей
//        Set<String> any = Set.of("ADMIN","EDITOR","READER");
//        permissions.put(CommandType.SHOW, any);
//        permissions.put(CommandType.INFO, any);
//        permissions.put(CommandType.HEAD, any);
//        permissions.put(CommandType.COUNT_LESS_THAN_DIFFICULTY, any);
//        permissions.put(CommandType.AVERAGE_OF_MINIMAL_POINT, any);
//        permissions.put(CommandType.MIN_BY_ID, any);
//        permissions.put(CommandType.EXECUTE_SCRIPT, any);
//
//        // запись — только ADMIN и EDITOR
//        Set<String> write = Set.of("ADMIN","EDITOR");
//        permissions.put(CommandType.ADD, write);
//        permissions.put(CommandType.UPDATE, write);
//        permissions.put(CommandType.REMOVE_BY_ID, write);
//        permissions.put(CommandType.CLEAR, write);
//        permissions.put(CommandType.ADD_IF_MAX, write);
//
//        // роль-специфичные
//        permissions.put(CommandType.GRANT_ROLE, Set.of("ADMIN"));
//        permissions.put(CommandType.REVOKE_ROLE, Set.of("ADMIN"));
//
//        // exit — любой авторизованный
//        permissions.put(CommandType.EXIT, any);
//
//        // register/login выполняются до проверки JWT
//    }
//
//    public static boolean isAllowed(CommandType cmd, List<String> roles) {
//        Set<String> allowed = permissions.get(cmd);
//        return allowed != null && roles.stream().anyMatch(allowed::contains);
//    }
//}
