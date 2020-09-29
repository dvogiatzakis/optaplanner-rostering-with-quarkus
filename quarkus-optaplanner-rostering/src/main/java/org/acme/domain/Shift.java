package org.acme.domain;

import java.time.DayOfWeek;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "shifts")
public class Shift extends PanacheEntityBase {

    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotNull
    private DayOfWeek dayOfWeek;
    @ManyToOne
    private ShiftType shiftType;
    @NotNull
    private int requiredEmployees;

    public Shift() {
        // TODO Auto-generated constructor stubzz
    }

    public Shift(DayOfWeek dayOfWeek, ShiftType shiftType, int requiredEmployees) {
        this.dayOfWeek = dayOfWeek;
        this.shiftType = shiftType;
        this.requiredEmployees = requiredEmployees;
    }

    public Long getId() {
        return id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getRequiredEmployees() {
        return requiredEmployees;
    }

    public void setRequiredEmployees(int requiredEmployees) {
        this.requiredEmployees = requiredEmployees;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    @Override
    public String toString() {
        return dayOfWeek + "\n " + shiftType.toString();
    }

}
