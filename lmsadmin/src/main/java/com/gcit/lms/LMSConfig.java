package com.gcit.lms;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan
public class LMSConfig{
	
	public String driver = "com.mysql.jdbc.Driver";
	public String url = "jdbc:mysql://gcittest.cnv3b0njilzq.us-east-1.rds.amazonaws.com:3306/library?useSSL=false";
	public String user = "gcittest";
	public String password = "gcittest";
	
	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(password);
		return ds;
	}
	
	@Bean
//	@Qualifier(value = "template")
	public JdbcTemplate template() {
		return new JdbcTemplate(dataSource());
	}	
}
