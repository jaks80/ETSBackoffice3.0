<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach items="${searchAttribute.itemsToShow}" var="product">
    <div class="content-layout-wrapper layout-item-0">
        <div class="content-layout layout-item-3">
            <div class="content-layout-row">
                <div class="layout-cell layout-item-5" style="width: 100%" >

                    <table>
                        <tr>
                            <th rowspan="4">                        
                            <img src="${product.imageURL}">                        
                        </th>
                        <th colspan="4" style="width: 100%"><b>
                                <a href="${product.sellersURL}&OP=${cidECN}" target="_blank"><c:out value="${product.title}" /></a>
                            </b></th>
                        </tr>
                        <tr>
                            <td>Shop from: Ebay</td>                            
                        </tr>
                        <tr>
                            <td>Current price: <b>$${product.currentPrice}</b></td>                            
                        </tr>
                        <tr>
                            <td></td>
                            
                        </tr>
                    </table>

                </div>           
            </div>
        </div>
    </div>
</c:forEach>
