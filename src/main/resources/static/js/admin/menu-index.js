document.addEventListener('DOMContentLoaded', function () {
    var csrfToken = document.querySelector('meta[name="_csrf"]').content;
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    document.querySelectorAll('.toggle-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var itemId = btn.dataset.itemId;

            fetch('/admin/menu/' + itemId + '/toggle-availability/ajax', {
                method: 'POST',
                headers: { [csrfHeader]: csrfToken }
            })
            .then(function (res) {
                if (!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(function (data) {
                var isAvailable = data.isAvailable;
                btn.textContent = isAvailable ? 'Убрать' : 'Вернуть';
                btn.className = btn.className.replace(/btn-(danger|success)/, isAvailable ? 'btn-danger' : 'btn-success');

                var row = btn.closest('tr');
                var statusCell = row.querySelector('td:nth-child(4)');
                if (statusCell) {
                    if (isAvailable) {
                        statusCell.innerHTML = '<span class="badge badge-green">Доступен</span>';
                    } else {
                        statusCell.innerHTML = '<span class="badge badge-red">Нет в наличии</span>';
                    }
                }

                showMessage(data.message, 'alert-success');
            })
            .catch(function (err) {
                showMessage('Ошибка: ' + err.message, 'alert-danger');
            });
        });
    });

    function showMessage(text, cls) {
        var el = document.getElementById('ajax-message');
        el.textContent = text;
        el.className = 'alert ' + cls;
        el.style.display = 'block';
        setTimeout(function () { el.style.display = 'none'; }, 3000);
    }
});
