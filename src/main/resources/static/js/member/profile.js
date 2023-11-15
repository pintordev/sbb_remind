function _question_detail(id) {
    location.href = '/question/' + id;
}

function _question_page(page) {
    $("#qPage").val(page);
    $("#pageForm").submit();
}

function _answer_page(page) {
    $("#aPage").val(page);
    $("#pageForm").submit();
}