package com.nagarro.pos.dao;

import java.util.List;

import com.nagarro.pos.model.Product;

public interface ProductDao {

	List<Product> getProductsDB();

	Product getProductById(int pid);

	List<Product> searchProducts(String toSearch);

}
