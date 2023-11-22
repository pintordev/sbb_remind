$(function() {
    _reset();
});

function _answer_create(qId, aId, tId) {

    aId = _isUndefined(aId);
    let target = tId === undefined ? aId : tId;

    $.ajax({
        url: "/answer/create",
        type: "POST",
        data: {
            "content": _get_content(target),
            "qId": qId,
            "aId": aId,
            "tId": tId
        },
        beforeSend : function() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $(document).ajaxSend(function(e, xhr, options) { xhr.setRequestHeader(header, token); });
        },
        success: function(res) {
            console.log(res.code + ": " + res.message);

            let anchor_tag = "answer" + res.data.id;
            let id = res.data.id;
            let author = res.data.author;
            let content = res.data.content;
            let createDate = res.data.createDate;
            let count = res.data.count;
            let tag_id = res.data.tag_id;
            let tag_nick_name = res.data.tag_nick_name;

            let template = `
                <div id="${anchor_tag}">
                    <span>${author}</span>
                    <span><p>${content}</p></span>
                    <span>${createDate}</span>

                    <!-- 댓글 수정 -->
                    <a href="/answer/modify/${id}" class="btn btn-sm btn-outline border-gray-300 gap-1">
                        <i class="fa-solid fa-eraser"></i>
                    </a>

                    <!-- 댓글 삭제 -->
                    <button class="btn btn-sm btn-outline border-gray-300 gap-1" onclick="_delete('answer', ${id})">
                        <i class="fa-solid fa-trash"></i>
                    </button>

                    <!-- 댓글 좋아요 -->
                    <button class="btn btn-sm btn-outline border-gray-300 gap-1" id="answer${id}_liked" onclick="_like('answer', ${id})" >
                        <i class="fa-solid fa-heart"></i>
                        <span>0</span>
                    </button>

                    <!-- 댓글 작성 버튼 -->
                    <button class="btn btn-sm btn-outline border-gray-300 gap-1" onclick="_form_open(${id})">
                        <span>댓글</span>
                    </button>

                    <!-- 댓글 작성 폼 -->
                    <form id="answer${id}_form" class="hidden">
                        <textarea name="content" placeholder="내용을 입력하세요"></textarea>
                        <button type="button" class="btn btn-sm btn-outline border-gray-300 gap-1" onclick="_form_close(${id})">취소</button>
                        <button type="button" class="btn btn-sm btn-outline border-gray-300 gap-1" onclick="_answer_create(${qId}, ${aId}, ${id})">댓글등록</button>
                    </form>
                </div>
            `;
            $("#answer" + aId + "_last").before(template);

            if (tId !== undefined) {
                let sub_template = `
                    <a class="text-gray-400" href="#answer${tag_id}">@${tag_nick_name}</a>
                `;
                $("#" + anchor_tag).prepend(sub_template);
            }

            _form_close(target);

            _form_clear(target);
            _reset();

            $("#answer_count").text(count);
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

function _question_delete(targetId) {
    if (confirm("정말 삭제하시겠습니까?")) {
        location.href = "/question/delete/" + targetId;
    }
}

function _like(target, targetId) {
    $.ajax({
        url: "/" + target + "/like",
        type: "POST",
        data: {
            "id": targetId
        },
        beforeSend : function() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $(document).ajaxSend(function(e, xhr, options) { xhr.setRequestHeader(header, token); });
        },
        success: function(res) {
            console.log(res.code + ": " + res.message);
            $("#" + target + res.data.id + "_liked").toggleClass("btn-active btn-neutral");
            $("#" + target + res.data.id + "_liked" + " > span").text(res.data.liked);
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

function _form_open(id) {
    $("#answer" + id + "_form").removeClass("hidden");
}

function _form_close(id) {
    if (id !== "") $("#answer" + id + "_form").addClass("hidden");
}

function _form_clear(id) {
    $("#answer" + id + "_form > textarea").removeClass("editor-loaded");
    $("#answer" + id + "_form > div").remove();
}

function _get_content(id) {
    let content = [];

    $("#answer" + id + "_form span.cm-null.cm-spell-error").each(function() {
        content.push($(this).text());
    })

    return content.join(" ");
}

function _answer_delete(id) {
    if (confirm("정말 삭제하시겠습니까?")) {
        $.ajax({
            url: "/answer/delete",
            type: "POST",
            data: {
                "id": id
            },
            beforeSend : function() {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                $(document).ajaxSend(function(e, xhr, options) { xhr.setRequestHeader(header, token); });
            },
            success: function(res) {
                console.log(res.code + ": " + res.message);
                $("#answer" + res.data).remove();
            },
            error: function(res) {
                console.log(res.responseJSON.code + ": " + res.responseJSON.message);
                alert(res.responseJSON.message);
            }
        })
    }
}