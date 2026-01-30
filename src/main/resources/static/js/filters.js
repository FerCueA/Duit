/**
 * FILTRAR ELEMENTOS POR ESTADO
 */
function filterByStatus(itemClassName, status) {

    var todosLosElementos = document.querySelectorAll('.' + itemClassName);


    todosLosElementos.forEach(function (elemento) {
        var estadoDelElemento = elemento.getAttribute('data-status');

        if (status === 'all' || estadoDelElemento === status) {
            elemento.style.display = '';
        } else {
            elemento.style.display = 'none';
        }
    });
}

/**
 * FILTRAR ELEMENTOS POR UN ATRIBUTO data-*
 * 
 * Ejemplo:
 *   filterByDataAttribute('oferta-item', 'data-category', 'Limpieza');
 *   filterByDataAttribute('oferta-item', 'data-category', 'all');
 */
function filterByDataAttribute(itemClassName, dataAttributeName, value) {
    var items = document.querySelectorAll('.' + itemClassName);

    items.forEach(function (item) {
        var attrValue = item.getAttribute(dataAttributeName);

        if (value === 'all' || attrValue === value) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}
