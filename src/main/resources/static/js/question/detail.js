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

            console.log(res);

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

function _question_delete(questionId) {
    if (confirm("정말 삭제하시겠습니까?")) {
        location.href = "/question/delete/" + questionId;
    }
}

function _answer_delete(answerId) {
    if (confirm("정말 삭제하시겠습니까?")) {
        location.href = "/answer/delete/" + answerId;
    }
}

function _like(target, targetId) {
    $.ajax({
        url: "/" + target + "/like/" + targetId,
        type: "POST",
        data: "",
        beforeSend : function() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $(document).ajaxSend(function(e, xhr, options) { xhr.setRequestHeader(header, token); });
        },
        success: function(res) {
            console.log(res.code + ": " + res.message);
            $("#" + target + "_" + targetId + "_liked").toggleClass("btn-active btn-neutral");
            $("#" + target + "_" + targetId + "_liked" + " > span").text(res.data);
        },
        error: function(res) {
            console.log(res.responseJSON.code + ": " + res.responseJSON.message);
            alert(res.responseJSON.message);
        }
    })
}