<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="serv.RequestBean" %>
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
        <div id="1" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">精密测试技术及仪器国家重点实验室是1990年经国家计委批准、利用世行贷款、
                由清华大学与天津大学联合组建的国家级重点实验室。1995年实验室建成并通过主管部门验收，同...
            </div>
            <div class="cite"><span class="addr">pmti.pim.tsinghua.edu.cn/</span> - <span class="date">2012-2-21</span>
            </div>
            <br>
        </div>
        <div id="2" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="3" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="">
                <div class="pic">
                    <img style="height: 90px;" src="http://yqs.pim.tsinghua.edu.cn/measure1.files/image002.jpg">
                </div>
                <div class="abstract">精密测试技术及仪器国家重点实验室是1990年经国家计委批准、利用世行贷款、
                    由清华大学与天津大学联合组建的国家级重点实验室。1995年实验室建成并通过主管部门验收，同...
                </div>
            </div>
            <div class="cite"><span class="addr">pmti.pim.tsinghua.edu.cn/</span> - <span class="date">2012-2-21</span>
            </div>
            <br>
        </div>
        <div id="4" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="5" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="6" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="7" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="8" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="9" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
        <div id="10" class="entry">
            <div class="link"><a href="#">这是一个超链接结果</a></div>
            <div class="abstract">1994 年始，与铁科院合作开展 DP 传感器在桥梁测试中应用的研究。 • 迄今为止
                ，已发展了适用于桥梁测试的磁电式速度（位移）传感器、现场桥梁动态测试技术、现场桥梁...
            </div>
            <div class="cite"><span class="addr">yqs.pim.tsinghua.edu.cn/m...</span> - <span
                    class="date">2006-4-13</span></div>
            <br>
        </div>
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
        <div class="buttons">
            <button class="button button-circle">1</button>
            &nbsp;
            <button class="button button-circle">2</button>
            &nbsp;
            <button class="button button-circle">3</button>
            &nbsp;
            <button class="button button-circle">4</button>
            &nbsp;
            <span>5</span>
            &nbsp;
            <button class="button button-circle">6</button>
            &nbsp;
            <button class="button button-circle">7</button>
            &nbsp;
            <button class="button button-circle">8</button>
            &nbsp;
            <button class="button button-circle">9</button>
            &nbsp;
            <button class="button button-circle">10</button>
        </div>
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