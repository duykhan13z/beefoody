function applyFilter(element) {
    var selectedFilter = element.getAttribute('data-filter');
    console.log("Selected filter:", selectedFilter);
    // Perform filter action based on selectedFilter
}

function selectAvailability(value, label) {
    document.getElementById("availableInput").value = value;
    document.getElementById("availabilityDropdown").innerText = label;
    document.querySelector("form.filterA").submit();
}