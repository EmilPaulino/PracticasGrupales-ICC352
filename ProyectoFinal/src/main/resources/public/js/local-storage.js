// Guarda formulario en localStorage
function guardarFormularioLocal(formulario) {
    let lista = JSON.parse(localStorage.getItem("formularios")) || [];
    lista.push(formulario);
    localStorage.setItem("formularios", JSON.stringify(lista));
    console.log("Formulario guardado local");
}


// Obtiene formularios pendientes
function obtenerFormulariosPendientes() {
    return JSON.parse(localStorage.getItem("formularios")) || [];
}


// Limpia formularios sincronizados
function limpiarFormularios() {
    localStorage.removeItem("formularios");
}