package org.acme.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import org.acme.domain.ShiftAssignment;
import org.acme.domain.ShiftType;
import org.acme.domain.Employee;
import org.acme.domain.Shift;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {

	@ConfigProperty(name = "roster.demoData", defaultValue = "SMALL")
	DemoData demoData;

	@Transactional
	public void generateDemoData(@Observes StartupEvent startupEvent) {
		if (demoData == DemoData.NONE) {
			return;
		}

		List<ShiftType> shiftTypeList = new ArrayList<>();
		shiftTypeList.add(new ShiftType("D", "Day Shift", LocalTime.of(7, 00), LocalTime.of(14, 30), false));
		shiftTypeList.add(new ShiftType("A", "Afternoon Shift", LocalTime.of(14, 30), LocalTime.of(22, 00), false));
		shiftTypeList.add(new ShiftType("N", "Night Shift", LocalTime.of(22, 00), LocalTime.of(7, 00), true));
		ShiftType.persist(shiftTypeList);

		List<Shift> shiftList = new ArrayList<>();
		shiftList.add(new Shift(DayOfWeek.MONDAY, shiftTypeList.get(0), 5));
		shiftList.add(new Shift(DayOfWeek.MONDAY, shiftTypeList.get(1), 3));
		shiftList.add(new Shift(DayOfWeek.MONDAY, shiftTypeList.get(2), 2));
		shiftList.add(new Shift(DayOfWeek.TUESDAY, shiftTypeList.get(0), 5));
		shiftList.add(new Shift(DayOfWeek.TUESDAY, shiftTypeList.get(1), 3));
		if (demoData == DemoData.LARGE) {
			shiftList.add(new Shift(DayOfWeek.WEDNESDAY, shiftTypeList.get(0), 5));
			shiftList.add(new Shift(DayOfWeek.WEDNESDAY, shiftTypeList.get(1), 3));
			shiftList.add(new Shift(DayOfWeek.THURSDAY, shiftTypeList.get(0), 5));
			shiftList.add(new Shift(DayOfWeek.THURSDAY, shiftTypeList.get(1), 3));
			shiftList.add(new Shift(DayOfWeek.FRIDAY, shiftTypeList.get(0), 5));
			shiftList.add(new Shift(DayOfWeek.FRIDAY, shiftTypeList.get(1), 3));
			shiftList.add(new Shift(DayOfWeek.SATURDAY, shiftTypeList.get(0), 3));
			shiftList.add(new Shift(DayOfWeek.SATURDAY, shiftTypeList.get(1), 3));
			shiftList.add(new Shift(DayOfWeek.SUNDAY, shiftTypeList.get(0), 3));
			shiftList.add(new Shift(DayOfWeek.SUNDAY, shiftTypeList.get(1), 3));
		}
		Shift.persist(shiftList);

		List<Employee> employeeList = new ArrayList<>();
		employeeList.add(new Employee("Employee A"));
		employeeList.add(new Employee("Employee B"));
		employeeList.add(new Employee("Employee C"));
		employeeList.add(new Employee("Employee D"));
		employeeList.add(new Employee("Employee E"));
		employeeList.add(new Employee("Employee F"));
		employeeList.add(new Employee("Employee G"));
		employeeList.add(new Employee("Employee H"));
		employeeList.add(new Employee("Employee I"));
		employeeList.add(new Employee("Employee J"));
		employeeList.add(new Employee("Employee K"));
		employeeList.add(new Employee("Employee L"));
		if (demoData == DemoData.LARGE) {
			employeeList.add(new Employee("Employee M"));
			employeeList.add(new Employee("Employee N"));
			employeeList.add(new Employee("Employee O"));
			employeeList.add(new Employee("Employee P"));
			employeeList.add(new Employee("Employee Q"));
			employeeList.add(new Employee("Employee R"));
			employeeList.add(new Employee("Employee S"));
			employeeList.add(new Employee("Employee T"));
			employeeList.add(new Employee("Employee U"));
			employeeList.add(new Employee("Employee V"));
			employeeList.add(new Employee("Employee W"));
			employeeList.add(new Employee("Employee X"));
			employeeList.add(new Employee("Employee Y"));
			employeeList.add(new Employee("Employee Z"));
		}
		Employee.persist(employeeList);

		List<ShiftAssignment> shiftAssignmentList = new ArrayList<>();
		for (Shift shift : shiftList)
			for (int required = shift.getRequiredEmployees(); required > 0; required--)
				shiftAssignmentList.add(new ShiftAssignment(shift));
		// Demo the first assignment
		ShiftAssignment shiftAssignment = shiftAssignmentList.get(0);
		shiftAssignment.setEmployee(employeeList.get(0));
		shiftAssignment = shiftAssignmentList.get(2);
		shiftAssignment.setEmployee(employeeList.get(1));
		shiftAssignment = shiftAssignmentList.get(5);
		shiftAssignment.setEmployee(employeeList.get(2));

		ShiftAssignment.persist(shiftAssignmentList);
	}

	public enum DemoData {
		NONE, SMALL, LARGE
	}

}