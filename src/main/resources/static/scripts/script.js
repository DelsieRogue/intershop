const APP_PATH = "/intershop";

function togglePostForm() {
    const form = document.getElementById('productForm');
    form.style.display = form.style.display === 'none' || form.style.display === '' ? 'block' : 'none';
}