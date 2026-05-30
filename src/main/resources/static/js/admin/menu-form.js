(function () {
    var photoUrlInput = document.getElementById('photoUrlInput');
    var previewWrap = document.getElementById('photo-preview-wrap');
    var previewImg = document.getElementById('photoPreviewImg');
    var fileInput = document.getElementById('photoFileInput');
    var clearBtn = document.getElementById('clearPhotoBtn');
    var statusEl = document.getElementById('photoUploadStatus');
    var csrfToken = document.querySelector('input[name="_csrf"]').value;

    fileInput.addEventListener('change', function () {
        var file = this.files[0];
        if (!file) return;
        statusEl.textContent = 'Загрузка...';
        statusEl.style.color = '#6b7280';

        var formData = new FormData();
        formData.append('file', file);

        fetch('/api/upload/menu-photo', {
            method: 'POST',
            headers: { 'X-CSRF-TOKEN': csrfToken },
            body: formData,
        })
            .then(function (r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
            .then(function (data) {
                photoUrlInput.value = data.url;
                previewImg.src = data.url;
                previewWrap.style.display = 'block';
                statusEl.textContent = 'Загружено';
                statusEl.style.color = '#22c55e';
                fileInput.value = '';
            })
            .catch(function (err) {
                statusEl.textContent = 'Ошибка загрузки: ' + err.message;
                statusEl.style.color = '#ef4444';
            });
    });

    clearBtn.addEventListener('click', function () {
        photoUrlInput.value = '';
        previewImg.src = '';
        previewWrap.style.display = 'none';
        statusEl.textContent = '';
        fileInput.value = '';
    });
})();
