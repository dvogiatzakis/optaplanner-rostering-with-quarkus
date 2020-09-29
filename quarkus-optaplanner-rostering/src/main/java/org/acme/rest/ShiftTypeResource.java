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

import org.acme.domain.ShiftType;

import io.quarkus.panache.common.Sort;

@Path("/shift_types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ShiftTypeResource {

    @GET
    public List<ShiftType> getAllShiftTypes() {
        return ShiftType.listAll(Sort.by("startTime")
        		.and("endTime").and("code").and("description").and("id"));
    }

    @POST
    public Response add(ShiftType shiftType) {
        ShiftType.persist(shiftType);
        return Response.accepted(shiftType).build();
    }

    @DELETE
    @Path("{shiftTypeId}")
    public Response delete(@PathParam("shiftTypeId") Long shiftTypeId) {
        ShiftType shiftType = ShiftType.findById(shiftTypeId);
        if (shiftType == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        shiftType.delete();
        return Response.status(Response.Status.OK).build();
    }

}
