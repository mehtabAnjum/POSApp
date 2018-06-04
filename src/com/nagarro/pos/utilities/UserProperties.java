package com.nagarro.pos.utilities;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.nagarro.pos.exception.CustomException;

/**
 * load user properties file
 */
public class UserProperties {
	private UserProperties() {
	}

	public  static final Logger logger = Logger.getLogger(UserProperties.class);
	private static Properties prop;

	public static Properties getProperties() {
		try {
			if (prop == null) {
				prop = new Properties();
				prop.load(UserProperties.class.getResourceAsStream("/com/nagarro/pos/resources/user.properties"));
			}

		} catch (final IOException e) {
			try {
				throw new CustomException(prop.getProperty("EXCEP_USERPROPERTIES"));
			} catch (final CustomException e1) {
				logger.error(e1);
			}
		}
		return prop;
	}
}
