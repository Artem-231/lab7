//package storage.mongo;
//
//import com.mongodb.client.MongoCollection;
//import core.dao.LabWorkDao;
//import core.enums.Color;
//import core.enums.Country;
//import core.enums.Difficulty;
//import core.objects.Coordinates;
//import core.objects.LabWork;
//import core.objects.Person;
//import org.bson.Document;
//import org.bson.conversions.Bson;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static com.mongodb.client.model.Filters.and;
//import static com.mongodb.client.model.Filters.eq;
//
//public class MongoLabWorkDao implements LabWorkDao {
//    private final MongoCollection<Document> col =
//            MongoConfig.getDatabase().getCollection("labworks");
//
//    @Override
//    public Optional<Long> insert(LabWork lw) {
//        Document d = new Document()
//                .append("appId", lw.getId())
//                .append("name", lw.getName())
//                .append("x", lw.getCoordinates().getX())
//                .append("y", lw.getCoordinates().getY())
//                .append("creationDate", lw.getCreationDate().toString())
//                .append("minimalPoint", lw.getMinimalPoint())
//                .append("description", lw.getDescription())
//                .append("difficulty", lw.getDifficulty() == null
//                        ? null
//                        : lw.getDifficulty().name())
//                .append("authorName", lw.getAuthor().getName())
//                .append("authorWeight", lw.getAuthor().getWeight())
//                .append("authorEyeColor", lw.getAuthor().getEyeColor() == null
//                        ? null
//                        : lw.getAuthor().getEyeColor().name())
//                .append("authorHairColor", lw.getAuthor().getHairColor().name())
//                .append("authorNationality", lw.getAuthor().getNationality().name())
//                .append("owner", lw.getOwnerLogin());
//        col.insertOne(d);
//        return Optional.of((long) lw.getId());
//    }
//
//    @Override
//    public boolean update(LabWork lw) {
//        Bson filter = and(
//                eq("appId", lw.getId()),
//                eq("owner", lw.getOwnerLogin())
//        );
//        Document upd = new Document("$set", new Document()
//                .append("name", lw.getName())
//                .append("x", lw.getCoordinates().getX())
//                .append("y", lw.getCoordinates().getY())
//                .append("minimalPoint", lw.getMinimalPoint())
//                .append("description", lw.getDescription())
//                .append("difficulty", lw.getDifficulty() == null
//                        ? null
//                        : lw.getDifficulty().name())
//                .append("authorName", lw.getAuthor().getName())
//                .append("authorWeight", lw.getAuthor().getWeight())
//                .append("authorEyeColor", lw.getAuthor().getEyeColor() == null
//                        ? null
//                        : lw.getAuthor().getEyeColor().name())
//                .append("authorHairColor", lw.getAuthor().getHairColor().name())
//                .append("authorNationality", lw.getAuthor().getNationality().name())
//        );
//        return col.updateOne(filter, upd).getModifiedCount() == 1;
//    }
//
//    @Override
//    public boolean delete(long id, String ownerLogin) {
//        Bson filter = and(
//                eq("appId", id),
//                eq("owner", ownerLogin)
//        );
//        return col.deleteOne(filter).getDeletedCount() == 1;
//    }
//
//    @Override
//    public List<LabWork> fetchAll() {
//        List<LabWork> out = new ArrayList<>();
//        for (Document d : col.find()) {
//            int    id   = d.getInteger("appId");
//            String name = d.getString("name");
//            double x    = d.getDouble("x");
//            long   y    = d.getLong("y");
//            LocalDateTime creationDate =
//                    LocalDateTime.parse(d.getString("creationDate"));
//            float  mp   = d.getDouble("minimalPoint").floatValue();
//            String desc = d.getString("description");
//            String dif  = d.getString("difficulty");
//            Difficulty difficulty = dif == null
//                    ? null
//                    : Difficulty.valueOf(dif);
//
//            Person author = new Person(
//                    d.getString("authorName"),
//                    d.getInteger("authorWeight"),
//                    d.getString("authorEyeColor") == null
//                            ? null
//                            : Color.valueOf(d.getString("authorEyeColor")),
//                    Color.valueOf(d.getString("authorHairColor")),
//                    Country.valueOf(d.getString("authorNationality"))
//            );
//
//            LabWork lw = new LabWork(
//                    id,
//                    name,
//                    new Coordinates(x, y),
//                    creationDate,
//                    mp,
//                    desc,
//                    difficulty,
//                    author
//            );
//            // восстановим owner
//            lw.setOwnerLogin(d.getString("owner"));
//            out.add(lw);
//        }
//        out.sort(null);
//        return out;
//    }
//
//    @Override
//    public Optional<LabWork> findById(long id) {
//        Document d = col.find(eq("appId", id)).first();
//        if (d == null) return Optional.empty();
//        // аналогично fetchAll: преобразуем d → LabWork
//        LabWork lw = /* ... тот же код что и выше ... */;
//        return Optional.of(lw);
//    }
//}
