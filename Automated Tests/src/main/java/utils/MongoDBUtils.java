// In utils/MongoDBUtils.java
package utils;

import com.mongodb.client.*;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

public class MongoDBUtils {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "studentDB";
    private static final String COLL_NAME = "students";
    private static final String COLL_NAME_1 = "courses";

    /**
     * Checks if a student with the given ID exists in the students collection.
     *
     * @param studentID the ID of the student to look for
     * @return true if found, false otherwise
     */
    public static boolean studentExists(String studentID) {
        // 1. Create a client to connect to MongoDB
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            // 2. Select the database and collection
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> coll = db.getCollection(COLL_NAME);
            // 3. Query for a document where "studentID" equals the given ID
            Document doc = coll.find(eq("studentID", studentID)).first();
            // 4. If we got a document back, the student exists
            return doc != null;
        }
    }
    
    public static boolean courseExists(String courseCode) {
        // 1. Create a client to connect to MongoDB
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            // 2. Select the database and collection
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> coll = db.getCollection(COLL_NAME_1);
            // 3. Query for a document where "studentID" equals the given ID
            Document doc = coll.find(eq("courseCode", courseCode)).first();
            // 4. If we got a document back, the student exists
            return doc != null;
        }
    }
    
}
