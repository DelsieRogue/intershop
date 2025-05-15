const APP_PATH = "/intershop";

function togglePostForm() {
    const form = document.getElementById('productForm');
    form.style.display = form.style.display === 'none' || form.style.display === '' ? 'block' : 'none';
}

function sendAction(productId, action) {
    sessionStorage.setItem('scrollY', window.scrollY);
    fetch(APP_PATH + `/product/${productId}/updateInCart`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: action
    })
        .then(response => {
            if (response.ok) {
                window.location.href = response.url;
            } else {
                console.error('Ошибка при отправке формы');
            }
        })
        .catch(error => {
            console.error('Сетевая ошибка:', error);
        });

    return false;
}

window.addEventListener('load', () => {
    const scrollY = sessionStorage.getItem('scrollY');
    if (scrollY !== null) {
        window.scrollTo(0, parseInt(scrollY, 10));
        sessionStorage.removeItem('scrollY');
    }
});