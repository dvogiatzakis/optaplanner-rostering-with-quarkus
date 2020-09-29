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

import org.acme.domain.Employee;

import io.quarkus.panache.common.Sort;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class EmployeeResource {

    @GET
    public List<Employee> getAllEmployees() {
        return Employee.listAll(Sort.by("name").and("id"));
    }

    
    @POST
    public Response add(Employee employee) {
        Employee.persist(employee);
        return Response.accepted(employee).build();
    }

    @DELETE
    @Path("{employeeId}")
    public Response delete(@PathParam("employeeId") Long employeeId) {
        Employee employee = Employee.findById(employeeId);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        employee.delete();
        return Response.status(Response.Status.OK).build();
    }

}
