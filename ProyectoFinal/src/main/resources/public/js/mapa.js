var map = L.map('map').setView([19.4517, -70.6970], 8);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);

fetch('/api/formularios')
    .then(res => res.json())
    .then(data => {
        data.forEach(f => {
            L.marker([f.latitud, f.longitud])
                .addTo(map)
                .bindPopup(`
                    <b>${f.nombre}</b><br>
                    ${f.sector}<br>
                    ${f.nivelEscolar}
                `);
        });
    });

.bindPopup(`
    <b>${f.nombre}</b><br>
    <img src="${f.imagen}" width="100"><br>
    ${f.sector}
`)