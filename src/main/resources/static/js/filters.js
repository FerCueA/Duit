
// ============================================================================
// FILTRA TRABAJOS POR ESTADO
// ============================================================================
function filterByStatus(itemClassName, status) {
    // Obtener todos los elementos que tienen la clase especificada
    var allElements = document.querySelectorAll('.' + itemClassName);

    // Recorrer cada elemento encontrado
    for (var i = 0; i < allElements.length; i++) {
        var currentElement = allElements[i];

        // Obtener el estado del elemento actual
        var elementStatus = currentElement.getAttribute('data-status');

        // Decidir si mostrar o ocultar el elemento
        if (status === 'all') {
            // Si el filtro es 'all', mostrar todos los elementos
            currentElement.style.display = '';
        } else if (elementStatus === status) {
            // Si el estado coincide con el filtro, mostrar el elemento
            currentElement.style.display = '';
        } else {
            // Si el estado no coincide, ocultar el elemento
            currentElement.style.display = 'none';
        }
    }
}


// ============================================================================
// FILTRA ELEMENTOS POR UN ATRIBUTO data-*
// ============================================================================
function filterByDataAttribute(itemClassName, dataAttributeName, value) {
    // Buscar todos los elementos que tienen la clase especificada
    var allItems = document.querySelectorAll('.' + itemClassName);

    // Examinar cada elemento uno por uno
    for (var i = 0; i < allItems.length; i++) {
        var currentItem = allItems[i];

        // Obtener el valor del atributo data-* especificado
        var currentAttributeValue = currentItem.getAttribute(dataAttributeName);

        // Determinar si el elemento debe ser visible
        var shouldShowElement = false;

        if (value === 'all') {
            // Si el filtro es 'all', mostrar todos los elementos
            shouldShowElement = true;
        } else if (currentAttributeValue === value) {
            // Si el valor del atributo coincide con el filtro, mostrar
            shouldShowElement = true;
        }

        // Aplicar la visibilidad al elemento
        if (shouldShowElement) {
            currentItem.style.display = '';
        } else {
            currentItem.style.display = 'none';
        }
    }
}
