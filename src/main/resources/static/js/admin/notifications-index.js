function toggleFields(type) {
    document.getElementById('field-phone').style.display = type === 'BY_PHONE' ? 'block' : 'none';
    document.getElementById('field-token').style.display = type === 'BY_TOKEN' ? 'block' : 'none';
}

function fillPhone(phone) {
    document.querySelector('input[name="sendType"][value="BY_PHONE"]').checked = true;
    toggleFields('BY_PHONE');
    document.querySelector('input[name="phone"]').value = phone;
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('input[name="sendType"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            toggleFields(this.value);
        });
    });
    var checked = document.querySelector('input[name="sendType"]:checked');
    if (checked) toggleFields(checked.value);

    document.addEventListener('click', function (e) {
        if (e.target.dataset.fillPhone) {
            fillPhone(e.target.textContent);
        }
    });
});
