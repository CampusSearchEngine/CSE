<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="serv.RequestBean" %>
<%@ page import="serv.ResultBean" %>
<%@ page import="java.util.Vector" %>
<%@ page import="serv.EntryBean" %>
<%
    RequestBean rb = (RequestBean) session.getAttribute("request");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>SearcTsing</title>

    <!-- Bootstrap -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="vendor/buttons/buttons.css" rel="stylesheet">
    <link href="css/result.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>
<body>
<div class="header" style="vertical-align: middle">
    <div class="col-md-offset-1">
        <img src="pic/icon.png" alt="返回首页" align="left" style="height: 80px;">
    </div>
    <div style="padding-top: 24px;" class="col-md-offset-3">
        <form action="/search">
            <table>
                <tr>
                    <td>
                        <input type="text" class="search_input" name="key" value="<%=rb.getKey()%>">
                        <input type="text" name="page_num" value="1" hidden>
                        <input type="text" name="time_filter" value="0" hidden>
                    </td>
                    <td>
                        <button type="button" class="search_btn">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                        </button>
                    </td>
                    <td style="padding-left: 20px;">
                        <button type="button">校外搜索</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div class="container-fluid">
    <div class="options col-md-2" style="margin-left: 3.5%;">

    </div>
    <div class="content col-md-6">
        <%
            ResultBean reb = (ResultBean) session.getAttribute("result");
            Vector<EntryBean> vec = reb.getVec();
            if (reb.getTotal_num() > 0 && vec.size() > 0) {
                for (int i = 0; i < vec.size(); ++i) {%>

        <div id="<%=i + 1%>" class="entry">
            <div class="link">
                <a href="<%=vec.elementAt(i).getLink()%>"><%=vec.elementAt(i).getTitle()%>
                </a>
            </div>
            <div>
                <%
                    if (1 == 0) {
                %>
                <div class="pic">
                    <img style="height: 90px;" src="http://yqs.pim.tsinghua.edu.cn/measure1.files/image002.jpg">
                </div>
                <%
                    }
                %>
                <div class="abstract"><%=vec.elementAt(i).getAbst()%>
                </div>

            </div>
            <div class="cite"><span class="addr"><%=vec.elementAt(i).getLink()%></span> - <span
                    class="type"><%=vec.elementAt(i).getType()%></span>
            </div>
            <br>
        </div>
        <%
            }
        } else if (reb.getTotal_num() == 0) {
        %>
        <h3>抱歉，没能找到与"<em style="color: red;"><%=rb.getKey()%>
        </em>"相关的网页</h3>
        <%
            }
        %>
    </div>
    <div class="attachment col-md-2">
    </div>
</div>

<div class="footer container-fluid">
    <div class="col-md-2" style="margin-left: 3.5%"></div>
    <div class="col-md-6">
        <p>相关搜索：</p>
        <p style="padding-top: 10px; padding-bottom: 10px;">
            <a href="#">blablalba....</a>&nbsp;&nbsp;<a href="#">blablalba....</a>&nbsp;&nbsp;<a
                href="#">blablalba....</a><br>
            <a href="#">blablalba....</a>&nbsp;&nbsp;<a href="#">blablalba....</a>&nbsp;&nbsp;<a
                href="#">blablalba....</a><br>
            <a href="#">blablalba....</a>&nbsp;&nbsp;<a href="#">blablalba....</a>&nbsp;&nbsp;<a
                href="#">blablalba....</a><br>
        </p>
        <%
            if (reb.getTotal_num() > 10) {
        %>
        <div class="buttons row">
            <%
                int bc = reb.getTotal_num() / 10;
                if (reb.getTotal_num() % 10 > 0) bc += 1;
                for (int i = 0; i < bc; ++i) {
                    if (rb.getPageNum() == i + 1) {
            %>
            <span>&nbsp;&nbsp;<%=i + 1%>&nbsp;&nbsp;</span>
            &nbsp;
            <%
            } else {
            %>
            <button class="button button-circle"><%=i + 1%>
            </button>
            &nbsp;
            <%
                    }
                }
            %>
        </div>
        <%
            }
        %>
    </div>
</div>

<div class="container-fluid end">

</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="vendor/jquery/jquery-1.11.3.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>