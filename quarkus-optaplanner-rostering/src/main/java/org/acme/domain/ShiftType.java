package org.acme.domain;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "shift_types")
public class ShiftType extends PanacheEntityBase {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@NotNull
	private Long id;
	private String code;
	private String description;
	@NotNull
	private LocalTime startTime;
	@NotNull
	private LocalTime endTime;
	private boolean night;

	public ShiftType() {
		// TODO Auto-generated constructor stub
	}

	public ShiftType(String code, String description, LocalTime startTime, LocalTime endTime, boolean night) {
		super();
		this.code = code;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.night = night;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public boolean isNight() {
		return night;
	}

	public void setNight(boolean night) {
		this.night = night;
	}

	@Override
	public String toString() {
		return code + ": " + description;
	}

}