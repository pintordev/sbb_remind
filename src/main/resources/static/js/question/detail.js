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
            let id = res.data.id;
            let author = res.data.author;
            let content = res.data.content;
            let createDate = res.data.createDate;

            let template = `
                <div>
                    <div id="${anchor_tag}">
                        <span>${author}</span>
                        <span>${content}</span>
                        <span>${createDate}</span>

                        <!-- 답변 수정 -->
                        <a href="/answer/modify/${id}" class="btn btn-sm btn-outline border-gray-300 gap-1">
                            <i class="fa-solid fa-eraser"></i>
                        </a>

                        <!-- 답변 삭제 -->
                        <button onclick="_answer_delete(${id})" class="btn btn-sm btn-outline border-gray-300 gap-1">
                            <i class="fa-solid fa-trash"></i>
                        </button>

                        <!-- 답변 좋아요 -->
                        <button onclick="_like('answer', ${id})" class="btn btn-sm btn-outline border-gray-300 gap-1"id="answer_${id}_liked">
                            <i class="fa-solid fa-heart"></i>
                            <span>0</span>
                        </button>
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

function _answer_page(questionId, page, sort) {
    location.href = '/question/' + questionId
                    + '?aPage=' + page
                    + "&sort=" + sort;
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

function _sort(sort, questionId, page) {
    location.href = "/question/" + questionId
                    + "?aPage=" + page
                    + "&sort=" + sort;
}