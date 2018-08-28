package com.frontierwholesales.core.magento.services.servlets;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frontierwholesales.core.beans.FrontierWholesalesProductSearch;
import com.frontierwholesales.core.magento.services.FrontierWholesalesMagentoCommerceConnector;
import com.frontierwholesales.core.models.ProductListModel;
import com.frontierwholesales.core.utils.FrontierWholesalesUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")

@Component(immediate = true,service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=FrontierWholesales Product List Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
       "sling.servlet.selectors=data",
       "sling.servlet.paths=/services/productlist",
        "sling.servlet.extensions=html"      
        
})


public class FrontierWholesalesProductListServlet extends SlingAllMethodsServlet{

	private static final Logger log = LoggerFactory.getLogger(FrontierWholesalesProductListServlet.class);
	private FrontierWholesalesMagentoCommerceConnector commerceConnector = new FrontierWholesalesMagentoCommerceConnector();
	private FrontierWholesalesUtils utils = new FrontierWholesalesUtils();
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		
		log.debug("doGet FrontierWholesalesProductListServlet Start here ");
		
		try {
			final String authorization = request.getHeader("Authorization");
			
			String groupId ="";
			
		   
			FrontierWholesalesProductSearch search = new FrontierWholesalesProductSearch();
			int currentPage = Integer.parseInt(request.getParameter("currentPage"));
			search.setCurrentPage(currentPage);
			int categoryId = Integer.parseInt(request.getParameter("categoryId"));
			search.setCategoryId(categoryId);
			int noOfRecsPerPage = Integer.parseInt(request.getParameter("noOfRecsPerPage"));
			search.setNoOfRecsPerPage(noOfRecsPerPage);
			String sortByPrice = request.getParameter("sortByPrice");
			search.setSortByPrice(sortByPrice);
			String sortByFeatured = request.getParameter("sortByFeatured");
			search.setSortByFeatured(sortByFeatured);
			String sortByNewProduct = request.getParameter("newProduct");
			search.setSortByNewProduct(sortByNewProduct);
			String facetSearch = request.getParameter("facetQuery");
			search.setFacetSearchQuery(facetSearch);
			if(authorization != null) {				
				groupId = utils.getCustomerDetailsByParameter("group_id", authorization);
			}
			
			String adminToken = commerceConnector.getAdminToken();
			String productList = commerceConnector.getProducts(adminToken, search);
			String catList = commerceConnector.getParentChildrenCategories(adminToken, categoryId);
			
			response.setContentType("text/html;charset=UTF-8;");
			response.setCharacterEncoding("UTF-8");
			
			String jsonResponse = utils.parseJsonObject(productList,noOfRecsPerPage,currentPage,request,groupId);
			
			jsonResponse = utils.addCategoryListToJson(jsonResponse, catList);
			
			Cookie cookie = FrontierWholesalesUtils.getCookie(request,"CustomerData");
			if(cookie != null) {
				log.debug("cookie is available");
				String cookieValue = cookie.getValue();
				String authToken = FrontierWholesalesUtils.getIdFromObject(cookieValue, "token");
				jsonResponse = addUserTokenToObject(jsonResponse,"authToken",authToken);
				log.debug("auth token is available");
			}
			
			ServletOutputStream stream = response.getOutputStream();
			log.debug(" FrontierWholesalesProductListServlet operations End ");
			stream.write(jsonResponse.getBytes("UTF-8"));
			
		}catch(Exception anyEx) {
			log.error("Error in productList "+anyEx.getMessage());
			response.getOutputStream().println("Error "+anyEx.getMessage());
		}
		log.debug("doGet FrontierWholesalesProductListServlet End here ");
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		
		log.debug("doPost FrontierWholesalesProductListServlet Start here ");
		try {
			
			String adminToken = commerceConnector.getAdminToken();
			
			String jsonData = request.getParameter("wishlist");
			String wishList = commerceConnector.addItemToWishList(adminToken, jsonData);
			JsonObject object = new JsonObject();
			if(wishList != null) {
			
				object.addProperty("WishList", "Added To WishList");
			}
			response.getOutputStream().println(object.toString());
		}catch(Exception anyEx) {
			log.error("Error in productList "+anyEx.getMessage());
			response.getOutputStream().println("Error");
		}
		
		log.debug("doPost FrontierWholesalesProductListServlet End here ");
	}
	
	private String addUserTokenToObject(String object,String key,String value) throws Exception{
			log.debug("addUserTokenToObject Start");
			JsonParser parser =  new JsonParser();
			JsonObject jsonObject =parser.parse(object).getAsJsonObject();
			
			jsonObject.addProperty(key, value);
		
			log.debug("addUserTokenToObject End ");
			return jsonObject.toString();
	}
}
