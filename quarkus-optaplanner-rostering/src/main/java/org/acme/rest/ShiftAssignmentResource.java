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

import org.acme.domain.ShiftAssignment;

import io.quarkus.panache.common.Sort;

@Path("/assignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ShiftAssignmentResource {

    @GET
    public List<ShiftAssignment> getAllShiftAssignments() {
        return ShiftAssignment.listAll(Sort.by("id"));
    }

    @POST
    public Response add(ShiftAssignment shiftAssignment) {
        ShiftAssignment.persist(shiftAssignment);
        return Response.accepted(shiftAssignment).build();
    }

    @DELETE
    @PathParam("{shiftAssignmentId}")
    public Response delete(@PathParam("shiftAssignmentId") Long shiftAssignmentId) {
        ShiftAssignment shiftAssignment = ShiftAssignment.findById(shiftAssignmentId);
        if (shiftAssignment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        shiftAssignment.delete();
        return Response.status(Response.Status.OK).build();
    }

}