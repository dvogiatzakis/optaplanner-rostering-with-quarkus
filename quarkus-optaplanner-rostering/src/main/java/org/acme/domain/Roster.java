package org.acme.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

@PlanningSolution
public class Roster {

	@ProblemFactCollectionProperty
	@ValueRangeProvider(id = "employeeRange")
	private List<Employee> employeeList;
	@ProblemFactCollectionProperty
	@ValueRangeProvider(id = "shiftRange")
	private List<Shift> shiftList;

	@PlanningEntityCollectionProperty
	private List<ShiftAssignment> shiftAssignmentList;

	@PlanningScore
	private HardSoftScore score;

	// Ignored by OptaPlanner, used by the UI to display solve or stop solving
	private SolverStatus solverStatus;

	public Roster() {
	}

	public Roster(final List<Shift> shiftList, final List<Employee> employeeList,
			final List<ShiftAssignment> shiftAssignmentList) {
		this.employeeList = employeeList;
		this.shiftList = shiftList;
		this.shiftAssignmentList = shiftAssignmentList;
	}

	public SolverStatus getSolverStatus() {
		return this.solverStatus;
	}

	public void setSolverStatus(SolverStatus solverStatus) {
		this.solverStatus = solverStatus;
	}

	public List<Employee> getEmployeeList() {
		return this.employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public List<Shift> getShiftList() {
		return this.shiftList;
	}

	public void setShiftList(List<Shift> shiftList) {
		this.shiftList = shiftList;
	}

	public List<ShiftAssignment> getShiftAssignmentList() {
		return this.shiftAssignmentList;
	}

	public void setShiftAssignmentList(List<ShiftAssignment> shiftAssignmentList) {
		this.shiftAssignmentList = shiftAssignmentList;
	}

	public HardSoftScore getScore() {
		return this.score;
	}

	public void setScore(HardSoftScore score) {
		this.score = score;
	}

}
