package org.acme.rest;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.domain.Shift;

import io.quarkus.panache.common.Sort;

@Path("/shifts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ShiftResource {

    @GET
    public List<Shift> getAllShifts() {
        return Shift.listAll(Sort.by("dayOfWeek").and("id"));
    }

    @POST
    public Response add(Shift shift) {
        Shift.persist(shift);
        return Response.accepted(shift).build();
    }

    @DELETE
    @Path("{shiftId}")
    public Response delete(@PathParam("shiftId") Long shiftId) {
        Shift shift = Shift.findById(shiftId);
        if (shift == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        shift.delete();
        return Response.status(Response.Status.OK).build();
    }

}

