import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;

import static io.restassured.RestAssured.given;


public class exerciseTest {
    String mainUri = "https://api.thecatapi.com/v1";
    String apiKey = "61dd1797-9ea8-443e-94cf-265fefd9e56f";
    Random rand = new Random();
    int favId = rand.nextInt(99999);

    String getImageId() {
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
                        contentType(ContentType.JSON).extract().response().body();
        String randomId = response.asString();
        String imageId = response.jsonPath().get("id[0]");
        return imageId;
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
        HashMap<String, String> map = new HashMap();
        map.put("image_id", getImageId());
        map.put("sub_id", String.valueOf(favId));
        given().
                contentType("application/json").
                header("x-api-key", apiKey).
                body(map).
        when().
                post(mainUri + "/favourites").
        then().
                statusCode(200);
    }

    @Test
    public void checkFav() {
        given().
                header("x-api-key", apiKey).
        when().
                get(mainUri + "/favourites/" + favId).
        then().
                statusCode(200);
    }
}