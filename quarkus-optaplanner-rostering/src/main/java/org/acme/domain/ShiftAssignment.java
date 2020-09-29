package org.acme.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@PlanningEntity
@Entity
@Table(name = "assignments")
public class ShiftAssignment extends PanacheEntityBase {

    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Employee employee;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Shift shift;

    public ShiftAssignment() {
        // TODO Auto-generated constructor stub
    }

    public ShiftAssignment(Shift shift) {
        this.shift = shift;
    }

    public ShiftAssignment(Shift shift, Employee employee) {
        this.shift = shift;
        this.employee = employee;
    }

    public Long getId() {
        return this.id;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    @Override
    public String toString() {
        return shift.getDayOfWeek() + " " + shift.getShiftType() + "->> " + employee.getName();
    }
}
