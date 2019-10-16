import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class OsmXmlTree {
	private String inputXml = null;
	private ArrayList<String> xmlLines = new ArrayList<String>();
	private ArrayList<XMLNode> nodes = new ArrayList<XMLNode>();
	private String timestamp = null;
	private String osmTerritoryName = "";
	private String osmTerritoryNumber = "";

	public OsmXmlTree() {
		inputXml = null;
		//Get time stamp
		setTimestamp(getTimeStamp());
	}
	public OsmXmlTree(String inputString) {
		inputXml = inputString;
		//Get time stamp
		setTimestamp(getTimeStamp());
	}

	public void xmlToLines() {
		String sParsedLine = null;

		// If the tree is empty, do nothing
		if (inputXml == null)
			return;
		
		String[] inputSplitNewLine = this.inputXml.split("[\\r\\n]+");
		for(int i=0;i<inputSplitNewLine.length;i++){
			sParsedLine = clean(inputSplitNewLine[i]);	
			this.xmlLines.add(sParsedLine);
			//Set territory number & name from input.
			if (sParsedLine.startsWith("<tag k=\"name")) {
				String keyword = "v";
				String value = this.pickAttributeValue(sParsedLine, keyword);
				osmTerritoryName = value;
				continue;
			} else if (sParsedLine.startsWith("<tag k=\"number")) {
				String keyword = "v";
				String value = this.pickAttributeValue(sParsedLine, keyword);
				osmTerritoryNumber = value;
				continue;
			}
		}
		return;
	}
	
	public void xmlToLines(String filter) {
		String sParsedLine = null;

		// If the tree is empty, do nothing
		if (inputXml == null)
			return;
		
		String[] inputSplitNewLine = this.inputXml.split("[\\r\\n]+");
		for(int i=0;i<inputSplitNewLine.length;i++){
			sParsedLine = clean(inputSplitNewLine[i]);
			if (sParsedLine.startsWith(filter)) {
				this.xmlLines.add(sParsedLine);	
			}
			//Set territory number & name from input.
			if (sParsedLine.startsWith("<tag k=\"name")) {
				String keyword = "v";
				String value = this.pickAttributeValue(sParsedLine, keyword);
				osmTerritoryName = value;
				continue;
			} else if (sParsedLine.startsWith("<tag k=\"number")) {
				String keyword = "v";
				String value = this.pickAttributeValue(sParsedLine, keyword);
				osmTerritoryNumber = value;
				continue;
			}
		}
		return;
	}

	public void reverseXmlLines() {
		ArrayList<String>reversedLines = new ArrayList<String>();
		// reverse lines
		for (int i = this.xmlLines.size() - 1; i >= 0; i--) {
			reversedLines.add(this.xmlLines.get(i));
		}
		//replace the original lines with reversed lines  
		setXmlLines(reversedLines);
		
		return ;
	}
	
	public String clean(String originalXmlStr) {
		String regex, updatedXml;
		String sParsed = null;

		// 1. remove all white space preceding a begin element tag:
		regex = "[\\n\\s]+(\\<[^/])";
		updatedXml = originalXmlStr.replaceAll( regex, "$1" );

		// 2. remove all white space following an end element tag:
		regex = "(\\</[a-zA-Z0-9-_\\.:]+\\>)[\\s]+";
		updatedXml = updatedXml.replaceAll( regex, "$1" );

		// 3. remove all white space following an empty element tag
		// (<some-element xmlns:attr1="some-value".... />):
		regex = "(/\\>)[\\s]+";
		updatedXml = updatedXml.replaceAll( regex, "$1" );
		
		sParsed = updatedXml.trim();

		return sParsed;
	}
	
	public void addNode(XMLNode node) {
		nodes.add(node);
	}
	
	public void parse() {
		
		for (int i = 0; i < this.xmlLines.size(); i++) {
			String xmlLine = this.xmlLines.get(i);
			
			//Vakio <?xml version="1.0" encoding="UTF-8"?>
			String tag = "?xml";
			String filter = "<" + tag;
			if (xmlLine.startsWith(filter)) {
				parseXmlHdr(tag, xmlLine);
				continue;
			}

			//Vakio <osm version="0.6" generator="Online area editor 0.1.20130730">
			tag = "osm";
			filter = "<" + tag;
			if (xmlLine.startsWith(filter)) {
				parseOsmHdr(tag, xmlLine);
				continue;
			}
			
			//<node id="-1" visible="true" timestamp="2016-09-27T15:55:23Z" lat="62.0083652" lon="21.6347081" version="1"/>
			tag = "node";
			filter = "<" + tag;
			if (xmlLine.startsWith(filter)) {
				parseNode(tag, xmlLine);
				continue;
			}

			// <way timestamp="2016-09-27T15:55:23Z" id="-419" visible="true" version="1">
			tag = "way";
			filter = "<" + tag;
			if (xmlLine.startsWith(filter)) {
				parseWay(tag, xmlLine);
				continue;
			}
			
			// <tag k="name" v="Honkajoki (38950) Tarkistettu"/>
			tag = "tag";
			filter = "<" + tag;
			if (xmlLine.startsWith(filter)) {
				parseTag(tag, xmlLine);
				continue;
			}

			//  <nd ref="-1"/>
			tag = "nd";
			filter = "<" + tag;
			if (xmlLine.startsWith(filter)) {
				parseNd(tag, xmlLine);
				continue;
			}

		    //</way>
			if (xmlLine.startsWith("</way")) {
				tag = "/way";
				XMLNode osmNode = new XMLNode(tag);
				osmNode.setPrefix("  "); // kaksi välilyöntiä sisennys
				//Add line to node tree
				addNode(osmNode);
				continue;
			}
			
		    //</osm>
			if (xmlLine.startsWith("</osm")) {
				tag = "/osm";
				XMLNode osmNode = new XMLNode(tag);
				//Add line to node tree
				addNode(osmNode);
				continue;
			}
		}
		return;			
	}
	
	public void parseXmlHdr(String tag, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setOpen(true);
		
		//version="1.0" 
		String keyword = "version";
		String value = this.pickAttributeValue(xmlLine, keyword);
		Attribute attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//encoding="UTF-8"?>
		keyword = "encoding";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}

	public void parseOsmHdr(String tag, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setOpen(true);
		
		//version="0.6" 
		String keyword = "version";
		String value = this.pickAttributeValue(xmlLine, keyword);
		Attribute attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//generator="Online area editor 0.1.20130730"
		keyword = "generator";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}

	public void parseNode(String tag, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setPrefix("  "); // kaksi välilyöntiä sisennys
		
		//id="-1" 
		String keyword = "id";
		String value = this.pickAttributeValue(xmlLine, keyword);
		Attribute attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//visible="true" 
		keyword = "visible";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//timestamp="2016-12-19T18:28:13Z" 
		keyword = "timestamp";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//lat="62.20718060304574" 
		keyword = "lat";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//lon="22.2056677191543" 
		keyword = "lon";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//version="1"
		keyword = "version";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//Add line to node tree
		addNode(osmNode);
	}
	
	public void parseNode(String tag, String id, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setPrefix("  "); // kaksi välilyöntiä sisennys
		
		//id="-1" 
		Attribute attr = populateAttribute(tag, "id", id);
		osmNode.addAttribute(attr);

		//visible="true" 
		String keyword = "visible";
		String value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//timestamp="2016-12-19T18:28:13Z" 
		attr = populateAttribute(tag, "timestamp", this.timestamp);
		osmNode.addAttribute(attr);
		
		//lat="62.20718060304574" 
		keyword = "lat";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//lon="22.2056677191543" 
		keyword = "lon";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//version="1"
		keyword = "version";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);
		
		//Add line to node tree
		addNode(osmNode);
	}

	public void parseWay(String tag, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setOpen(true);
		osmNode.setPrefix("  "); // kaksi välilyöntiä sisennys
		
		//timestamp="2016-09-27T15:55:23Z" 
		String keyword = "timestamp";
		String value = this.pickAttributeValue(xmlLine, keyword);
		Attribute attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//id="-419"
		keyword = "id";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//visible="true"
		keyword = "visible";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//version="1"
		keyword = "version";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}

	public void parseTag(String tag, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setPrefix("    "); // neljä välilyöntiä sisennystä
		
		// k="name" 
		String keyword = "k";
		String value = this.pickAttributeValue(xmlLine, keyword);
		Attribute attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		// v="Honkajoki (38950) Tarkistettu"/>
		keyword = "v";
		value = this.pickAttributeValue(xmlLine, keyword);
		attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		if (xmlLine.startsWith("<tag k=\"name")) {
			osmTerritoryName = value;
		} else if (xmlLine.startsWith("<tag k=\"number")) {
			osmTerritoryNumber = value;
		}
		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}

	public void parseNd(String tag, String xmlLine) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setPrefix("    "); // neljä välilyöntiä sisennystä
		
		// ref="-1" 
		String keyword = "ref";
		String value = this.pickAttributeValue(xmlLine, keyword);
		Attribute attr = populateAttribute(tag, keyword, value);
		osmNode.addAttribute(attr);

		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}
	
	public String pickAttributeValue(String xmlLine, String keyword) {
		//Get value
		int ind = xmlLine.indexOf(keyword);
		int beginIndex = xmlLine.indexOf("\"", ind+keyword.length()) + 1;
		int endIndex = xmlLine.indexOf("\"", beginIndex);
		String value = xmlLine.substring(beginIndex, endIndex);
		
		return value;
	}

	public Attribute populateAttribute(String tag, String keyword, String value) {
		Attribute attr = new Attribute(tag);

		//Set keyword and value
		attr.setKeyword(keyword);
		attr.setValue(value);
		return attr;
	}
	
	public void buildOsmTree() {
		Integer nodeIndex = 0;
		String filter = "<node";
		int nodeCount = this.xmlLines.size();
		
		for (int i = 0; i < nodeCount; i++) {
			String xmlLine = this.xmlLines.get(i);
			
			if (xmlLine.startsWith(filter)) {
				nodeIndex--;
				if (nodeIndex == -1) { // first node
					//Add constant header nodes
					buildNode("?xml", null);
					buildNode("osm", null);
				}
			}
			parseNode("node", nodeIndex.toString(), xmlLine);
		}
		
		if (nodeCount > 0) {
			nodeIndex--;
			buildNode("way", nodeIndex.toString());

			buildTag("tag", "name", osmTerritoryName);
			buildTag("tag", "number", osmTerritoryNumber);
			
			nodeIndex = 0;
			for (int i = 0; i < nodeCount; i++) {
				nodeIndex--;
				buildNode("nd", nodeIndex.toString());
			}

			//  <nd ref="-1"/>
			nodeIndex = -1;
			buildNode("nd", nodeIndex.toString());

			buildTag("tag", "area", "yes");
		    //</way>
			XMLNode osmNode = new XMLNode("/way");
			osmNode.setPrefix("  "); // kaksi välilyöntiä sisennys
			//Add line to node tree
			addNode(osmNode);
			
		    //</osm>
			osmNode = new XMLNode("/osm");
			//Add line to node tree
			addNode(osmNode);
		}
		return;			
	}

	public void buildNode(String tag, String id) {
		XMLNode osmNode = new XMLNode(tag);
		
		switch (tag) {
		case "?xml":
			osmNode.setOpen(true);
			
			//version="1.0" 
			Attribute attr = populateAttribute(tag, "version", "1.0");
			osmNode.addAttribute(attr);

			//encoding="UTF-8"
			attr = populateAttribute(tag, "encoding", "UTF-8");
			osmNode.addAttribute(attr);
			break;
		case "osm":
			osmNode.setOpen(true);
			
			//version="0.6" 
			attr = populateAttribute(tag, "version", "0.6");
			osmNode.addAttribute(attr);

			//generator="Online area editor 0.1.20130730"
			attr = populateAttribute(tag, "generator", "Online area editor 0.1.20130730");
			osmNode.addAttribute(attr);
			break;
		case "way":
			osmNode.setOpen(true);
			osmNode.setPrefix("  "); // kaksi välilyöntiä sisennys
			
			//timestamp 
			attr = populateAttribute(tag, "timestamp", this.timestamp);
			osmNode.addAttribute(attr);

			//id
			attr = populateAttribute(tag, "id", id);
			osmNode.addAttribute(attr);

			//visible="true"
			attr = populateAttribute(tag, "visible", "true");
			osmNode.addAttribute(attr);

			//version=
			attr = populateAttribute(tag, "version", "1");
			osmNode.addAttribute(attr);
			break;
		case "nd":
			osmNode.setPrefix("    "); // neljä välilyöntiä sisennystä
			
			// ref 
			attr = populateAttribute(tag, "ref", id);
			osmNode.addAttribute(attr);
			break;
		default:
			break;
		}

		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}
	
	public void buildTag(String tag, String kValue, String vValue) {
		XMLNode osmNode = new XMLNode(tag);
		osmNode.setPrefix("    "); // neljä välilyöntiä sisennystä
		
		// k="name" 
		Attribute attr = populateAttribute(tag, "k", kValue);
		osmNode.addAttribute(attr);

		// v="Honkajoki (38950) Tarkistettu"/>
		attr = populateAttribute(tag, "v", vValue);
		osmNode.addAttribute(attr);

		//Add line to node tree
		addNode(osmNode);
		
		return;			
	}
	
	//Getters and setters
	public String getInputXml() {
		return inputXml;
	}
	public void setInputXml(String inputXml) {
		this.inputXml = inputXml;
	}
	public ArrayList<String> getXmlLines() {
		return xmlLines;
	}
	public void setXmlLines(ArrayList<String> xmlLines) {
		this.xmlLines = xmlLines;
	}
	public ArrayList<XMLNode> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<XMLNode> nodes) {
		this.nodes = nodes;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	private String getTimeStamp() {
		String t = null;
		Date now = new Date();

		// create time zone object     
		TimeZone timezone = TimeZone.getDefault();
		   
        // Get calendar set to current date and time
        Calendar cal = Calendar.getInstance(timezone);  
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        dateFormat.setCalendar(cal);  //explicitly set the calendar into the date formatter

        t = dateFormat.format(now);
		return t;
	}
	public String getOsmTerritoryName() {
		return osmTerritoryName;
	}
	public void setOsmTerritoryName(String osmTerritoryName) {
		this.osmTerritoryName = osmTerritoryName;
	}
	public String getOsmTerritoryNumber() {
		return osmTerritoryNumber;
	}
	public void setOsmTerritoryNumber(String osmTerritoryNumber) {
		this.osmTerritoryNumber = osmTerritoryNumber;
	}

	
	public String parseCoordinates(String sCoordinates) {
		ArrayList<String> lat = new ArrayList<String>();
		ArrayList<String> lon = new ArrayList<String>();
		String osmXMLstring = "";
		
		//Halkaistaan sCoordinates
		String[] coordinateParts = sCoordinates.split(" ");
		for (int i = 0; i < coordinateParts.length; i++) {
			String[] coordinates = coordinateParts[i].split(",");

			// Convert coordinates to numbers with 12 decimals. 
			try {
				BigDecimal bdLon = new BigDecimal(coordinates[0]);
				BigDecimal bdLat = new BigDecimal(coordinates[1]);
				
				//Round coordinates to 12 decimals.
				bdLon = bdLon.setScale(12, BigDecimal.ROUND_HALF_UP);
				bdLat = bdLat.setScale(12, BigDecimal.ROUND_HALF_UP);

				// Set formatter to strip trailing zeroes and 
				// use decimal point instead of comma.
				DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance();
				decimalSymbols.setDecimalSeparator('.');
				DecimalFormat df = new DecimalFormat("0.00##########", decimalSymbols);
				
			    String formattedLon = df.format(bdLon.stripTrailingZeros());
				String formattedLat = df.format(bdLat.stripTrailingZeros());
				
				lon.add(formattedLon);
				lat.add(formattedLat);
			} catch (Exception ex) {
				System.out.print(ex.toString());
			}
		}
		
		//Make Osm structure
		osmXMLstring = constructOsmString(lat, lon);
		
		return osmXMLstring;
	}
	
	private String constructOsmString(ArrayList<String> latitudes, ArrayList<String> longitudes) {
		XMLNode osmLine = new XMLNode();
		Attribute attr = new Attribute();
		String timestamp = getTimeStamp(); 
		Integer nodeID = 0;
		String osmXMLstring = "";
		
		//Vakio <?xml version="1.0" encoding="UTF-8"?>
		osmLine.setTag("?xml");
		osmLine.setOpen(true);
		
		attr = populateAttribute(osmLine.getTag(), "version", "1.0");
		osmLine.addAttribute(attr);
		
		attr = populateAttribute(osmLine.getTag(), "encoding", "UTF-8");
		osmLine.addAttribute(attr);

		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();
		
		//Vakio <osm version="0.6" generator="Online area editor 0.1.20130730">
		osmLine.setTag("osm");
		osmLine.setOpen(true);
		
		attr = populateAttribute(osmLine.getTag(), "version", "0.6");
		osmLine.addAttribute(attr);

		attr = populateAttribute(osmLine.getTag(), "generator", "Online area editor 0.1.20130730");
		osmLine.addAttribute(attr);
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();

		//<node id="-1" visible="true" timestamp="2016-09-27T15:55:23Z" lat="62.0083652" lon="21.6347081" version="1"/>
		for (int i = 0; i < latitudes.size(); i++) {
			osmLine.setPrefix("  "); // yksi sisennys
			osmLine.setTag("node");
			
			nodeID = -1*(i+1);
			attr = populateAttribute(osmLine.getTag(), "id", nodeID.toString());
			osmLine.addAttribute(attr);

			attr = populateAttribute(osmLine.getTag(), "visible", "true");
			osmLine.addAttribute(attr);

			attr = populateAttribute(osmLine.getTag(), "timestamp", timestamp);
			osmLine.addAttribute(attr);

			attr = populateAttribute(osmLine.getTag(), "lat", latitudes.get(i));
			osmLine.addAttribute(attr);

			attr = populateAttribute(osmLine.getTag(), "lon", longitudes.get(i));
			osmLine.addAttribute(attr);

			attr = populateAttribute(osmLine.getTag(), "version", "1");
			osmLine.addAttribute(attr);
			osmXMLstring = osmXMLstring + osmLine.getLine();
			osmLine.clearNode();
		}

		// <way timestamp="2016-09-27T15:55:23Z" id="-419" visible="true" version="1">
		osmLine.setTag("way");
		osmLine.setOpen(true);
		osmLine.setPrefix("  "); // yksi sisennys

		attr = populateAttribute(osmLine.getTag(), "timestamp", timestamp);
		osmLine.addAttribute(attr);

		nodeID = nodeID - 1;
		attr = populateAttribute(osmLine.getTag(), "id", nodeID.toString());
		osmLine.addAttribute(attr);
		
		attr = populateAttribute(osmLine.getTag(), "visible", "true");
		osmLine.addAttribute(attr);

		attr = populateAttribute(osmLine.getTag(), "version", "1");
		osmLine.addAttribute(attr);
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();

		//  <tag k="name" v="Honkajoki (38950) Tarkistettu"/>
		osmLine.setTag("tag");
		osmLine.setPrefix("    "); // kaksi sisennystä

		attr = populateAttribute(osmLine.getTag(), "k", "name");
		osmLine.addAttribute(attr);

		attr = populateAttribute(osmLine.getTag(), "v", "Honkajoki (38950) Tarkistettu");
		osmLine.addAttribute(attr);
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();
		
		//  <tag k="number" v="38950"/>
		osmLine.setTag("tag");
		osmLine.setPrefix("    "); // kaksi sisennystä

		attr = populateAttribute(osmLine.getTag(), "k", "number");
		osmLine.addAttribute(attr);

		attr = populateAttribute(osmLine.getTag(), "v", "38950");
		osmLine.addAttribute(attr);
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();
		
		//<nd ref="-1"/>
		for (int i = 0; i < latitudes.size(); i++) {
			osmLine.setTag("nd");
			osmLine.setPrefix("    "); // kaksi sisennystä

			nodeID = -1*(i+1);
			attr = populateAttribute(osmLine.getTag(), "ref", nodeID.toString());
			osmLine.addAttribute(attr);

			osmXMLstring = osmXMLstring + osmLine.getLine();
			osmLine.clearNode();
		}

		osmLine.setTag("nd");
		osmLine.setPrefix("    "); // kaksi sisennystä

		//<nd ref="-1"/>
		nodeID = -1;
		attr = populateAttribute(osmLine.getTag(), "ref", nodeID.toString());
		osmLine.addAttribute(attr);
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();

		//<tag k="area" v="yes"/>
		osmLine.setTag("tag");
		osmLine.setPrefix("    "); // kaksi sisennystä

		attr = populateAttribute(osmLine.getTag(), "k", "area");
		osmLine.addAttribute(attr);

		attr = populateAttribute(osmLine.getTag(), "v", "yes");
		osmLine.addAttribute(attr);
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();
		
	    //</way>
		osmLine.setTag("/way");
		osmLine.setPrefix("  "); // kaksi sisennystä
		osmXMLstring = osmXMLstring + osmLine.getLine();
		osmLine.clearNode();

		//</osm>
		osmLine.setTag("/osm");
		osmXMLstring = osmXMLstring + osmLine.getLine();
		
		return osmXMLstring;
	}
}
