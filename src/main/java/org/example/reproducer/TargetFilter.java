package org.example.reproducer;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static java.net.http.HttpResponse.BodyHandlers.ofString;

@WebFilter("/*")
public class TargetFilter extends HttpFilter {
    private static final String BASE_URI = "http://localhost:8080/hello";
    private static final Client JEE = ClientBuilder.newClient();
    private static final HttpClient JAVA = HttpClient.newHttpClient();

    @Inject
    @RestClient
    private TargetApi targetApi;

    @RegisterRestClient(baseUri = BASE_URI)
    public interface TargetApi {
        @GET
        @Path("/target")
        @Produces(TEXT_PLAIN)
        String getTarget();
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getPathInfo() == null) { // no recursion on /target
            var queryString = request.getQueryString();
            if (queryString == null) queryString = "mp";
            System.out.println("### " + queryString);
            var target = switch (queryString) {
                case "mp" -> targetApi.getTarget();
                case "jee" -> JEE.target(BASE_URI).path("/target").request().get(String.class);
                case "java" -> java();
                default -> null;
            };
            System.out.println("-> " + target);
        }

        chain.doFilter(request, response);
    }

    private String java() throws IOException {
        try {
            var request = HttpRequest.newBuilder(URI.create(BASE_URI + "/target")).GET().build();
            var response = JAVA.send(request, ofString());
            if (response.statusCode() != 200)
                throw new RuntimeException("got " + response.statusCode() + ": " + response.body());
            return response.body();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
