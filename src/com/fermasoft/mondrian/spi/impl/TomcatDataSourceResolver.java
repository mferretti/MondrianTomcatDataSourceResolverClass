package com.fermasoft.mondrian.spi.impl;


import java.util.logging.Level;
import java.util.logging.Logger;

import mondrian.spi.DataSourceResolver;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

public class TomcatDataSourceResolver implements DataSourceResolver {

	private transient Logger logger = Logger.getLogger(TomcatDataSourceResolver.class.getName());
	@Override
	public DataSource lookup(String dataSourceName)
		throws Exception {
			logger.log(Level.INFO,"initializing context ... ");
			Context initialContext = new InitialContext();
			logger.log(Level.INFO,"initializing env context ... ");
			Context envContext = (Context) initialContext.lookup("java:comp/env");
			return (DataSource) envContext.lookup(dataSourceName);
	}

}

