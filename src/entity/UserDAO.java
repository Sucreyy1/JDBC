package entity;

import java.io.Serializable;

import annotation.Column;
import annotation.ID;
import annotation.Table;

@Table("user")
public class UserDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ID(isAutoIncrement = true, value = "1")
	private Integer id;
	@Column("name")
	private String name;
	@Column("sex")
	private String sex;
	@Column("tel")
	private String tel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
}
