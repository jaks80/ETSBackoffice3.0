<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

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

<div class="textblock object227400126" data-left="99.06%">
    <form:form id="searchform" modelAttribute="searchAttribute" onsubmit="javascript:_action();return false;">
        <form:input placeholder="Search item..." path="keyword" id="kw" type="text" class="search-text"/>
        <input type="submit" value="search" name="search" class="search-button">
    </form:form>
</div>
