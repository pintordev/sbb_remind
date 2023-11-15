function _searchEnter() {
    if (window.event.keyCode == 13) {
        _search();
    }
}

function _search() {
    location.href = "/question?kw=" + $("#search_box").val();
}