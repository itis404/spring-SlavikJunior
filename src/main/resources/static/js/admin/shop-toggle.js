(function () {
    var csrfToken = document.querySelector('meta[name="_csrf"]').content;
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    document.getElementById('shop-toggle').addEventListener('click', function () {
        fetch('/admin/shop/toggle', {
            method: 'POST',
            headers: { [csrfHeader]: csrfToken },
        })
        .then(function (r) { return r.json(); })
        .then(function (data) {
            var btn = document.getElementById('shop-toggle');
            btn.textContent = data.isOpen ? 'Магазин открыт' : 'Магазин закрыт';
            btn.classList.toggle('btn-success', data.isOpen);
            btn.classList.toggle('btn-danger', !data.isOpen);
            clearTimeout(window.ordersReloadTimer);
            window.ordersReloadTimer = setTimeout(() => window.location.reload(), 15000);
        });
    });
})();
