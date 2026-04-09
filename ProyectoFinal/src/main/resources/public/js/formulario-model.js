function crearFormularioEjemplo() {

    return {

        nombre: "Pedro",

        sector: "Villa Olga",

        nivelEscolar: "MEDIO",

        ubicacion: {
            latitud: 19.45,
            longitud: -70.69
        },

        fotoBase64: "abc123",

        usuario: {
            username: "admin"
        }

    };

}

let fotoBase64 = "";

function iniciarCamara() {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            document.getElementById("video").srcObject = stream;
        });
}

function tomarFoto() {
    const canvas = document.getElementById("canvas");
    const video = document.getElementById("video");

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    const ctx = canvas.getContext("2d");
    ctx.drawImage(video, 0, 0);

    fotoBase64 = canvas.toDataURL("image/png");
}

document.getElementById("formEncuesta").addEventListener("submit", function (e) {
    e.preventDefault();

    if (!fotoBase64) {
        alert("Debes capturar una foto");
        return;
    }

    navigator.geolocation.getCurrentPosition(pos => {

        const data = {
            nombre: document.getElementById("nombre").value,
            sector: document.getElementById("sector").value,
            nivelEscolar: document.getElementById("nivelEscolar").value.toUpperCase(),

            usuario: {
                username: localStorage.getItem("usuario")
            },

            ubicacion: {
                latitud: pos.coords.latitude,
                longitud: pos.coords.longitude
            },

            fotoBase64: fotoBase64
        };

        guardarLocal(data);
        alert("Guardado localmente");
        this.reset();

    });
});

function guardarLocal(data) {
    let registros = JSON.parse(localStorage.getItem("encuestas")) || [];
    registros.push({
        ...data,
        estado: "pendiente"
    });
    localStorage.setItem("encuestas", JSON.stringify(registros));
}

iniciarCamara();