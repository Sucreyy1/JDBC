package services;

import java.lang.reflect.Field;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import annotation.Column;
import annotation.ID;
import annotation.Table;
import entity.UserDAO;

public class CreateSql {

	public static void main(String[] args) {
		UserDAO userDAO = new UserDAO();
		userDAO.setName("张三");
		userDAO.setSex("1");
		userDAO.setTel("110");
		new CreateSql().createSQL(userDAO);
	}

	public void createSQL(UserDAO userDAO) {
		//insert into 表名(name,...) values(?,...)
		StringBuffer buffer = new StringBuffer();
		buffer.append("insert into ");
		// 获取UserDAO的Class的对象
		
		Class<? extends UserDAO> clazz = userDAO.getClass();
		// 获取类注解(表名)
		Table annotation = clazz.getAnnotation(Table.class);
		String value = annotation.value();
		buffer.append(value + "(");
		// 获取属性注解(列名)
		Field[] declaredFields = clazz.getDeclaredFields();
		int length =0;
		for (Field field : declaredFields) {
			//获取权限
			field.setAccessible(true);
			//判断是否为ID和Column的注解
			if (field.isAnnotationPresent(ID.class)|| field.isAnnotationPresent(Column.class)) {
				if (field.isAnnotationPresent(ID.class)) {
					ID annotation2 = field.getAnnotation(ID.class);
					//判断是否自增长，是跳过，否加入列名
					if (!annotation2.isAutoIncrement()) {
						buffer.append(field.getName()).append(",");
						length += 1;
					} else {
						continue;
					}
				} else {
					Column annotation2 = field.getAnnotation(Column.class);
					buffer.append(annotation2.value()).append(",");
					length += 1;
				}
			} else {
				continue;
			}
		}
		//去除最后一个逗号
		buffer.deleteCharAt(buffer.length()-1).append(")").append(" ").append("values(");
		for(int i =0;i<length;i++){
			buffer.append("?,");
		}
		buffer.deleteCharAt(buffer.length()-1).append(")");
		String sql = buffer.toString();
		
		PreparedStatement statement = null;
		Connection connection = null;
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			String password="606999";
			String user="root";
			String url="jdbc:mysql://localhost:3306/jdbc";
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.prepareStatement(sql);
			statement.setString(1, userDAO.getName());
			statement.setString(2, userDAO.getSex());
			statement.setString(3, userDAO.getTel());
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(statement!=null){
					statement.close();
				}
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
