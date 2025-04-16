document.querySelectorAll('.nav.side-menu > li > a[data-bs-toggle="collapse"]').forEach(item => {
    item.addEventListener('click', function(event) {
        const target = document.querySelector(this.getAttribute('data-bs-target'));
        const icon = this.querySelector('.fa-chevron-down, .fa-chevron-up'); // Select the chevron icon directly

        // Toggle the collapse (show or hide the dropdown)
        target.classList.toggle('collapse');
        target.classList.toggle('show');

        // Toggle the chevron icon (down/up)
        if (target.classList.contains('show')) {
            icon.classList.remove('fa-chevron-down');
            icon.classList.add('fa-chevron-up');
        } else {
            icon.classList.remove('fa-chevron-up');
            icon.classList.add('fa-chevron-down');
        }
    });
});

