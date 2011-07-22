package net.sf.commons.ssh.options;


public abstract class AbstractProperties implements Properties
{
    protected Properties parent=null;

    protected abstract Object getSelfProperty(String key);

    public Object getProperty(String key)
    {
        Object result = getSelfProperty(key);
        if(result==null && parent!=null)
        {
            result=parent.getProperty(key);
        }
        return result;
    }

    public void includeDefault(Properties configurable)
    {
        if(parent==null)
            parent=configurable;
        else
            parent.includeDefault(configurable);
    }
}