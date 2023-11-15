function _find_loginId() {
    $("#idFindEmail_error").text("").removeClass("text-red-400 text-green-500");
    $.ajax({
        url: "/member/find/loginId",
        type: "GET",
        data: {
            "email": $("#idFindEmail").val()
        },
        success: function(res) {
            console.log(res.code + ": " + res.message);
            $("#idFindEmail_error").text("입력한 이메일로 가입한 아이디는 " + res.data + " 입니다").addClass("text-green-500");
            $("#pwFindLoginId").val(res.data).focus();
            $("#pwFindEmail").val($("#idFindEmail").val());
        },
        error: function(res) {
            console.log(res.responseJSON.code + ": " + res.responseJSON.message);
            $("#idFindEmail_error").text(res.responseJSON.message).addClass("text-red-400");
        }
    })
}

function _reset_password() {
    $("#pwFindLoginId_error").text("").removeClass("text-red-400 text-green-500");
    $.ajax({
        url: "/member/find/password",
        type: "POST",
        data: {
            "loginId": $("#pwFindLoginId").val(),
            "email": $("#pwFindEmail").val()
        },
        beforeSend : function() {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $(document).ajaxSend(function(e, xhr, options) { xhr.setRequestHeader(header, token); });
        },
        success: function(res) {
            console.log(res.code + ": " + res.message);
            $("#pwFindLoginId_error").text(res.message).addClass("text-green-500");
        },
        error: function(res) {
            console.log(res.responseJSON.code + ": " + res.responseJSON.message);
            $("#pwFindLoginId_error").text(res.responseJSON.message).addClass("text-red-400");
        }
    })
}