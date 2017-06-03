<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>Hot Queries</title>

    <!-- Bootstrap -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="vendor/buttons/buttons.css" rel="stylesheet">
    <link href="css/hotQuery.css" rel="stylesheet">
<title>Hot Queries</title>
<%
	Vector<String> hotQueries = (Vector<String>) session.getAttribute("hotQueries");
	Vector<Integer> hotCounts = (Vector<Integer>) session.getAttribute("hotCounts");
	int topNum = hotCounts.size();
%>
</head>
<body>
<div class="container-fluid">
    <div class="content col-md-6">
        <%
                for (int i = 0; i < topNum; ++i) {%>

        <div id="<%=i + 1%>" class="entry">
            <div class="link">
                <a href="search?key=<%=hotQueries.elementAt(i)%>&page_num=1&time_filter=0"><%=hotQueries.elementAt(i)%>
                </a>
            </div>
            <div>
                <div class="count"><%=hotCounts.elementAt(i)%>
                </div>
            </div>
            <br>
        </div>
        <%
                }
        %>
    </div>
</div>
</body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="vendor/jquery/jquery-1.11.3.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
</html>