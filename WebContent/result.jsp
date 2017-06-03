<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="java.util.Vector" %>
<%@ page import="JavaBean.*" %>
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
    <div style="padding-left: 100px;">
        <img src="pic/icon.png" alt="返回首页" align="left" style="height: 80px;">
    </div>
    <div style="padding-top: 24px;padding-left: 80px;" class="col-md-offset-2">
        <form action="search">
            <table>
                <tr>
                    <td>
                        <input type="text" class="search_input" name="key" value="<%=rb.getKey()%>">
                        <input type="text" name="page_num" value="1" hidden>
                    </td>
                    <td>
                        <button type="submit" class="search_btn">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                        </button>
                    </td>
                    <td style="padding-left: 20px;">
                        <button onclick="window.location='https://www.baidu.com/s?wd=<%=rb.getKey()%>'" type="button">校外搜索</button>
                    </td>
                </tr>
            </table>
        </form>
        <h5 hidden id="correct_ctl">您要找的是不是： &nbsp;&nbsp;<b id="correction"></b></h5>
    </div>
</div>
<div class="container-fluid">
    <div class="options col-xs-2" style="margin-left: 3.5%;">

    </div>
    <div class="content col-xs-6">
        <%
            ResultBean reb = (ResultBean) session.getAttribute("result");
            Vector<EntryBean> vec = reb.getVec();
            if (reb.getTotal_num() > 0 && vec.size() > 0) {
                for (int i = 0; i < vec.size(); ++i) {%>

        <div id="<%=i + 1%>" class="entry">
            <div class="link">
                <a href="http://<%=vec.elementAt(i).getLink()%>"><%=vec.elementAt(i).getTitle()%>
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
                <div class="abstract"><%=vec.elementAt(i).getAbst()%>...
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
    <div class="attachment col-xs-3" style="padding-left: 40px;">
        <h5>实时热搜榜</h5>
        <canvas id="hot_query" width="400" height="400">

        </canvas>
        <br>
        <%
            ImagineBean ib = (ImagineBean) session.getAttribute("imagine");
            Vector<String> svec = ib.getImagineWords();
            if (svec.size() > 0) {
        %>
        <h5>您可能感兴趣的搜索</h5>
        <h5>（结果由word2vec生成）</h5>
        <%
            }
        %>
        <table style="padding-top: 10px; padding-bottom: 10px;">
            <%

                for (int i = 0; i < svec.size(); ++i) {
                    if (i % 3 == 0) {
            %>
            <tr>
                <%
                    }
                %>
                <td style="padding: 5px 10px 5px 10px">
                    <a href="search?key=<%=svec.elementAt(i)%>&page_num=1"><%=svec.elementAt(i)%>
                    </a>
                </td>
                <%
                    if (i % 3 == 2) {
                %>
            </tr>
            <%
                    }
                }
            %>
        </table>
    </div>
</div>

<div class="footer container-fluid">
    <div class="col-md-2" style="margin-left: 3.5%"></div>
    <div class="col-md-6">

        <%
            if (reb.getTotal_num() > 10) {
        %>
        <div class="buttons row" style="padding: 20px;">
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
            <button class="button button-circle"
                    onclick="window.location='search?key=<%=rb.getKey()%>&page_num=<%=i+1%>';"><%=i + 1%>
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

<div style="padding: 24px 10px 30px 80px" class="col-xs-offset-2">
    <form action="search">
        <table>
            <tr>
                <td>
                    <input type="text" class="search_input" name="key" value="<%=rb.getKey()%>">
                    <input type="text" name="page_num" value="1" hidden>
                </td>
                <td>
                    <button type="submit" class="search_btn">
                        <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                    </button>
                </td>
                <td style="padding-left: 20px;">
                    <button onclick="window.location='https://www.baidu.com/s?wd=<%=rb.getKey()%>'" type="button">校外搜索</button>
                </td>
            </tr>
        </table>
    </form>
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script type="text/javascript" src="vendor/jquery/jquery-1.11.3.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script type="text/javascript" src="vendor/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/Chart.min.js"></script>
<script type="text/javascript" src="js/ajaxCookies.js"></script>
<script>
    $(document).ready(function () {
        <%
            HotQueryBean hqb = (HotQueryBean)session.getAttribute("hotquery");
            Vector<String> hotQueries = hqb.getQueries();
            Vector<Integer> hotCounts = hqb.getCounts();
            int count = hotCounts.size();
            String data_label = "", data_num = "";
            for(int i = 0; i < count; ++i) {
                data_label += "\"" + hotQueries.elementAt(i) + "\"";
                data_num += hotCounts.elementAt(i);
                if(i != count - 1) {
                    data_label += ", ";
                    data_num += ", ";
                }
            }
        %>
        var ctx = $('#hot_query').get(0).getContext('2d');
        var hot_query = new Chart(ctx, {
            type: 'horizontalBar',
            data: {
                labels: [<%=data_label%>],
                datasets: [{
                    label: '热搜指数',
                    data: [<%=data_num%>],
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255,99,132,1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });

        var text = $('.search_input').val();
        text = {"text":text};
        $.post("/correction", text, function(data, status) {
            data = JSON.parse(data);
            console.log(data);
            var result = data.result;
            var html = '<a id="cor_link">';
            var word = "";
            var error = false;
            for (var i = 0; i < result.length; i++) {
                if(result[i].t == 0 || result[i].t > 9) {
                    html += result[i].n;
                    word += result[i].n;
                } else {
                    error = true;
                    html += "<span style='color: red'>" + result[i].r[0].n + "</span>";
                    word += result[i].r[0].n;
                }
            }
            html += "</a>";
            $('#correction').html(html);
            $('#cor_link').attr("href", "search?key=" + word + "&page_num=1");
            if(error) {
                $('#correct_ctl').attr('hidden', false);
            }
            console.log(data);
        });

    });
</script>
</body>
</html>