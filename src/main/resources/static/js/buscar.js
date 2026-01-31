/**
 * INICIALIZAR FILTROS EN PÁGINA DE BÚSQUEDA DE OFERTAS
 */
document.addEventListener('DOMContentLoaded', function () {
    // PASO 1: Obtener los selects
    var categorySelect = document.getElementById('categoryFilter');
    var postalcodeSelect = document.getElementById('postalcodeFilter');
    var items = document.querySelectorAll('.oferta-item');
    var categories = new Set();
    var postalcodes = new Set();

    // PASO 2: Recopilar todas las categorías y códigos postales únicos
    items.forEach(function (item) {
        var category = item.getAttribute('data-category') || 'Sin categoría';
        var postalcode = item.getAttribute('data-postalcode') || 'Sin código';
        item.setAttribute('data-category', category);
        item.setAttribute('data-postalcode', postalcode);
        categories.add(category);
        postalcodes.add(postalcode);
    });

    // PASO 3: Llenar el select de categorías
    if (categorySelect) {
        Array.from(categories).sort().forEach(function (category) {
            var option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categorySelect.appendChild(option);
        });
    }

    // PASO 4: Llenar el select de códigos postales
    if (postalcodeSelect) {
        Array.from(postalcodes).sort().forEach(function (postalcode) {
            var option = document.createElement('option');
            option.value = postalcode;
            option.textContent = postalcode;
            postalcodeSelect.appendChild(option);
        });
    }
});
