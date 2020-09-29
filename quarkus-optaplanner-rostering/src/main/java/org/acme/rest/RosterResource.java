package org.acme.rest;

import java.util.UUID;
//import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.domain.Employee;
import org.acme.domain.Roster;
import org.acme.domain.Shift;
import org.acme.domain.ShiftAssignment;
import org.optaplanner.core.api.score.ScoreManager;
//import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;

import io.quarkus.panache.common.Sort;

@Path("/roster")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RosterResource {

	public static final Long SINGLETON_TIME_TABLE_ID = 1L;

	@Inject
	SolverManager<Roster, Long> solverManager;
	@Inject
	ScoreManager<Roster> scoreManager;

	// To try, open http://localhost:8080/roster
	@GET
	public Roster getRoster() {
		// Get the solver status before loading the solution
		// to avoid the race condition that the solver terminates between them
		SolverStatus solverStatus = getSolverStatus();
		Roster solution = findById(SINGLETON_TIME_TABLE_ID);
		scoreManager.updateScore(solution); // Sets the score
		solution.setSolverStatus(solverStatus);
		return solution;
	}

	@POST
	@Path("/solve")
	public void solve() {
		solverManager.solveAndListen(SINGLETON_TIME_TABLE_ID, this::findById, this::save);
	}

	public SolverStatus getSolverStatus() {
		return solverManager.getSolverStatus(SINGLETON_TIME_TABLE_ID);
	}

	@POST
	@Path("/stopSolving")
	public void stopSolving() {
		solverManager.terminateEarly(SINGLETON_TIME_TABLE_ID);
	}

	@Transactional
	protected Roster findById(Long id) {
		if (!SINGLETON_TIME_TABLE_ID.equals(id)) {
			throw new IllegalStateException("There is no roster with id (" + id + ").");
		}
		// Occurs in a single transaction, so each initialized shiftAssignment
		// references the same shift/employee instance
		// that is contained by the roster's shiftList/employeeList.
		return new Roster(Shift.listAll(Sort.by("dayOfWeek").and("id")), Employee.listAll(Sort.by("name").and("id")),
				ShiftAssignment.listAll(Sort.by("id")));
	}

	@Transactional
	protected void save(Roster roster) {
		for (ShiftAssignment shiftAssignment : roster.getShiftAssignmentList()) {
			// TODO this is awfully naive: optimistic locking causes issues if called by the
			// SolverManager
			ShiftAssignment attachedShiftAssignment = ShiftAssignment.findById(shiftAssignment.getId());
			attachedShiftAssignment.setShift(shiftAssignment.getShift());
			attachedShiftAssignment.setEmployee(shiftAssignment.getEmployee());
		}
	}

	@Inject
	SolverManager<Roster, UUID> solverManager2;

	// @POST
	// @Path("/solve")
	// public Roster solve(Roster problem) {
	// UUID problemId = UUID.randomUUID();
	// // Submit the problem to start solving
	// SolverJob<Roster, UUID> solverJob = solverManager2.solve(problemId, problem);
	// Roster solution;
	// try {
	// // Wait until the solving ends
	// solution = solverJob.getFinalBestSolution();
	// } catch (InterruptedException | ExecutionException e) {
	// throw new IllegalStateException("Solving failed.", e);
	// }
	// return solution;
	// }
}
