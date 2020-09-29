package org.acme;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.DayOfWeek;
import java.util.List;

import org.acme.domain.Shift;
import org.acme.domain.ShiftAssignment;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class ShiftAssignmentResourceTest {

    @Test
    public void getAll() {
        List<ShiftAssignment> shiftAssignmentList = given().when().get("/shiftAssignments").then().statusCode(200)
                .extract().body().jsonPath().getList(".", ShiftAssignment.class);
        assertFalse(shiftAssignmentList.isEmpty());
        ShiftAssignment firstShiftAssignment = shiftAssignmentList.get(0);
        assertEquals("Employee A", firstShiftAssignment.getEmployee().getName());
        assertEquals(DayOfWeek.MONDAY, firstShiftAssignment.getShift().getDayOfWeek());
    }

    @Test
    void addAndRemove() {
        ShiftAssignment shiftAssignment = given().when().contentType(ContentType.JSON).body(new ShiftAssignment(new Shift()))
                .post("/shiftAssignments").then().statusCode(202).extract().as(ShiftAssignment.class);

        given().when().delete("/shiftAssignments/{id}", shiftAssignment.getId()).then().statusCode(200);
    }

}
