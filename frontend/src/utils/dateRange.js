function formatLocalDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function parseLocalDate(value) {
    const [year, month, day] = value.split("-").map(Number);
    return new Date(year, month - 1, day);
}

export function getEarliestAllowedDate(toDateValue) {
    const toDate = parseLocalDate(toDateValue);
    return formatLocalDate(new Date(
        toDate.getFullYear(),
        toDate.getMonth() - 2,
        1
    ));
}

export function getDefaultDateRange() {
    const toDate = new Date();
    const toDateValue = formatLocalDate(toDate);

    return {
        fromDate: getEarliestAllowedDate(toDateValue),
        toDate: toDateValue
    };
}

export function validateDateRange(fromDate, toDate) {
    if (!fromDate || !toDate) {
        return "Select both start and end dates";
    }
    if (fromDate > toDate) {
        return "Start date must be on or before end date";
    }
    if (fromDate < getEarliestAllowedDate(toDate)) {
        return "Choose a start date within the current month and previous two months";
    }
    return "";
}
