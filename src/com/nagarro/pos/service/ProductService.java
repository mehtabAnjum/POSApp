package com.nagarro.pos.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nagarro.pos.dao.ProductDao;
import com.nagarro.pos.model.Product;

@Service
public class ProductService {

	final Logger logger = Logger.getLogger(ProductService.class);

	@Autowired
	ProductDao productDao;

	@Transactional
	public List<Product> getProducts() {
		return productDao.getProductsDB();
	}

	@Transactional
	public Product getProductById(int pid) {
		return productDao.getProductById(pid);
	}

	@Transactional
	public List<Product> searchProducts(String toSearch) {
		return productDao.searchProducts(toSearch);
	}

}
