(function () {
    var csrfToken = document.querySelector('meta[name="_csrf"]').content;
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    var container = document.getElementById('chat-messages');
    var input = document.getElementById('chat-input');
    var sendBtn = document.getElementById('chat-send');
    var orderId = container.dataset.orderId;
    var lastMessageId = 0;

    // Track last known message id from initial load
    container.querySelectorAll('.msg-bubble').forEach(function (el) {
        var id = parseInt(el.dataset.msgId || '0');
        if (id > lastMessageId) lastMessageId = id;
    });
    scrollToBottom();

    function scrollToBottom() {
        container.scrollTop = container.scrollHeight;
    }

    function appendMessage(msg) {
        // Remove "no messages" placeholder if present
        var placeholder = container.querySelector('[style*="Сообщений пока нет"]');
        if (placeholder) placeholder.remove();

        var div = document.createElement('div');
        div.className = 'msg-bubble ' + (msg.senderRole === 'ADMIN' ? 'msg-admin' : 'msg-client');
        div.dataset.msgId = msg.id;
        div.innerHTML =
            '<span class="msg-text">' + escapeHtml(msg.text) + '</span>' +
            '<span class="msg-time">' + msg.sentAt.substring(11, 16) + '</span>';
        container.appendChild(div);
        if (msg.id > lastMessageId) lastMessageId = msg.id;
        scrollToBottom();
    }

    function escapeHtml(s) {
        return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    }

    // Send message
    function sendMessage() {
        var text = input.value.trim();
        if (!text) return;
        sendBtn.disabled = true;

        var params = new URLSearchParams();
        params.append('text', text);

        fetch('/admin/orders/' + orderId + '/chat', {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken,
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
        .then(function (res) {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        })
        .then(function (msg) {
            input.value = '';
            appendMessage(msg);
        })
        .catch(function (err) {
            alert('Ошибка отправки: ' + err.message);
        })
        .finally(function () { sendBtn.disabled = false; });
    }

    sendBtn.addEventListener('click', sendMessage);
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Enter') sendMessage();
    });

    // Poll for new messages every 3 seconds
    setInterval(function () {
        fetch('/admin/orders/' + orderId + '/chat')
        .then(function (res) { return res.json(); })
        .then(function (msgs) {
            msgs.forEach(function (msg) {
                if (msg.id > lastMessageId) appendMessage(msg);
            });
        })
        .catch(function () {});
    }, 3000);
})();
