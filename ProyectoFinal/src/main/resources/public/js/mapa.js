var map = L.map('map').setView([19.4517, -70.6970], 8);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);

fetch('/api/mapa')
    .then(res => res.json())
    .then(data => {

        data.forEach(f => {

            if (f.ubicacion && f.ubicacion.latitud != null && f.ubicacion.longitud != null) {

                let img = f.fotoBase64
                    ? `<img src="${f.fotoBase64}" width="100">`
                    : '';

                L.marker([f.ubicacion.latitud, f.ubicacion.longitud])
                    .addTo(map)
                    .bindPopup(`
                        <b>${f.nombre}</b><br>
                        ${f.sector}<br>
                        ${f.nivelEscolar}<br>
                        ${img}
                    `);

            }

        });

    });

    setTimeout(() => {
        map.invalidateSize();
    }, 200);