let fotoBase64  = "";
let stream      = null;
let editarIndex = localStorage.getItem("editarIndex");

window.addEventListener("DOMContentLoaded", () => {

    if (window.location.pathname.includes("/crear")) {
        localStorage.removeItem("editarIndex");
        editarIndex = null;
    }

    if (window.location.pathname.includes("/editar") && editarIndex !== null) {
        let lista = JSON.parse(localStorage.getItem("formularios")) || [];
        let f = lista[editarIndex];
        if (f) {
            nombre.value       = f.nombre;
            sector.value       = f.sector;
            nivelEscolar.value = f.nivelEscolar;
            fotoBase64         = f.fotoBase64;
            if (fotoBase64) {
                preview.src          = fotoBase64;
                preview.style.display = "block";
            }
        }
    }
});

function mostrarModal(titulo, texto) {
    let modalEl = document.getElementById("modalMensaje");
    let modal   = new bootstrap.Modal(modalEl);
    document.getElementById("modalTitulo").textContent = titulo;
    document.getElementById("modalTexto").textContent  = texto;
    modal.show();
}

function activarCamara() {
    if (stream) return;
    preview.style.display = "none";
    navigator.mediaDevices
        .getUserMedia({ video: true })
        .then(s => {
            stream            = s;
            video.srcObject   = s;
            video.style.display = "block";
            video.play();
        })
        .catch(() => {
            mostrarModal("Error", "No se pudo activar la cámara");
        });
}

function desactivarCamara() {
    if (stream) {
        stream.getTracks().forEach(t => t.stop());
        stream = null;
    }
    video.style.display = "none";
}

function tomarFoto() {
    if (!stream) {
        mostrarModal("Error", "Primero activa la cámara");
        return;
    }
    canvas.width  = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext("2d").drawImage(video, 0, 0);
    fotoBase64            = canvas.toDataURL("image/png");
    preview.src           = fotoBase64;
    preview.style.display = "block";
    desactivarCamara();
}

formEncuesta.addEventListener("submit", e => {
    e.preventDefault();

    if (!fotoBase64) {
        mostrarModal("Error", "Debes capturar una foto");
        return;
    }

    if (!nombre.value.trim() || !sector.value.trim()) {
        mostrarModal("Error", "Todos los campos son obligatorios");
        return;
    }

    navigator.geolocation.getCurrentPosition(pos => {

        // Leer el objeto completo guardado en localStorage
        const authUser = JSON.parse(localStorage.getItem("auth_user")) || {};

        let data = {
            nombre:       nombre.value.trim(),
            sector:       sector.value.trim(),
            nivelEscolar: nivelEscolar.value.toUpperCase(),
            usuario: {
                username: authUser.username || ""   // ← corregido, antes era localStorage.getItem("usuario")
            },
            ubicacion: {
                latitud:  pos.coords.latitude,
                longitud: pos.coords.longitude
            },
            fotoBase64,
            estado: "pendiente"
        };

        let lista = JSON.parse(localStorage.getItem("formularios")) || [];

        if (editarIndex !== null && window.location.pathname.includes("/editar")) {
            lista[editarIndex] = data;
            localStorage.removeItem("editarIndex");
            mostrarModal("Éxito", "Formulario actualizado correctamente");
        } else {
            lista.push(data);
            mostrarModal("Éxito", "Formulario guardado correctamente");
        }

        localStorage.setItem("formularios", JSON.stringify(lista));
        formEncuesta.reset();
        preview.style.display = "none";
        fotoBase64 = "";

        if (editarIndex !== null) {
            window.location.href = "/formularios";
        }
    });
});

window.addEventListener("beforeunload", desactivarCamara);