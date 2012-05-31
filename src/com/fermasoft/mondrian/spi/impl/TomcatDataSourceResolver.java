
package com.fermasoft.mondrian.spi.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import mondrian.spi.DataSourceResolver;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TomcatDataSourceResolver implements DataSourceResolver {

	private transient Logger logger =
		Logger.getLogger(TomcatDataSourceResolver.class.getName());
	
	private static Context initialContext ;
	static{
		Logger.getLogger(TomcatDataSourceResolver.class.getName()).log(Level.INFO, "initializing context ... ");
		try {
			initialContext = new InitialContext();
		}
		catch (NamingException e) {
			Logger.getLogger(TomcatDataSourceResolver.class.getName()).log(Level.SEVERE, "error getting context",e);
		}
	}
	
	@Override
	public DataSource lookup(String dataSourceName)
		throws Exception {

//		logger.log(Level.INFO, "initializing env context ... ");
//		Context envContext = (Context) initialContext.lookup("java:comp/env");
//		return (DataSource) envContext.lookup(dataSourceName);
        return (DataSource)_lookup(initialContext,dataSourceName);
    }

	private Object _lookup(Context ctx, String location)
		throws NamingException {

		logger.log(Level.INFO, "Lookup " + location);
		Object obj = null;
		try {
			obj = ctx.lookup(location);
		}
		catch (NamingException n1) {
			// java:comp/env/ObjectName to ObjectName
			if (location.indexOf("java:comp/env/") != -1) {
				try {
					String newLocation =
						StringUtil.replace(location, "java:comp/env/", "");
					logger.log(Level.CONFIG, n1.getMessage());
					logger.log(Level.CONFIG, "Attempt " + newLocation);
					obj = ctx.lookup(newLocation);
				}
				catch (NamingException n2) {
					// java:comp/env/ObjectName to java:ObjectName
					String newLocation =
						StringUtil.replace(location, "comp/env/", "");
					logger.log(Level.CONFIG, n2.getMessage());
					logger.log(Level.CONFIG, "Attempt " + newLocation);
					obj = ctx.lookup(newLocation);
				}
			}

			// java:ObjectName to ObjectName
			else if (location.indexOf("java:") != -1) {
				try {
					String newLocation =
						StringUtil.replace(location, "java:", "");
					logger.log(Level.CONFIG, n1.getMessage());
					logger.log(Level.CONFIG, "Attempt " + newLocation);
					obj = ctx.lookup(newLocation);
				}
				catch (NamingException n2) {
					// java:ObjectName to java:comp/env/ObjectName
					String newLocation =
						StringUtil.replace(location, "java:", "java:comp/env/");
					logger.log(Level.CONFIG, n2.getMessage());
					logger.log(Level.CONFIG, "Attempt " + newLocation);
					obj = ctx.lookup(newLocation);
				}
			}
			// ObjectName to java:ObjectName
			else if (location.indexOf("java:") == -1) {
				try {
					String newLocation = "java:" + location;
					logger.log(Level.CONFIG, n1.getMessage());
					logger.log(Level.CONFIG, "Attempt " + newLocation);
					obj = ctx.lookup(newLocation);
				}
				catch (NamingException n2) {
					// ObjectName to java:comp/env/ObjectName
					String newLocation = "java:comp/env/" + location;
					logger.log(Level.CONFIG, n2.getMessage());
					logger.log(Level.CONFIG, "Attempt " + newLocation);
					obj = ctx.lookup(newLocation);
				}
			}
			else {
				throw new NamingException();
			}
		}
		return obj;
	}
	static class StringUtil {

		public static final String BLANK = "";

		public static String replace(String s, String oldSub, String newSub) {
			return replace(s, oldSub, newSub, 0);
		}


		public static String replace(

		String s, String oldSub, String newSub, int fromIndex) {

			if ((s == null) || (oldSub == null) || (newSub == null)) {
				return null;
			}

			if (oldSub.equals(BLANK)) {
				return s;
			}

			int y = s.indexOf(oldSub, fromIndex);

			if (y >= 0) {
				StringBuffer sb = new StringBuffer();

				int length = oldSub.length();
				int x = 0;

				while (x <= y) {
					sb.append(s.substring(x, y));
					sb.append(newSub);

					x = y + length;
					y = s.indexOf(oldSub, x);
				}

				sb.append(s.substring(x));

				return sb.toString();
			}
			else {
				return s;
			}
		}
	}
}
