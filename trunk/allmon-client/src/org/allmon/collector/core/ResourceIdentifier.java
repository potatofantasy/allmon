package org.allmon.collector.core;

public class ResourceIdentifier 
{
	private String resourceType;
	private String resourceSubType;
	private String resourceName;
	private String invokerId;
	private Class[] parameterTypes;
	private Object[] parameterValues;
	
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourceSubType() {
		return resourceSubType;
	}
	public void setResourceSubType(String resourceSubType) {
		this.resourceSubType = resourceSubType;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getInvokerId() {
		return invokerId;
	}
	public void setInvokerId(String invokerId) {
		this.invokerId = invokerId;
	}
	public Class[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameterValues() {
		return parameterValues;
	}
	public void setParameterValues(Object[] parameterValues) {
		this.parameterValues = parameterValues;
	}
}

