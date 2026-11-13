package com.automation.support.evidence;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.regex.Pattern;

/**
 * Records every HTTP exchange in the PDF evidence, with passwords and tokens masked.
 *
 * <p>With {@code attachToAllure = true} it also attaches the masked exchange to Allure. Use that
 * for requests that carry credentials, instead of the raw AllureRestAssured log.
 */
public class EvidenceHttpFilter implements Filter {

    private static final Pattern JSON_SECRET =
            Pattern.compile("(\"(?:password|token)\"\\s*:\\s*\")[^\"]*(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern KEY_VALUE_SECRET =
            Pattern.compile("\\b(password|token)=([^,}&\\s]+)", Pattern.CASE_INSENSITIVE);

    private final boolean attachToAllure;

    public EvidenceHttpFilter(boolean attachToAllure) {
        this.attachToAllure = attachToAllure;
    }

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        Response response = context.next(request, responseSpec);

        String title = "%s %s -> %d".formatted(request.getMethod(), request.getURI(), response.getStatusCode());
        Object requestBody = request.getBody();
        String exchange = """
                Request:  %s %s
                %s
                Response: %s
                %s""".formatted(
                request.getMethod(), request.getURI(),
                requestBody == null ? "(no body)" : mask(String.valueOf(requestBody)),
                response.getStatusLine(),
                mask(response.getBody().asPrettyString()));

        EvidenceRecorder.addText(title, exchange);
        if (attachToAllure) {
            Allure.addAttachment(title + " (secrets masked)", "text/plain", exchange);
        }
        return response;
    }

    static String mask(String text) {
        String masked = JSON_SECRET.matcher(text).replaceAll("$1***$2");
        return KEY_VALUE_SECRET.matcher(masked).replaceAll("$1=***");
    }
}
