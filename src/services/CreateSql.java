package services;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import annotation.Column;
import annotation.ID;
import annotation.Table;
import entity.UserDAO;

public class CreateSql {

	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		while(true){
			UserDAO userDAO = new UserDAO();
			System.out.println("-------请输入信息--------");
			System.out.print("姓名：");
			String name = sc.next();
			userDAO.setName(name);
			System.out.print("性别：");
			String sex = sc.next();
			userDAO.setSex(sex);
			System.out.print("电话：");
			String tel = sc.next();
			userDAO.setTel(tel);
			new CreateSql().createSQL(userDAO);
			System.out.println("保存成功....Y[继续添加]");
			String next = sc.next();
			if(!next.equalsIgnoreCase("y")){
				return;
			}
		}
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
