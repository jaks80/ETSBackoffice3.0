package com.add.controller;

import com.add.apidao.yahoo.YBossDao;
import com.add.model.page.FullSearch;
import com.add.service.EbayService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Yusuf
 */
@Controller
@RequestMapping("/")
public class FindingController {

    @Resource(name = "ebayService")
    private EbayService ebayService;

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    public String getNew(Model model) {

        model.addAttribute("searchAttribute", new FullSearch());
        return "/kwsearch";
    }

    @RequestMapping(value = "/search/{keyword}", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    public String searchItems(@PathVariable("keyword") String keyword,
            @ModelAttribute("searchAttribute") FullSearch fullSearch,Model model, HttpServletRequest request) {

        String pageNumber = (String) request.getParameter("page");

        String vals[] = keyword.split("~");

        keyword = vals[0];
        
        keyword = keyword.replace("+", " ");
        
        String sortOrderType = "";
        String minSelectedPrice = "";
        String maxSelectedPrice = "";

        if(vals.length > 1){
         sortOrderType = vals[1];
        }

        if(vals.length > 2){
         minSelectedPrice = vals[2].trim();
        }

        if(vals.length > 3){
         maxSelectedPrice = vals[3].trim();
        }

        if (pageNumber == null) {
            fullSearch.setKeyword(keyword);
            fullSearch.setSelectedSortOrder(sortOrderType);
            fullSearch.setMinSelectedPrice(minSelectedPrice);
            fullSearch.setMaxSelectedPrice(maxSelectedPrice);

            fullSearch = ebayService.getFullSearchPage(fullSearch);
            fullSearch.setRelatedSearch(getRelatedKeywords(keyword));
            request.getSession().setAttribute("fs_session", fullSearch);
        } else {
            fullSearch = (FullSearch) request.getSession().getAttribute("fs_session");
            model.addAttribute("searchAttribute", fullSearch);
            fullSearch.paginateProduct(pageNumber);

        }

        return "/kwsearch";
    }

    @RequestMapping(value = "/category/{catParent}", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    public String searchParentCategoryItems(@PathVariable("catParent") String catParent,HttpServletRequest request) {

        System.out.println("test");
        return "/kwsearch";
    }
    
        private List<String> getRelatedKeywords(String keyword) {
        YBossDao dao = new YBossDao();
        dao.setSearchString(keyword);
        String result = null;
        try {
            result = dao.returnHttpData();
        } catch (Exception ex) {
           
        }
        List<String> keywords = dao.getSuggestedKeywords(result);
        return keywords;
    }
}
