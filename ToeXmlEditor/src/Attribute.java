
public class Attribute {
	String tag;
	String keyword;
	String value;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Attribute() {
		tag = null;
		keyword = null;
		value = null;
	}
	public Attribute(String tag) {
		this.tag = tag;
		keyword = null;
		value = null;
	}

	public String toString() {
	    String s = keyword + "=\"" + value + "\"";
		return s;
	}
}
