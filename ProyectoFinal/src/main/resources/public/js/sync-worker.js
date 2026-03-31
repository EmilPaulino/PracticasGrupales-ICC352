let socket;

self.onmessage = function () {
    console.log("Iniciando sincronización...");
    if (!socket) {
        socket = new WebSocket("ws://localhost:7000/sync");
        socket.onopen = () => {
            console.log("Worker conectado");
            sincronizar();
        };
    }
};


function sincronizar() {
    let formularios = JSON.parse(localStorage.getItem("formularios")) || [];
    formularios.forEach(f => {
        socket.send(JSON.stringify(f));
    });
}