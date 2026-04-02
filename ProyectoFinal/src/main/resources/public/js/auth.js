(async function () {

    const token = localStorage.getItem("jwt_token");
    const usuarioStr = localStorage.getItem("usuario");

    if (!token) {
        window.location.href = "/login";
        return;
    }

    let usuario;

    try {

        usuario = JSON.parse(usuarioStr);

    } catch (e) {

        localStorage.clear();
        window.location.href = "/login";
        return;

    }

    if (!usuario || usuario.rol !== "ADMIN") {

        window.location.href = "/";
        return;

    }

    try {

        const res = await fetch("/api/usuario/actual", {

            headers: {
                "Authorization": "Bearer " + token
            }

        });

        if (!res.ok) {

            localStorage.clear();
            window.location.href = "/login";

        }

    }
    catch (e) {

        localStorage.clear();
        window.location.href = "/login";

    }

})();