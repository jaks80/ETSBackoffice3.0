<span class="pager">    
    
    <!--<span class="pager active"><a href="">2</a></span>-->
    
    <c:forEach begin="1" end="${searchAttribute.numberOfPage}" varStatus="loop">
        <c:set var="indx" value="${loop.index}"/>
        <c:if test="${indx eq searchAttribute.currentPage}">
            <c:set var="c" value="active"/>
        </c:if>
        <span class="pager ${c}"><a href="?page=${indx}">${indx}</a></span>
        <c:set var="c" value=""/>
    </c:forEach>
        
    <c:choose>
        <c:when test="${searchAttribute.currentPage == indx}">
            <span class="pager"><a href="?page=${searchAttribute.currentPage-1}">« Previous</a></span>
            <span class="pager"><a href="?page=1">« First</a></span>
        </c:when>
        <c:when test="${searchAttribute.currentPage > 1 && searchAttribute.currentPage <5}">
            <span class="pager"><a href="?page=${searchAttribute.currentPage-1}">« Previous</a></span>
            <span class="pager"><a href="?page=${searchAttribute.currentPage+1}">Next »</a></span>
            <span class="pager"><a href="?page=1">« First</a></span>                                        
            <span class="pager"><a href="?page=${indx}">Last »</a></span>
        </c:when>
        <c:otherwise>
            <span class="pager"><a href="?page=${searchAttribute.currentPage+1}">Next »</a></span>
            <span class="pager"><a href="?page=${indx}">Last »</a></span>
        </c:otherwise>
    </c:choose>                                        
</span>