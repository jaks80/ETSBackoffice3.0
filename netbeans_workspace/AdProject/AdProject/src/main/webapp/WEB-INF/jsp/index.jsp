<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html dir="ltr" lang="en-US">
    <head>
        <meta charset="utf-8">
        <title>Home</title>
        <meta name="viewport" content="initial-scale = 1.0, maximum-scale = 1.0, user-scalable = no, width = device-width">
        <%@include file="style.jsp" %>
        <script type="text/javascript">
            function _action()
            {             
                var kw = document.getElementById("kw").value;
                
                if(kw != ""){
                var s = "/search/" + kw;
                document.getElementById('searchform').action = s;
                document.getElementById("searchform").submit();  
            }
            }            
        </script>
    </head>
    <body>
        <div id="main">

            <header class="header">

            </header>
            <div class="sheet clearfix">
                <nav class="nav">
                    <ul class="hmenu">
                        <li><a href="/category/Computers" class="">Computers</a></li>
                        <li><a href="/category/Electronics" class="active">Electronics</a></li>
                        <li><a href="/category/Clothing">Clothing</a></li>
                        <li><a href="/category/Books">Books</a></li>
                        <li><a href="/category/Shoes">Shoes</a></li>
                        <li><a href="/category/Household">Household</a></li>
                        <li><a href="/category/Sports">Sports</a></li>
                        <li><a href="/category/Office">Office Supply</a></li>
                    </ul>
                </nav>
                <div class="layout-wrapper">
                    <div class="content-layout">
                        <div class="content-layout-row">
                            <div class="layout-cell sidebar1">
                                <div class="block clearfix">                                    
                                    <div class="blockcontent_advert">

                                    </div>
                                </div>

                            </div>
                            <div class="layout-cell content">
                                <article class="post article">
                                    <div class="postcontent postcontent-0 clearfix">                                                                                
                                        <div class="content-layout-wrapper layout-item-0">
                                            <div class="content-layout layout-item-3">
                                                <div class="content-layout-row">

                                                </div>
                                                <div class="content-layout-row">

                                                    <div style="margin: 129px 50px 170px 50px">                                                        
                                                        <form:form id="searchform" modelAttribute="searchAttribute" onsubmit="javascript:_action();return false;">
                                                            <form:input placeholder="Search item..." path="keyword" id="kw" type="text" class="search-text"/>
                                                            <input type="submit" value="search" name="search" style="position: absolute !important;right: 231px;top: 130px;background-color: #6A9301; height: 30px;width: 100px; color: #ffffff">
                                                        </form:form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>


                                </article></div>
                            <div class="layout-cell sidebar2"><div class="block clearfix">                                    
                                    <div class="blockcontent_advert"><p><br></p></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <%@include file="footer.jsp" %>

            </div>
        </div>
    </body>
</html>