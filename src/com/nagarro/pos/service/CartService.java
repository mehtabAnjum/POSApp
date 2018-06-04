package com.nagarro.pos.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nagarro.pos.dao.CartDao;
import com.nagarro.pos.dao.CartProductMapperDao;
import com.nagarro.pos.dao.CustomerDao;
import com.nagarro.pos.dao.ProductDao;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Cart;
import com.nagarro.pos.model.CartProductMapper;
import com.nagarro.pos.model.Customer;
import com.nagarro.pos.model.Product;

@Service
public class CartService {

	final Logger logger = Logger.getLogger(CartService.class);

	@Autowired
	CartDao cartDao;

	@Autowired
	ProductDao productDao;

	@Autowired
	CustomerDao customerDao;

	@Autowired
	CartProductMapperDao cartProductMapperDao;

	@Transactional(rollbackFor = Exception.class)
	public Product addProductToCart(int pid, int custId, int quantity) throws CustomException {

		Product product = null;
		try {
			product = productDao.getProductById(pid);
			if (product == null) {
				throw new CustomException("Product does not exist!");
			}
			final Customer customer = customerDao.getCustomerById(custId);
			if (customer == null) {
				throw new CustomException("Customer does not exist!");
			}
			Cart newCartData = customer.getCart();
			if (newCartData == null) {
				newCartData = new Cart();
			}
			final CartProductMapper existingProduct = newCartData.getCartProductMapper().stream()
					.filter(p -> p.getProduct().getId() == pid).findAny().orElse(null);
			if (existingProduct != null) {
				existingProduct.setQuantity(existingProduct.getQuantity() + 1);
				newCartData.setUpdated(new Date());
			} else {

				final CartProductMapper cartProductMapper = new CartProductMapper();

				newCartData.setCustomer(customer);

				newCartData.getCartProductMapper().add(cartProductMapper);
				product.getCartProductMapper().add(cartProductMapper);

				cartProductMapper.setCart(newCartData);
				cartProductMapper.setProduct(product);
				cartProductMapper.setQuantity(quantity);

				customer.setCart(newCartData);

				customer.setCreated(new Date());
				customer.setUpdated(new Date());
				newCartData.setCreated(new Date());
				newCartData.setUpdated(new Date());

				cartDao.addProductToCart(newCartData);
				cartDao.addCartProducttoMapper(cartProductMapper);
			}

		} catch (final Exception e) {
			logger.error(e);
			throw new CustomException(e.getMessage());
		}
		return product;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean removeCustomerCart(Cart cart) throws Exception {

		return cartDao.removeCart(cart);

	}

	@Transactional(rollbackFor = Exception.class)
	public boolean removeCustomerCart(int custId) throws CustomException {
		final Customer customer = customerDao.getCustomerById(custId);

		if (customer.getCart() == null) {
			throw new CustomException("No cart Exist for this customer");
		}

		final Cart cart = customer.getCart();
		cart.setCustomer(null);
		customer.setCart(null);
		for (final CartProductMapper cartProductMapper : cart.getCartProductMapper()) {
			cartProductMapperDao.removeCartProductMapper(cartProductMapper);
		}

		return cartDao.removeCart(cart);
	}

	@Transactional
	public CartProductMapper increaseQuantity(int custId, int pid) throws CustomException {

		CartProductMapper cartProductMapper = null;
		try {

			final Customer customer = customerDao.getCustomerById(custId);
			final int customerCartId = customer.getCart().getId();
			cartProductMapper = cartDao.increaseQuantity(customerCartId, pid);
		} catch (final CustomException e) {
			logger.error(e);
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}

		return cartProductMapper;
	}

	@Transactional
	public CartProductMapper decreaseQuantity(int custId, int pid) throws CustomException {

		CartProductMapper cartProductMapper = null;
		try {
			final Customer customer = customerDao.getCustomerById(custId);
			final int customerCartId = customer.getCart().getId();
			cartProductMapper = cartDao.decreaseQuantity(customerCartId, pid);
		} catch (final CustomException e) {
			logger.error(e);
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}
		return cartProductMapper;
	}

	@Transactional
	public Cart getCustomerCart(int custId) throws CustomException {
		Cart cart = null;
		try {
			final Customer customer = customerDao.getCustomerById(custId);
			cart = customer.getCart();
			if (cart == null) {
				throw new CustomException("No Cart Exist for this Customer");
			}
		} catch (final CustomException e) {
			logger.error(e);
			throw new CustomException(e.getMessage());
		}

		return cart;

	}

	@Transactional
	public boolean deleteProductFromCart(int custId, int pid) throws CustomException {
		Cart cart = null;
		try {
			final Customer customer = customerDao.getCustomerById(custId);
			cart = customer.getCart();
			if (cart == null) {
				throw new CustomException("No Cart Exist for this Customer");
			}

			final CartProductMapper prod = cart.getCartProductMapper().stream()
					.filter(p -> p.getProduct().getId() == pid).findAny().orElse(null);

			if (prod == null) {
				throw new CustomException("Product does not exist in the cart");
			}

			cartProductMapperDao.removeCartProductMapper(prod);

		} catch (final CustomException e) {
			logger.error(e);
			throw new CustomException(e.getMessage());
		}

		return true;
	}
}
