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
    <div id="qrDiv" style="width: 200px; height: 40%; text-align: center; margin-bottom: 50px;">
        <%-- QR code image string 세팅 --%>
        <img id="startQrImg" style="width: 200px; height: 200px;" src="data:image/jpg;base64,${qrImageString}">
        <br>
        <button name="sendResultApi" onclick="sendResultApi()">결과 확인</button>
    </div>
    <div id="resultDiv" style="width: 100%; height: 50%; display: none;">
        <textarea id="resultText" style="width: 100%; height: 300px;"></textarea>
    </div>
</div>
</body>
<script>
    var comtrueToken = "<c:out value='${token}' />";
    var comtrueResultUrl = "<c:out value='${resultUrl}' />";

    // result API java code 호출
    function sendResultApi() {
        const formData = new FormData();
        formData.append("token", comtrueToken);
        formData.append("resultUrl", comtrueResultUrl);
        $.ajax({
            type: "POST",
            url: "${contextPath}/sendResultApi",
            enctype: "multipart/form-data",
            data: formData,
            contentType: false,
            processData: false,
            headers: {
                Accept: "*/*"
            },
            success: function (data) {
                if (data != null && data !== "" && typeof data === "string") {
                    data = JSON.parse(data);
                    console.log(JSON.stringify(data));
                }
                document.getElementById("resultText").value = JSON.stringify(data);
                document.getElementById("resultDiv").style.display = "block";
            },
            error: function (e) {
                console.log(e);
            }
        });
    }
</script>
</html>