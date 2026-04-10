const sidebar   = document.getElementById('sidebar');
const overlay   = document.getElementById('sidebarOverlay');
const toggleBtn = document.getElementById('sidebarToggle');

function toggleSidebar() {
    sidebar?.classList.toggle('sidebar-open');
    overlay?.classList.toggle('show');
}

if (toggleBtn) toggleBtn.addEventListener('click', toggleSidebar);
if (overlay)   overlay.addEventListener('click', toggleSidebar);