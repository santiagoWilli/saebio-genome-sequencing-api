import org.bson.Document;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

import com.mongodb.MongoClient;
import static com.mongodb.client.model.Filters.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import java.io.File;

public class Application_ {
    private static MongoClient mongoClient;
    private static MongoDatabase sparkDatabase;
    private static MongoCollection<Document> filesCollection;

    static {
        port = 4567;
        connectToDatabase();
    }

    private static void connectToDatabase() {
        mongoClient = new MongoClient();
        sparkDatabase = mongoClient.getDatabase("saebio");
        filesCollection = sparkDatabase.getCollection("fs.files");
    }

    @Test
    public void given_noMultipartBody_when_postToSequencies_then_statusCode400() {
        when().
                post("/sequencies").
        then().
                statusCode(400);
    }

    @Test
    public void given_aPairOfFiles_when_postToSequencies_then_statusCode201() {
        given().
                multiPart("pair1", new File("test/sequencies/Kpneu1_191120_R1.fastq.gz")).
                multiPart("pair2", new File("test/sequencies/Kpneu1_191120_R2.fastq.gz")).
        when().
                post("/sequencies").
        then().
                statusCode(201);
    }

    @Test
    public void given_notAPair_when_postToSequencies_then_statusCode400() {
        given().
                multiPart("pair1", new File("test/sequencies/Kpneu1_191120_R1.fastq.gz")).
        when().
                post("/sequencies").
        then().
                statusCode(400);
    }
}
