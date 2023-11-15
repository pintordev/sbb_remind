function _question_page(page) {
    location.href = '/question?page=' + page;
}

function _question_detail(id) {
    location.href = '/question/' + id;
}

function _sort(sort) {
    location.href = "/question?kw=" + $("#searchedKw").val()
                    + "&sort=" + sort;
}