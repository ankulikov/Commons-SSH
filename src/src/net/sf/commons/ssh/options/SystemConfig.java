package net.sf.commons.ssh.options;

public class SystemConfig extends AbstractProperties
{
	
	@Override
	protected Object getSelfProperty(String key)
	{
		return System.getProperty(key);
	}

	@Override
	public String toString()
	{
		return "[JMV Properties]\n" + super.toString();
	}

}