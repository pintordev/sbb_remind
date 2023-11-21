function _searchEnter() {
    if (window.event.keyCode == 13) {
        _search();
    }
}

function _search() {
    location.href = "/question?kw=" + $("#search_box").val();
}

function _reset() {
    $("textarea").each(function() {
        if (!$(this).hasClass("editor-loaded")) {
            let simpleMde = new SimpleMDE({
                element: this,
                placeholder: "내용을 입력해주세요",
                hideIcons: ["guide", "fullscreen", "side-by-side"]
            });
            $(this).addClass("editor-loaded");
        }
    });
}

function _isUndefined(id) {
    if (id === undefined) {
        return "";
    } else {
        return id;
    }
}