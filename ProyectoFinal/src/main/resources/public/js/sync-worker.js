// sync-worker.js
// Coloca este archivo en: src/main/resources/public/js/sync-worker.js

self.onmessage = function (e) {

    const payload = e.data;

    const protocol = self.location.protocol === "https:" ? "wss:" : "ws:";
    const socket = new WebSocket(`${protocol}//${self.location.host}/ws/formularios`);

    const timeout = setTimeout(() => {
        socket.close();
        self.postMessage("ERROR");
    }, 10000);

    socket.onopen = function () {
        // Enviamos el payload completo: { username, formularios }
        socket.send(JSON.stringify(payload));
    };

    socket.onmessage = function (msg) {
        clearTimeout(timeout);
        self.postMessage(msg.data); // "OK" o "ERROR"
        socket.close();
    };

    socket.onerror = function () {
        clearTimeout(timeout);
        self.postMessage("ERROR");
        socket.close();
    };

    socket.onclose = function () {
        // Socket cerrado limpiamente
        console.log("[Worker] Socket cerrado");
    };
};