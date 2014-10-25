<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html dir="ltr" lang="en-US">
    <head>
        <meta charset="utf-8">
        <title>Home</title>

        <meta name="viewport" content="initial-scale = 1.0, maximum-scale = 1.0, user-scalable = no, width = device-width">
        <%@include file="style.jsp" %>
    </head>

    <body>
        <div id="main">
            <header class="header">
                <%@include file="searchwidget.jsp" %>
            </header>
            <div class="sheet clearfix">
                <nav class="nav">
                    <ul class="hmenu">
                        <li><a href="/search/Computers" class="">Computers</a></li>
                        <li><a href="/search/Electronics" class="active">Electronics</a></li>
                        <li><a href="/search/Clothing">Clothing</a></li>
                        <li><a href="/search/Books">Books</a></li>
                        <li><a href="/search/Shoes">Shoes</a></li>
                        <li><a href="/search/Household">Household</a></li>
                        <li><a href="/search/Sports">Sports</a></li>
                        <li><a href="/search/Office">Office Supply</a></li>
                    </ul>
                </nav>
                <div class="layout-wrapper">
                    <div class="content-layout">
                        <div class="content-layout-row">
                            <div class="layout-cell sidebar1">
                                <div class="block clearfix">
                                    <div class="blockheader">
                                        <h3 class="t">Refine Search</h3>
                                    </div>
                                    <div class="blockcontent">
                                        <form:form id="refineform" modelAttribute="searchAttribute" onsubmit="javascript:_action();return false;">
                                            <table>
                                                <tr>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <b>Price range</b>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        $&nbsp;<form:input path="minSelectedPrice" id="minSelectedPrice" type="text" size="3"/>&nbsp;to&nbsp;
                                                        <form:input path="maxSelectedPrice" id="maxSelectedPrice" type="text" size="3"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td><br></td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <b>Sort By</b>
                                                        <select name="sortOrderType" id="sortOrderType">
                                                            <option value="best_match">Best Match</option>
                                                            <option value="price_desc">Price: High to Low</option>
                                                            <option value="price_asc">Price: Low to High</option>
                                                        </select>
                                                    </td>
                                                </tr>                                                
                                                <tr><td><br></td></tr>
                                                <tr>
                                                    <td style="text-align: right;">
                                                        <input type="submit" value="Refine Search" name="search" class="button">
                                                    </td>
                                                </tr>
                                            </table>
                                        </form:form>
                                    </div>
                                </div>
                                <!--
                                <div class="block clearfix">
                                    <div class="blockheader">
                                        <h3 class="t">Refne Search</h3>
                                    </div>
                                    <div class="blockcontent">
                                        <p>refine block2</p>
                                    </div>
                                </div>
                                -->
                            </div>
                            <div class="layout-cell content"><article class="post article">


                                    <div class="postcontent postcontent-0 clearfix"><div class="content-layout-wrapper layout-item-0">                                            
                                            <div class="content-layout-wrapper layout-item-0">
                                                <div class="content-layout layout-item-3">
                                                    <div class="content-layout-row">
                                                        <div class="layout-cell layout-item-4" style="width: 100%" >
                                                            <script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
                                                            <!-- 728X90Top -->
                                                            <ins class="adsbygoogle"
                                                                 style="display:inline-block;width:728px;height:90px"
                                                                 data-ad-client="ca-pub-6278518161512683"
                                                                 data-ad-slot="1357214408"></ins>
                                                            <script>
                                                                (adsbygoogle = window.adsbygoogle || []).push({});
                                                            </script>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="content-layout layout-item-1">
                                                <div class="content-layout-row">
                                                    <div class="layout-cell layout-item-2" style="width: 100%" >
                                                        <%@include file="pagination.jsp" %>
                                                    </div>
                                                </div>
                                            </div>
                                            <%@include file="items.jsp" %>
                                            <div class="content-layout layout-item-1">
                                                <div class="content-layout-row">
                                                    <div class="layout-cell layout-item-2" style="width: 100%" >
                                                        <%@include file="pagination.jsp" %>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="content-layout-wrapper layout-item-0">
                                                <div class="content-layout layout-item-3">
                                                    <div class="content-layout-row">
                                                        <div class="layout-cell layout-item-4" style="width: 100%" >
                                                            <script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
                                                            <!-- 728X90Bottom -->
                                                            <ins class="adsbygoogle"
                                                                 style="display:inline-block;width:728px;height:90px"
                                                                 data-ad-client="ca-pub-6278518161512683"
                                                                 data-ad-slot="7072344005"></ins>
                                                            <script>
                                                                (adsbygoogle = window.adsbygoogle || []).push({});
                                                            </script>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                </article></div>
                            <div class="layout-cell sidebar2">
                                <div class="block clearfix">

                                    <c:if test="${not empty searchAttribute.relatedSearch}">
                                        <div class="blockheader">
                                            <h3 class="t">Related Search</h3>
                                        </div>
                                        <div class="blockcontent">
                                            <c:forEach items="${searchAttribute.relatedSearch}" var="kw">
                                                <c:set var="_kw" value="${fn:replace(kw,' ', '+')}" />
                                                <a href="/search/"><p>${kw}</p></a>
                                            </c:forEach>
                                        </div>   
                                    </c:if>
                                </div>
                                <br>
                                <div class="block clearfix">
                                    <div class="blockcontent_advert">
                                        <script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
                                        <!-- 160X600 Right -->
                                        <ins class="adsbygoogle"
                                             style="display:inline-block;width:160px;height:600px"
                                             data-ad-client="ca-pub-6278518161512683"
                                             data-ad-slot="6014413203"></ins>
                                        <script>
                                                                (adsbygoogle = window.adsbygoogle || []).push({});
                                        </script>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%@include file="footer.jsp" %>

                </div>
            </div>
           </div>          
    </body>
</html>