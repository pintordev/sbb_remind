function _answer_create(questionId) {

    $("#questionId").val(questionId);

    $.ajax({
        url: "/answer/create",
        type: "POST",
        data: $("#answerForm").serialize(),
        beforeSend : function() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $(document).ajaxSend(function(e, xhr, options) { xhr.setRequestHeader(header, token); });
        },
        success: function(res) {
            console.log(res.code + ": " + res.message);

            let anchor_tag = "answer_" + res.data.id;
            let content = res.data.content;
            let createDate = res.data.createDate;

            let template = `
                <div>
                    <div id="${anchor_tag}">
                        <span>${content}</span>
                        <span>${createDate}</span>
                    </div>
                </div>
            `;

            $("#answer_last").append(template);
            $("#answerForm > #content").val("");
        },
        error: function(res) {
            console.log(res.responseJSON.code + ": " + res.responseJSON.message);
            alert(res.responseJSON.message);
        }
    })

}

function _answer_page(questionId, page) {
    location.href = '/question/' + questionId + '?aPage=' + page;
}