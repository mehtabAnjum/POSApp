package com.nagarro.pos.daoImp;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nagarro.pos.dao.CartProductMapperDao;
import com.nagarro.pos.model.CartProductMapper;

@Repository
public class CartProductMapperImpl implements CartProductMapperDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void removeCartProductMapper(CartProductMapper cartProductMapper) {
		getCurrentSession().remove(cartProductMapper);
	}

}