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
		userDAO.setName("����");
		userDAO.setSex("1");
		userDAO.setTel("110");
		new CreateSql().createSQL(userDAO);
	}

	public void createSQL(UserDAO userDAO) {
		//insert into ����(name,...) values(?,...)
		StringBuffer buffer = new StringBuffer();
		buffer.append("insert into ");
		// ��ȡUserDAO��Class�Ķ���
		
		Class<? extends UserDAO> clazz = userDAO.getClass();
		// ��ȡ��ע��(����)
		Table annotation = clazz.getAnnotation(Table.class);
		String value = annotation.value();
		buffer.append(value + "(");
		// ��ȡ����ע��(����)
		Field[] declaredFields = clazz.getDeclaredFields();
		int length =0;
		for (Field field : declaredFields) {
			//��ȡȨ��
			field.setAccessible(true);
			//�ж��Ƿ�ΪID��Column��ע��
			if (field.isAnnotationPresent(ID.class)|| field.isAnnotationPresent(Column.class)) {
				if (field.isAnnotationPresent(ID.class)) {
					ID annotation2 = field.getAnnotation(ID.class);
					//�ж��Ƿ��������������������������
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
		//ȥ�����һ������
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
