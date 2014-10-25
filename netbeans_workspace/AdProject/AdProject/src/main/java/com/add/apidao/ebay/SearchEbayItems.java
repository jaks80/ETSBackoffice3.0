package com.add.apidao.ebay;

import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.AspectFilter;
import com.ebay.services.finding.FindItemsByCategoryRequest;
import com.ebay.services.finding.FindItemsByCategoryResponse;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.ItemFilter;
import com.ebay.services.finding.ItemFilterType;
import com.ebay.services.finding.OutputSelectorType;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.SortOrderType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("searchEbayItems")
public class SearchEbayItems {

    private final String devID = "89ded839-c69e-4b45-b020-86e131bf9c49";
    private final String appID = "ETypeSys-ad5d-4fe1-8a2f-42d2b1dabe57";
    private final String certID = "b1887c14-33fc-4c6d-a4db-18ac1545ecc8";

    public SearchEbayItems() {

    }

    public List<SearchItem> search(String keyword, String sortOrderType, String maxPrice, String minPrice) {
        List<SearchItem> items = new ArrayList<>();

        try {
            ClientConfig config = new ClientConfig();
            config.setApplicationId(this.appID);
            FindingServicePortType serviceClient = FindingServiceClientFactory.getServiceClient(config);
            FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();

            request.getOutputSelector().add(OutputSelectorType.ASPECT_HISTOGRAM);
            request.getOutputSelector().add(OutputSelectorType.CATEGORY_HISTOGRAM);
            request.getOutputSelector().add(OutputSelectorType.CONDITION_HISTOGRAM);
            request.setKeywords(keyword);

            if (sortOrderType != null && !sortOrderType.isEmpty()) {
                request.setSortOrder(SortOrderType.valueOf(sortOrderType));
            }

            //Filter items
            if (maxPrice != null && !maxPrice.isEmpty()) {
                ItemFilter maxPriceFilter = new ItemFilter();
                maxPriceFilter.setName(ItemFilterType.MAX_PRICE);
                maxPriceFilter.setParamName("Currency");
                maxPriceFilter.setParamValue("USD");
                maxPriceFilter.getValue().add(maxPrice.trim());
                request.getItemFilter().add(maxPriceFilter);
            }

            if (minPrice != null && !minPrice.isEmpty()) {
                ItemFilter minPriceFilter = new ItemFilter();
                minPriceFilter.setName(ItemFilterType.MIN_PRICE);
                minPriceFilter.setParamName("Currency");
                minPriceFilter.setParamValue("USD");
                minPriceFilter.getValue().add(minPrice.trim());
                request.getItemFilter().add(minPriceFilter);
            }

            //set request parameters
            request.setKeywords(keyword);
            FindItemsByKeywordsResponse result = serviceClient.findItemsByKeywords(request);

            items = result.getSearchResult().getItem();
            for (SearchItem item : items) {
                System.out.println(item.getTitle());
            }

        } catch (Exception ex) {
            System.out.println("Exception:"+ex);
        }
        return items;
    }

    public List<SearchItem> searchItemsByCategory(String catId,
            String sortOrderType,
            String maxPrice,
            String minPrice, Map<String, String> aspectMap) {

        ClientConfig config = new ClientConfig();
        config.setApplicationId(this.appID);
        FindingServicePortType serviceClient = FindingServiceClientFactory.getServiceClient(config);

        FindItemsByCategoryRequest request = new FindItemsByCategoryRequest();
        request.getCategoryId().add(catId);
        request.getOutputSelector().add(OutputSelectorType.ASPECT_HISTOGRAM);
        request.getOutputSelector().add(OutputSelectorType.CATEGORY_HISTOGRAM);
        request.getOutputSelector().add(OutputSelectorType.CONDITION_HISTOGRAM);

        for (Map.Entry<String, String> entry : aspectMap.entrySet()) {
            AspectFilter filter = new AspectFilter();
            filter.setAspectName(entry.getKey());
            filter.getAspectValueName().add(entry.getValue());
            request.getAspectFilter().add(filter);
        }

        if (sortOrderType != null && !sortOrderType.isEmpty()) {
            request.setSortOrder(SortOrderType.valueOf(sortOrderType));
        }

        if (maxPrice != null && !maxPrice.isEmpty()) {
            ItemFilter maxPriceFilter = new ItemFilter();
            maxPriceFilter.setName(ItemFilterType.MAX_PRICE);
            maxPriceFilter.setParamName("Currency");
            maxPriceFilter.setParamValue("USD");
            maxPriceFilter.getValue().add(maxPrice.trim());
            request.getItemFilter().add(maxPriceFilter);
        }

        if (minPrice != null && !minPrice.isEmpty()) {
            ItemFilter minPriceFilter = new ItemFilter();
            minPriceFilter.setName(ItemFilterType.MIN_PRICE);
            minPriceFilter.setParamName("Currency");
            minPriceFilter.setParamValue("USD");
            minPriceFilter.getValue().add(minPrice.trim());
            request.getItemFilter().add(minPriceFilter);
        }

        FindItemsByCategoryResponse result = serviceClient.findItemsByCategory(request);
        List<SearchItem> items = result.getSearchResult().getItem();

        return items;
    }
}
