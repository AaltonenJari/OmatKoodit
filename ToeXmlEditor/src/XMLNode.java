import java.util.ArrayList;

public class XMLNode {
	private String tag = null;
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private boolean open = false;
	private String prefix = null;
	
	public XMLNode() {
	}

	public XMLNode(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public ArrayList<Attribute> getAttributes() {
		return this.attributes;
	}
	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}
	public boolean isOpen() {
		return this.open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public String getPrefix() {
		return this.prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public void addAttribute(Attribute a) {
		attributes.add(a);
	}
	public void clearNode() {
		this.tag = "";
		this.open = false;
		this.setPrefix(null);
		this.attributes.clear();
	}
	public String toString() {
	    String s = null;
	    if (tag == null)
	    	return null;
	    //Start tag
	    s = "<" + tag;
	    
	    if (tag.startsWith("/")) { // End tag
		    s = s + ">";
	    } else {
		    //attributes
		    for (int i=0; i < attributes.size(); i++) {
		    	s = s + " " + attributes.get(i).toString();
		    }
		    
		    //end tag
		    if (this.isOpen()) {
		    	if (tag.startsWith("?"))
		    		s = s + "?";
			    s = s + ">";
		    }
		    else
			    s = s + "/>";
	    }
	    
		return s;
	}
	public String getLine() {
		String s = "";
		if (prefix != null)
			s = prefix;
		
	    s = s + this.toString();
	    s = s + "\n";
		return s;
	}
}
