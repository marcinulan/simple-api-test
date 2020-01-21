import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class exerciseTest {
    static String mainUri = "https://api.thecatapi.com/v1";
    static String apiKey = "61dd1797-9ea8-443e-94cf-265fefd9e56f";
    static Random rand = new Random();
    static String favId = String.valueOf(rand.nextInt(99999));
    static HashMap postBody = getPostBody();
    static String favImageId = postBody.get("image_id").toString();
    static Integer favMainId;


    public static HashMap getPostBody() {
        var response = given().
                        param("size", "small").
                        param("limit", "1").
                        param("page", "33").
                        param("format", "json").
                        header("x-api-key", apiKey).
                    when().
                        get(mainUri + "/images/search").
                    then().
                        statusCode(200).
                        contentType(ContentType.JSON);
        String imageId = response.extract().response().body().jsonPath().get("id[0]");
        HashMap<String, String> map = new HashMap();
        map.put("image_id", imageId);
        map.put("sub_id", String.valueOf(favId));
        return map;
    }

    @Test
    public void getFavs() {
        given().
                header("x-api-key", apiKey).
        when().
                get(mainUri + "/favourites").
        then().
                statusCode(200);
    }

    @Test
    public void setFav() {
        var response = given().
                contentType("application/json").
                header("x-api-key", apiKey).
                body(postBody).
        when().
                post(mainUri + "/favourites").
        then().
                statusCode(200).
                contentType(ContentType.JSON);
        favMainId = response.extract().response().body().jsonPath().get("id");
    }

    @Test
    public void checkFav() {
        given().
                header("x-api-key", apiKey).
        when().
                get(mainUri + "/favourites/" + favMainId).
        then().
                statusCode(200).
                body("sub_id", equalTo(favId)).
                body("id", equalTo(favMainId)).
                body("image_id", equalTo(favImageId));

    }
}