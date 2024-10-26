<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>QR HOME</title>
    <script type="text/javascript" src="${contextPath}/static/js/jquery-2.2.4.min.js"></script>
</head>
<body>
<div style="width: 100%; height: 100%;">
    <button name="getStartApi" onclick="getStartApi()">ocr url 받기</button>

    <form id="popupForm" name="popupForm" method="post"></form>
</div>
</body>
<script>
    // start API java code 호출하는 버튼
    function getStartApi() {
        $.ajax({
            type: "POST",
            url: "${contextPath}/getStartApiUrl",
            data: {key: ""},
            headers: {
                Accept: "*/*"
            },
            success: function (data) {
                console.log(typeof data);
                data = JSON.parse(data);
                console.log("data: " + JSON.stringify(data));

                // QR code 생성하는 url 호출
                const popupForm = document.getElementById("popupForm");
                popupForm.method = "post";
                popupForm.action = "${contextPath}/qrOpen";
                popupForm.target = "_self";

                // token
                let input = document.createElement("input");
                input.type = "hidden";
                input.name = "token";
                input.value = data.token;
                popupForm.appendChild(input);

                // ocr 촬영 url
                input = document.createElement("input");
                input.type = "hidden";
                input.name = "startUrl";
                input.value = data._links._start_page.href;
                popupForm.appendChild(input);

                // result api url
                input = document.createElement("input");
                input.type = "hidden";
                input.name = "resultUrl";
                input.value = data._links._result_api.href;
                popupForm.appendChild(input);
                popupForm.submit();
            },
            error: function (e) {
                console.log(e);
            }
        });
    }
</script>
</html>
