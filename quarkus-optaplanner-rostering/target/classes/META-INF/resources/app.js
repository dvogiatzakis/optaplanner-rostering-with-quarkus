var autoRefreshCount = 0;
var autoRefreshIntervalId = null;
var shiftTypeList = new Array();
var shiftList = new Array();
var employeeList = new Array();

function refreshRoster() {
    $.getJSON("/roster", function (roster) {
        refreshSolvingButtons(roster.solverStatus != null && roster.solverStatus !== "NOT_SOLVING");
        $("#score").text("Score: " + (roster.score == null ? "?" : roster.score));



        const rosterByEmployee = $("#rosterByEmployee");
        rosterByEmployee.children().remove();
        const unassignedShifts = $("#unassignedShifts");
        unassignedShifts.children().remove();

        const theadByEmployee = $("<thead>").appendTo(rosterByEmployee);
        // create header row
        const headerRowByEmployee = $("<tr>").appendTo(theadByEmployee);
        headerRowByEmployee.append($("<th>Employee</th>"));
        $.each(roster.shiftList, (index, shift) => {
            headerRowByEmployee.append($(`<th class="align-middle"/>`)
                .append($("<span/>").text(`
                    ${shift.dayOfWeek.charAt(0) + shift.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(shift.shiftType.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(shift.shiftType.endTime, "HH:mm:ss").format("HH:mm")}
                	`)
                    .append($(`<button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1"/>`)
                        .append($(`<small class="fas fa-trash" />`)).click(() => deleteShift(shift)))));
        });

        const tbodyByEmployee = $("<tbody>").appendTo(rosterByEmployee);
        // create body rows
        $.each(roster.employeeList, (index, employee) => {
            const rowByEmployee = $("<tr>").appendTo(tbodyByEmployee);
            rowByEmployee
                .append($("<th/>")
                    .append($("<span/>").text(employee.name))
                    .append($(`<button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1"/>`)
                        .append($(`<small class="fas fa-trash"/>`)).click(() => deleteEmployee(employee))));


            $.each(roster.shiftList, (index, shift) => {
                rowByEmployee.append($("<td/>").prop("id", `employee${employee.id}shift${shift.id}`));
            });
        });

        $.each(roster.shiftAssignmentList, (index, shiftAssignment) => {
            const color = pickColor(shiftAssignment.id);
            const shiftAssignmentElementWithoutDelete = $(`<div class="card shiftAssignment" style="background-color: ${color}"/>`)
                .append($(`<div class="card-body p-2"/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(shiftAssignment.shift.shiftType.code))
                    .append($(`<p class="card-text ml-2 mb-1"/>`))
                    .append($(`<small class="ml-2 mt-1 card-text text-muted align-bottom float-right"/>`).text(`id:${shiftAssignment.id}`)));
            const shiftAssignmentElement = shiftAssignmentElementWithoutDelete.clone();
            shiftAssignmentElement.find(".card-body").prepend(
                $(`<button type="button" class="ml-2 btn btn-light btn-sm p-1 float-right"/>`)
                .append($(`<small class="fas fa-trash"/>`)).click(() => deleteShiftAssignment(shiftAssignment))
            );
            if (shiftAssignment.employee == null) {
                unassignedShifts.append(shiftAssignmentElement);
            } else {
                $(`#employee${shiftAssignment.employee.id}shift${shiftAssignment.shift.id}`)
                    .append(shiftAssignmentElement);
            }
        });
    });
}

function convertToId(str) {
    // Base64 encoding without padding to avoid XSS
    return btoa(str).replace(/=/g, "");
}

function solve() {
    $.post("/roster/solve", function () {
        refreshSolvingButtons(true);
        autoRefreshCount = 16;
        if (autoRefreshIntervalId == null) {
            autoRefreshIntervalId = setInterval(autoRefresh, 2000);
        }
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Start solving failed.", xhr);
    });
}

function refreshSolvingButtons(solving) {
    if (solving) {
        $("#solveButton").hide();
        $("#stopSolvingButton").show();
    } else {
        $("#solveButton").show();
        $("#stopSolvingButton").hide();
    }
}

function autoRefresh() {
    refreshRoster();
    autoRefreshCount--;
    if (autoRefreshCount <= 0) {
        clearInterval(autoRefreshIntervalId);
        autoRefreshIntervalId = null;
    }
}

function stopSolving() {
    $.post("/roster/stopSolving", function () {
        refreshSolvingButtons(false);
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Stop solving failed.", xhr);
    });
}

function addShiftAssignment() {
    let shiftID = $("#shiftAssignment_shiftList").val();
    console.log(shiftID);
    let shiftObj = null;
    shiftList.forEach(element => {
        console.log(element.dayOfWeek);
        if (element.id == shiftID)
            shiftObj = element;
    });
    console.log(shiftObj);
    let employeeName = $('#shiftAssignment_employeeList').val().trim();
    let employeeObj = null;
    if (employeeName != null) {
        employeeList.forEach(element => {
            if (element.name == employeeName)
                employeeObj = element;
        });
    }
    console.log(employeeObj);
    $.post("/assignments", JSON.stringify({
        "shift": shiftObj,
        "employee": employeeObj
    }), function () {
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Adding shift Assignment (" + shiftAssignment.id + ") failed.", xhr);
    });
    $('#shiftAssignmentDialog').modal('toggle');
}

function deleteShiftAssignment(shiftAssignment) {
    $.delete("/assignments/" + shiftAssignment.id, function () {
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Deleting shift Assignment (" + shiftAssignment.shift.id + ") failed.", xhr);
    });
}

function addShift() {
    let code = $("#shift_shiftTypeList").val().substring(0, 1);
    let shiftTypeObj = null;
    shiftTypeList.forEach(element => {
        if (element.code == code)
            shiftTypeObj = element;
    });
    $.post("/shifts", JSON.stringify({
        "dayOfWeek": $("#shift_dayOfWeek").val().trim().toUpperCase(),
        "shiftType": shiftTypeObj,
        "requiredEmployees": $("#shift_required").val().trim()
    }), function () {
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Adding shift failed.", xhr);
    });
    $('#shiftDialog').modal('toggle');
}

function deleteShift(shift) {
    console.log(shift.id);
    $.delete("/shifts/" + shift.id, function () {
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Deleting shift (" + shift.name + ") failed.", xhr);
    });
}

function addEmployee() {
    var name = $("#employee_name").val().trim();
    $.post("/employees", JSON.stringify({
        "name": name
    }), function () {
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Adding employee (" + name + ") failed.", xhr);
    });
    $("#employeeDialog").modal('toggle');
}

function deleteEmployee(employee) {
    $.delete("/employees/" + employee.id, function () {
        refreshRoster();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Deleting employee (" + employee.name + ") failed.", xhr);
    });
}

function showError(title, xhr) {
    const serverErrorMessage = !xhr.responseJSON ? `${xhr.status}: ${xhr.statusText}` : xhr.responseJSON.message;
    console.error(title + "\n" + serverErrorMessage);
    const notification = $(`<div class="toast" role="alert" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 30rem"/>`)
        .append($(`<div class="toast-header bg-danger">
                            <strong class="mr-auto text-dark">Error</strong>
                            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`))
        .append($(`<div class="toast-body"/>`)
            .append($(`<p/>`).text(title))
            .append($(`<pre/>`)
                .append($(`<code/>`).text(serverErrorMessage))
            )
        );
    $("#notificationPanel").append(notification);
    notification.toast({
        delay: 30000
    });
    notification.toast('show');
}

$(document).ready(function () {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });
    // Extend jQuery to support $.put() and $.delete()
    jQuery.each(["put", "delete"], function (i, method) {
        jQuery[method] = function (url, data, callback, type) {
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }
            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });

    // Register Event Listeners
    $("#refreshButton").click(function () {
        refreshRoster();
    });
    $("#solveButton").click(function () {
        solve();
    });
    $("#stopSolvingButton").click(function () {
        stopSolving();
    });
    $("#addShiftAssignmentSubmitButton").click(function () {
        addShiftAssignment();
    });
    $("#addShiftSubmitButton").click(function () {
        addShift();
    });
    $("#addEmployeeSubmitButton").click(function () {
        addEmployee();
    });
    $("#addShiftButton").one("click", function () {
        $.getJSON("/shift_types", function (shift_types) {
            $.each(shift_types, (index, shiftType) => {
                shiftTypeList.push(shiftType);
                $("#shift_shiftTypeList").append($(`<option>`)
                    .text(`${shiftType.code} : ${shiftType.description} -->
                    ${moment(shiftType.startTime, "HH:mm:ss").format("HH:mm")}-
                    ${moment(shiftType.endTime, "HH:mm:ss").format("HH:mm")}`))
            });
        });
    });
    $("#addShiftAssignmentButton").one("click", function () {
        $.getJSON("/shifts", function (shifts) {
            $.each(shifts, (index, shift) => {
                console.log(shift);
                shiftList.push(shift);
                console.log(shiftList);
                $("#shiftAssignment_shiftList").append($(`<option value=${shift.id}>`)
                    .text(`${shift.dayOfWeek} : 
                    ${moment(shift.shiftType.startTime, "HH:mm:ss").format("HH:mm")}-
                    ${moment(shift.shiftType.endTime, "HH:mm:ss").format("HH:mm")}`))
            });
            $("#shiftAssignment_shiftList").prop("selectedIndex", 0);
        });
        $.getJSON("/employees", function (employees) {
            $.each(employees, (index, employee) => {
                console.log(employee);
                employeeList.push(employee);
                console.log(employeeList);
                $("#shiftAssignment_employeeList").append($(`<option>`)
                    .text(`${employee.name}`))
            });
            $("#shiftAssignment_employeeList").prop("selectedIndex", 0);
        });
    });
    refreshRoster();
});

//#region Color Factory
// ****************************************************************************
// TangoColorFactory
// ****************************************************************************

const SEQUENCE_1 = [0x8AE234, 0xFCE94F, 0x729FCF, 0xE9B96E, 0xAD7FA8];
const SEQUENCE_2 = [0x73D216, 0xEDD400, 0x3465A4, 0xC17D11, 0x75507B];

var colorMap = new Map;
var nextColorCount = 0;

function pickColor(object) {
    let color = colorMap[object];
    if (color !== undefined) {
        return color;
    }
    color = nextColor();
    colorMap[object] = color;
    return color;
}

function nextColor() {
    let color;
    let colorIndex = nextColorCount % SEQUENCE_1.length;
    let shadeIndex = Math.floor(nextColorCount / SEQUENCE_1.length);
    if (shadeIndex === 0) {
        color = SEQUENCE_1[colorIndex];
    } else if (shadeIndex === 1) {
        color = SEQUENCE_2[colorIndex];
    } else {
        shadeIndex -= 3;
        let floorColor = SEQUENCE_2[colorIndex];
        let ceilColor = SEQUENCE_1[colorIndex];
        let base = Math.floor((shadeIndex / 2) + 1);
        let divisor = 2;
        while (base >= divisor) {
            divisor *= 2;
        }
        base = (base * 2) - divisor + 1;
        let shadePercentage = base / divisor;
        color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
    }
    nextColorCount++;
    return "#" + color.toString(16);
}

function buildPercentageColor(floorColor, ceilColor, shadePercentage) {
    let red = (floorColor & 0xFF0000) + Math.floor(shadePercentage * ((ceilColor & 0xFF0000) - (floorColor & 0xFF0000))) & 0xFF0000;
    let green = (floorColor & 0x00FF00) + Math.floor(shadePercentage * ((ceilColor & 0x00FF00) - (floorColor & 0x00FF00))) & 0x00FF00;
    let blue = (floorColor & 0x0000FF) + Math.floor(shadePercentage * ((ceilColor & 0x0000FF) - (floorColor & 0x0000FF))) & 0x0000FF;
    return red | green | blue;
}
//#endregion