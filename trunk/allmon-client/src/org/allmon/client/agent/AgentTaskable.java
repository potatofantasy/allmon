package org.allmon.client.agent;

public interface AgentTaskable {

    public void execute();
    
    public void setParameters(String[] paramsString);
    
}
