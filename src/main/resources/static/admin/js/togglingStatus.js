// Add event listeners for clicking on the toggle icons
document.querySelectorAll('.fa-toggle-on, .fa-toggle-off').forEach(function(icon) {
    icon.addEventListener('click', function() {
        // Update the icon's class based on the current state
        if (isOn) {
            icon.classList.remove('fa-toggle-on');
            icon.classList.add('fa-toggle-off');
            icon.classList.remove('text-success');
            icon.classList.add('text-danger');
        } else {
            icon.classList.remove('fa-toggle-off');
            icon.classList.add('fa-toggle-on');
            icon.classList.remove('text-danger');
            icon.classList.add('text-success');
        }
        console.log('Status toggled!');
    });
});