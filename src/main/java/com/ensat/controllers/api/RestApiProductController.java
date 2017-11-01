package com.ensat.controllers.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ensat.entities.Product;
import com.ensat.services.ProductService;
import com.ensat.util.CustomErrorType;


@RestController
@RequestMapping("/api")
public class RestApiProductController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiProductController.class);

	@Autowired
	ProductService productService; //Service which will do all data retrieval/manipulation work

	// -------------------Retrieve All Products---------------------------------------------

	@RequestMapping(value = "/product", method = RequestMethod.GET)
	public ResponseEntity<List<Product>> listAllProducts() {
		List<Product> products = (List<Product>) productService.listAllProducts();
		if (products.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
	}

	
	// -------------------Retrieve Single Product------------------------------------------

	@RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getProduct(@PathVariable("id") long id) {
		logger.info("Fetching Product with id {}", id);
		Product product  = productService.getProductById(new Integer((int) id));
		if (product == null) {
			logger.error("Product with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Product with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Product>(product, HttpStatus.OK);
	}


	// -------------------Create a Product-------------------------------------------

	@RequestMapping(value = "/product/", method = RequestMethod.POST)
	public ResponseEntity<?> createProduct(@RequestBody Product product, UriComponentsBuilder ucBuilder) {
		logger.info("Creating Product : {}", product);

		if (product==null) {
			logger.error("Unable to create. A Product with name {} already exist", product.getName());
			return new ResponseEntity(new CustomErrorType("Unable to create. A Product with name " + 
			product.getName() + " already exist."),HttpStatus.CONFLICT);
		}
		productService.saveProduct(product);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/product/{id}").buildAndExpand(product.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	// ------------------- Update a Product ------------------------------------------------

	@RequestMapping(value = "/product/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProduct(@PathVariable("id") long id, @RequestBody Product product) {
		logger.info("Updating Product with id {}", id);

		Product currentProduct = productService.getProductById(new Integer((int) id));

		if (currentProduct == null) {
			logger.error("Unable to update. Product with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to upate. Product with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		currentProduct.setName(product.getName());
		currentProduct.setPrice(product.getPrice());
		currentProduct.setProductId(product.getProductId());


		productService.saveProduct(currentProduct);
		return new ResponseEntity<Product>(currentProduct, HttpStatus.OK);
	}

	// ------------------- Delete a Product-----------------------------------------

	@RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProduct(@PathVariable("id") long id) {
		logger.info("Fetching & Deleting Product with id {}", id);

		Product product = productService.getProductById(new Integer((int) id));
		if (product == null) {
			logger.error("Unable to delete. Product with id {} not found.", id);
			return new ResponseEntity(new CustomErrorType("Unable to delete. Product with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		productService.deleteProduct(new Integer((int) id));
		return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
	}


	



}