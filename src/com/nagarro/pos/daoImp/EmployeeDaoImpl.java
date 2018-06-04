package com.nagarro.pos.daoImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nagarro.pos.dao.EmployeeDao;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.CashDrawer;
import com.nagarro.pos.model.Employee;
import com.nagarro.pos.model.UserSecret;
import com.nagarro.pos.utilities.UserProperties;

@Repository
public class EmployeeDaoImpl implements EmployeeDao {

	@Autowired
	private SessionFactory sessionFactory;
	static Properties prop = UserProperties.getProperties();

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public Employee getEmployeeById(int empId) {
		return getCurrentSession().get(Employee.class, empId);
	}

	@Override
	public Employee getEmp(String email) throws CustomException {
		Employee emp = null;
		final Session session = sessionFactory.getCurrentSession();
		try {
			final Query query = session.createQuery("from Employee where email=:email");
			query.setParameter("email", email);
			emp = (Employee) query.getSingleResult();
		} catch (final Exception e) {
			throw new CustomException(prop.getProperty("GETUSER_DAO_EXCEP"));
		}
		return emp;
	}

	@Override
	public UserSecret getPass(int id) throws CustomException {
		UserSecret userSecret = null;
		final Session session = sessionFactory.getCurrentSession();
		try {
			final Query query = session.createQuery("from UserSecret where id=:id");

			query.setParameter("id", id);
			userSecret = (UserSecret) query.getSingleResult();
		} catch (final Exception e) {
			throw new CustomException(prop.getProperty("GETPASS_DAO_EXCEP"));
		}
		return userSecret;
	}

	@Override
	public void addCashDrawer(CashDrawer cashDrawer) throws CustomException {

		final Session session = sessionFactory.getCurrentSession();
		session.save(cashDrawer);

	}

	@Override
	public void updateEmployee(Employee employee) throws CustomException {

		final Session session = sessionFactory.getCurrentSession();
		session.update(employee);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Employee> getAllEmployees() throws CustomException {

		final Session session = sessionFactory.getCurrentSession();
		List<Employee> empList = new ArrayList<>();

		try {
			final Query query = session.createQuery("from Employee");
			empList = query.getResultList();
		} catch (final Exception e) {
			throw new CustomException(prop.getProperty("GETPASS_DAO_EXCEP"));
		}
		return empList;
	}

}
