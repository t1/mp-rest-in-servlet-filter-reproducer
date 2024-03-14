package org.example.reproducer;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.example.reproducer.TargetFilter.TargetApi;

@Path("/")
public class HelloResource {
    @Inject
    @RestClient
    private TargetApi targetApi;

    @GET
    @Produces(TEXT_PLAIN)
    public String hello(@QueryParam("direct") String direct) {
        if (direct != null)
            return "Direct Hello, " + targetApi.getTarget();
        return "Hello!";
    }
}
