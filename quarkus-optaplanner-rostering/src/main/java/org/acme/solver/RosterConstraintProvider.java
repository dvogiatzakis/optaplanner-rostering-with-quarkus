package org.acme.solver;

import java.time.Duration;

import org.acme.domain.ShiftAssignment;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class RosterConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                shiftConflict(constraintFactory), restConflict(constraintFactory)
                // Soft constraints are only implemented in optaplanner-quickstart
        };
    }

    private Constraint shiftConflict(ConstraintFactory constraintFactory) {
        // An employee can work only one shift per timeslot
        // Select a shiftAssignment...
        return constraintFactory.from(ShiftAssignment.class)
                // ... and pair it with another shiftAssignment ...
                .join(ShiftAssignment.class,
                        // ... in the same shift ...
                        Joiners.equal(ShiftAssignment::getShift),
                        // ... with the same employee ...
                        Joiners.equal(ShiftAssignment::getEmployee),
                        // ... and the pair is unique (different id, no reverse pairs)
                        Joiners.lessThan(ShiftAssignment::getId))
                // then penalize each pair with a hard weight.
                .penalize("Shift conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint restConflict(ConstraintFactory constraintFactory) {
        // Consecutive shifts must
        // be assigned to employees with at least 10 hours gap between
        // Select a shiftAssignment...
        return constraintFactory.from(ShiftAssignment.class)
                .join(ShiftAssignment.class, Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.equal((shiftAssignment) -> shiftAssignment.getShift().getDayOfWeek()))
                .filter((shiftAssignment1, shiftAssignment2) -> {
                    Duration between = Duration.between(shiftAssignment1.getShift().getShiftType().getEndTime(),
                            shiftAssignment2.getShift().getShiftType().getStartTime());
                    return !between.isNegative() && between.compareTo(Duration.ofHours(10)) <= 0;
                }).penalize("Rest between shifts not enough <10h", HardSoftScore.ONE_HARD);
    }

}