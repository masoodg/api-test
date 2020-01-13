package APITest;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class Booking {

    // Default values for bookings
    private String checkin = "2030-01-12";
    private String checkout = "2030-01-13";
    private boolean depositpaid = true;
    private String firstname = "name";
    private String lastname = "family";
    private String phone = "12345678909";

    @Test
    public void
    getBookings_returns_min_2_results() {

        // create two bookings
        for (int i = 1; i <= 2; i++) {
            int randomID = ThreadLocalRandom.current().nextInt(100, 10000);
            book(checkin, checkout, randomID, depositpaid, firstname, lastname, phone);
        }

        // Call the API and verifies they exists
        given().
                when().
                get("https://automationintesting.online/booking/").
                then().
                assertThat().
                statusCode(200).
                body("bookings.size()", greaterThanOrEqualTo(2));
    }

    @Test
    public void
    postBooking_returns_201() {
        int randomID = ThreadLocalRandom.current().nextInt(100, 10000);
        book(checkin, checkout, randomID, depositpaid, firstname, lastname, phone);
    }

    @Test
    public void
    getBooking_returns_existing_booking() {
        // create a new booking
        int randomID = ThreadLocalRandom.current().nextInt(100, 10000);
        int response = book(checkin, checkout, randomID, depositpaid, firstname, lastname, phone);
        String bookingID = String.valueOf(response);

        // Call the API and assert it exists
        given().
                when().
                get(String.format("https://automationintesting.online/booking/%s", bookingID)).
                then().
                assertThat().
                statusCode(200).
                body("bookingid", equalTo(Integer.valueOf(bookingID))).
                body("firstname", equalTo(firstname)).
                body("lastname", equalTo(lastname)).
                body("depositpaid", equalTo(depositpaid)).
                body("roomid", equalTo(randomID));

    }

    private int book(String checkin, String checkout, int id, boolean deposit, String name, String family, String phone) {

        JSONObject jsonRequest = new JSONObject();
        JSONObject bookingDateJsonObject = new JSONObject();

        bookingDateJsonObject.put("checkin", checkin);
        bookingDateJsonObject.put("checkout", checkout);
        jsonRequest.put("bookingdates", bookingDateJsonObject);
        jsonRequest.put("bookingid", id);
        jsonRequest.put("depositpaid", deposit);
        jsonRequest.put("email", id + "@gmail.de");
        jsonRequest.put("firstname", name);
        jsonRequest.put("lastname", family);
        jsonRequest.put("phone", phone);
        jsonRequest.put("roomid", id);

        ExtractableResponse<Response> response = given().contentType(ContentType.JSON).
                when().body(jsonRequest.toString()).
                post("https://automationintesting.online/booking/").
                then().
                assertThat().
                statusCode(201).
                extract();
        return response.path("bookingid");
    }
}
