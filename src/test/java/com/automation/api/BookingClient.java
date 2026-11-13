package com.automation.api;

import static io.restassured.RestAssured.given;

import com.automation.api.model.Booking;
import com.automation.config.TestConfig;
import com.automation.support.evidence.EvidenceHttpFilter;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * Client for the Restful Booker API. Tests talk to this class, never to raw HTTP calls,
 * so endpoint changes are fixed in one place. Every request/response is attached to Allure.
 */
public class BookingClient {

    private final RequestSpecification spec = baseSpec()
            .addFilter(new AllureRestAssured())
            .addFilter(new EvidenceHttpFilter(false))
            .build();

    /**
     * Auth requests carry credentials: AllureRestAssured would log the password in clear text,
     * so they only get the masked evidence filter (which also attaches the masked copy to Allure).
     */
    private final RequestSpecification authSpec = baseSpec()
            .addFilter(new EvidenceHttpFilter(true))
            .build();

    private static RequestSpecBuilder baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.apiBaseUrl())
                .setContentType(ContentType.JSON)
                // The API answers 418 unless Accept is exactly "application/json"
                // (ContentType.JSON would send a list of JSON-like types).
                .setAccept("application/json");
    }

    public Response createToken(String username, String password) {
        return given(authSpec)
                .body(Map.of("username", username, "password", password))
                .post("/auth");
    }

    /** Authenticates with the configured credentials and returns a token. */
    public String authenticate() {
        return createToken(TestConfig.apiUsername(), TestConfig.apiPassword())
                .then().statusCode(200)
                .extract().path("token");
    }

    public Response create(Booking booking) {
        return given(spec).body(booking).post("/booking");
    }

    public Response get(int bookingId) {
        return given(spec).get("/booking/{id}", bookingId);
    }

    public Response update(int bookingId, Booking booking, String token) {
        return given(spec)
                .cookie("token", token)
                .body(booking)
                .put("/booking/{id}", bookingId);
    }

    public Response delete(int bookingId, String token) {
        return given(spec)
                .cookie("token", token)
                .delete("/booking/{id}", bookingId);
    }
}
