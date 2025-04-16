function updateRowsPerPage() {
    const rowsPerPage = document.getElementById('rowsPerPage').value;
    const currentPage = 1; // Reset to the first page
    window.location.href = `${paginationPath}?page=${currentPage}&rowsPerPage=${rowsPerPage}`; // Redirect to the first page with the new rowsPerPage value
}

// Navigate to the specific page entered by the user
function goToPage() {
    const page = document.getElementById('pageInput').value;
    const rowsPerPage = document.getElementById('rowsPerPage').value;

    if (page >= 1 && page <= totalPages) {
        window.location.href = `${paginationPath}?page=${page}&rowsPerPage=${rowsPerPage}`;
    } else {
              alert(`Please enter a page number between 1 and ${totalPages}`);
    }
}


// Function to go to the previous page
function goLeftPage() {
    const currentPage = parseInt(document.getElementById('pageInput').value);
    const rowsPerPage = document.getElementById('rowsPerPage').value;

    // Ensure that the previous page is not less than 1
    if (currentPage > 1) {
        const newPage = currentPage - 1;
        window.location.href = `${paginationPath}?page=${newPage}&rowsPerPage=${rowsPerPage}`;
    }
}

// Function to go to the next page
function goRightPage() {
    const currentPage = parseInt(document.getElementById('pageInput').value);
    const rowsPerPage = document.getElementById('rowsPerPage').value;

    // Ensure that the next page does not exceed total pages
    if (currentPage < totalPages) {
        const newPage = currentPage + 1;
        window.location.href = `${paginationPath}?page=${newPage}&rowsPerPage=${rowsPerPage}`;
    }
}

function updatePaginationButtons() {
    // Safely access the page input value
    const pageInputElement = document.getElementById('pageInput');

    // Check if the element exists and has a valid value
    let currentPage = 1;  // Default value if the input doesn't exist or is invalid

    if (pageInputElement && pageInputElement.value) {
        currentPage = parseInt(pageInputElement.value);  // Parse the value as an integer
    }

    const prevPageBtn = document.getElementById('prevPageBtn');
    const nextPageBtn = document.getElementById('nextPageBtn');

    // Check if totalPages is a valid number (in case it's not set or is incorrect)
    if (isNaN(totalPages) || totalPages <= 0) {
        totalPages = 1;  // Reset to 1 if invalid
    }

    // Hide "Previous" button if on the first page and the button exists
    if (prevPageBtn) {
        if (currentPage === 1) {
            prevPageBtn.style.display = 'none';
        } else {
            prevPageBtn.style.display = 'inline';
        }
    }
    // Hide "Next" button if on the last page and the button exists
    if (nextPageBtn) {
        if (currentPage === totalPages) {
            nextPageBtn.style.display = 'none';
        } else {
            nextPageBtn.style.display = 'inline';
        }
    }
}
window.onload = updatePaginationButtons;