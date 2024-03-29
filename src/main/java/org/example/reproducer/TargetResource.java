package org.example.reproducer;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/target")
public class TargetResource {
    @GET
    @Produces(TEXT_PLAIN)
    public String getTarget() {
        return "Target";
    }
}
