function trackOrder() {
    const orderId = document.getElementById('orderIdInput').value;

    if (orderId) {
      // Send AJAX request to check if the order exists
      fetch(`/admin/check-order/${orderId}`)
        .then(response => {
          if (response.ok) {
            // If the order exists, redirect to order-details page
            window.location.href = `/admin/orders/${orderId}`;
          } else if (response.status === 404) {
            // If the order doesn't exist, show "No order found" message
            document.getElementById('noOrderFound').style.display = 'block';
          }
        })
        .catch(error => {
          console.error('Error:', error);
        });
    } else {
      // If input is empty, prompt the user to enter a valid order ID
      alert("Please enter a valid order ID.");
    }
}

// Check if salesData is an array and contains objects with valid properties
let months = [];
let revenue = [];
// Proceed if salesData is valid (non-empty array with valid data)
if (Array.isArray(salesData) && salesData.length > 0) {
    months = salesData.map(data => data.month || 'Unknown Month');  // Default to 'Unknown Month' if month is undefined
    revenue = salesData.map(data => data.revenue || 0);             // Default to 0 if revenue is undefined
}
if (Array.isArray(months) && months.length > 0 && Array.isArray(revenue) && revenue.length > 0) {
    // Create the chart using Chart.js
    const ctx = document.getElementById('salesChart').getContext('2d');
    const salesChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: months,
            datasets:
            [{
                label: 'Revenue',
                data: revenue,
                backgroundColor: 'rgba(255, 159, 64, 0.5)', // Orange for Revenue
                borderColor: 'rgba(255, 159, 64, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}
