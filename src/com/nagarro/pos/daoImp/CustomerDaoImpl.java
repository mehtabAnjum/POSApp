package com.nagarro.pos.daoImp;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nagarro.pos.dao.CustomerDao;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Customer;

@Repository
public class CustomerDaoImpl implements CustomerDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public Customer getCustomerById(int custId) throws CustomException {
		return getCurrentSession().get(Customer.class, custId);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Customer> getCustomer(String toSearch) {
		final String hql = "from Customer where email like :keyword or firstName like :keyword or lastName like :keyword or mobile like :keyword";
		final String keyword = toSearch;
		final Query query = getCurrentSession().createQuery(hql);
		query.setParameter("keyword", "%" + keyword + "%");

		return query.getResultList();
	}

}