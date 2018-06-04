package com.nagarro.pos.daoImp;

import java.util.List;
import java.util.Properties;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nagarro.pos.dao.ProductDao;
import com.nagarro.pos.model.Product;
import com.nagarro.pos.utilities.UserProperties;

@Repository
public class ProductDaoImpl implements ProductDao {

	@Autowired
	private SessionFactory sessionFactory;
	static Properties prop = UserProperties.getProperties();

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getProductsDB() {
		final Session session = getCurrentSession();
		final Query query = session.createQuery("from Product");

		return query.getResultList();
	}

	@Override
	public Product getProductById(int pid) {
		return getCurrentSession().get(Product.class, pid);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Product> searchProducts(String toSearch) {
		final String hql = "from Product where pcode like :keyword or name like :keyword";
		final String keyword = toSearch;
		final Query query = getCurrentSession().createQuery(hql);
		query.setParameter("keyword", "%" + keyword + "%");

		return query.getResultList();

	}

}
