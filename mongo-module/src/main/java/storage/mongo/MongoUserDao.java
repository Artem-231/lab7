//package storage.mongo;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.result.UpdateResult;
//import core.dao.UserDao;
//import core.enums.Role;
//import core.objects.User;
//import org.bson.Document;
//
//import java.util.Optional;
//
//public class MongoUserDao implements UserDao {
//    private final MongoCollection<Document> col =
//            MongoConfig.getDatabase().getCollection("users");
//
//    @Override
//    public Optional<User> findByLogin(String login) {
//        Document d = col.find(Filters.eq("login", login)).first();
//        if (d == null) return Optional.empty();
//        String hash = d.getString("passwordHash");
//        Role   r    = Role.valueOf(d.getString("role"));
//        return Optional.of(new User(login, hash, r));
//    }
//
//    @Override
//    public boolean exists(String login) {
//        return col.countDocuments(Filters.eq("login", login)) > 0;
//    }
//
//    @Override
//    public long count() {
//        return col.countDocuments();
//    }
//
//    @Override
//    public boolean insert(User user) {
//        try {
//            Document d = new Document("login", user.getLogin())
//                    .append("passwordHash", user.getPasswordHash())
//                    .append("role", user.getRole().name());
//            col.insertOne(d);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean checkCredentials(String login, String passwordHash) {
//        return col.countDocuments(
//                Filters.and(
//                        Filters.eq("login", login),
//                        Filters.eq("passwordHash", passwordHash)
//                )
//        ) == 1;
//    }
//
//    @Override
//    public boolean updateRole(String login, Role newRole) {
//        UpdateResult res = col.updateOne(
//                Filters.eq("login", login),
//                new Document("$set", new Document("role", newRole.name()))
//        );
//        return res.getModifiedCount() == 1;
//    }
//
//    @Override
//    public boolean revokeRole(String login, Role roleToRevoke) {
//        // просто даём READER
//        return updateRole(login, Role.READER);
//    }
//}
