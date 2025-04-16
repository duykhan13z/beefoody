document.addEventListener('DOMContentLoaded', () => {
    // Popup functions
    function showPopup(type) {
        console.log('Showing popup:', type);
        closePopup();
        const popup = document.getElementById(`${type}-popup`);
        if (popup) {
            popup.style.display = 'flex';
        } else {
            console.error('Popup not found:', `${type}-popup`);
        }
    }

    function closePopup() {
        console.log('Closing all popups');
        document.querySelectorAll('.popup-overlay').forEach(popup => {
            popup.style.display = 'none';
        });
    }

    function switchPopup(type) {
        console.log('Switching to popup:', type);
        closePopup();
        showPopup(type);
    }

    // Login button
    document.querySelector('.order-btn').addEventListener('click', () => {
        console.log('Login button clicked');
        showPopup('login');
    });

    // Close buttons
    document.querySelectorAll('.popup-close').forEach(button => {
        button.addEventListener('click', closePopup);
    });

    // Popup links
    document.querySelectorAll('.register-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            switchPopup('register');
        });
    });

    document.querySelectorAll('.forgot-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            switchPopup('forgot');
        });
    });

    document.querySelectorAll('.login-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            switchPopup('login');
        });
    });
});