package itest.com.googlecode.icegem.cacheutils.comparator;

import java.io.Serializable;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;


@AutoSerializable(dataSerializerID = 980498520)
@BeanVersion(value = 1)
public class User implements Serializable {
	private static final long serialVersionUID = -14499917940744468L;

	private long id;
	private String name;
	private int age;
	private boolean married;

	public User() {
	}
	
	public User(long id, String name, int age, boolean married) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.married = married;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (married ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (age != other.age)
			return false;
		if (id != other.id)
			return false;
		if (married != other.married)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
