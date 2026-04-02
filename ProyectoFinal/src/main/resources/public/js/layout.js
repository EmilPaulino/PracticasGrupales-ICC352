
const sidebar   = document.getElementById('sidebar');
const overlay   = document.getElementById('sidebarOverlay');
const toggleBtn = document.getElementById('sidebarToggle');

function toggleSidebar() {
    sidebar?.classList.toggle('sidebar-open');
    overlay?.classList.toggle('show');
}

if (toggleBtn) toggleBtn.addEventListener('click', toggleSidebar);
if (overlay)   overlay.addEventListener('click', toggleSidebar);

document.addEventListener("DOMContentLoaded", () => {

    const loginBtn          = document.getElementById("loginBtnNav");
    const userDropdown      = document.getElementById("userDropdown");
    const navUsername       = document.getElementById("navUsername");
    const navUsernameMobile = document.getElementById("navUsernameMobile");
    const logoutBtn         = document.getElementById("logoutBtn");
    const sidebarToggle     = document.getElementById("sidebarToggle");

    if (!loginBtn) return;

    const token   = localStorage.getItem("jwt_token");
    const usuario = JSON.parse(localStorage.getItem("usuario"));

    if (token && usuario) {

        loginBtn.classList.add("d-none");
        userDropdown.classList.remove("d-none");

        const nombre = usuario.nombre || usuario.username || "Usuario";

        navUsername.textContent = nombre;

        if (navUsernameMobile) navUsernameMobile.textContent = nombre;

        if (sidebarToggle) {
            if (usuario.rol === "ADMIN") {
                sidebarToggle.classList.remove("d-none");
            } else {
                sidebarToggle.classList.add("d-none");
            }
        }

    } else {
        loginBtn.classList.remove("d-none");
        userDropdown.classList.add("d-none");
        if (sidebarToggle) sidebarToggle.classList.add("d-none");

    }

    logoutBtn?.addEventListener("click", () => {
        localStorage.clear();
        window.location.href = "/login";
    });

});