package org.allmon.client.agent.advices;

import javax.servlet.http.HttpServletRequest;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

class HttpServletRequestUtil {

	private static final XStream XSTREAM = new XStream(new JsonHierarchicalStreamDriver()); //new JettisonDriver());
	
	private HttpServletRequest request;
	
	public HttpServletRequestUtil(HttpServletRequest request) {
		this.request = request;
	}
		
	public Object getUserObject(String sessionUserAttributeKey) {
		return  request.getSession().getAttribute(sessionUserAttributeKey);
	}
	
	public String getUserObjectString(String sessionUserAttributeKey) {
		try {
			return getUserObject(sessionUserAttributeKey).toString();
    	} catch (Exception ex) {
    		return "user-not-found";
    	}
	}
	
	public String getUserObjectSerializedString(String sessionUserAttributeKey) {
    	try {
			return XSTREAM.toXML(getUserObject(sessionUserAttributeKey));
    	} catch (Exception ex) {
    		return "user-not-found";
    	}
	}
		
}
