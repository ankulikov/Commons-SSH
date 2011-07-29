/**
 * 
 */
package net.sf.commons.ssh.connector;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.auth.AuthenticationOptions;
import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.connection.AuthenticationException;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.event.Event;
import net.sf.commons.ssh.event.EventListener;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.event.EventTypeFilter;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.event.events.ConnectedEvent;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SftpSessionPropertiesBuilder;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;

import java.util.*;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractConnector extends AbstractContainer<Connection> implements Connector
{

    public AbstractConnector(Properties properties)
    {
        super(properties);
        
    }

    @Override
    protected void configureDefault(Properties properties)
    {
        super.configureDefault(properties);
        includeDefault(InitialPropertiesBuilder.getInstance().getDefault());
        includeDefault(ConnectorPropertiesBuilder.getInstance().getDefault());
        includeDefault(ConnectionPropertiesBuilder.getInstance().getDefault());
        includeDefault(ShellSessionPropertiesBuilder.getInstance().getDefault());
        includeDefault(SftpSessionPropertiesBuilder.getInstance().getDefault());
    }


    public Set<Feature> getSupportedFeatures()
    {
        SupportedFeatures supportedFeatures = this.getClass().getAnnotation(SupportedFeatures.class);
        if(supportedFeatures == null)
            return Collections.EMPTY_SET;
        Feature[] features = supportedFeatures.value();
        if(features == null)
                return Collections.EMPTY_SET;
        return new HashSet<Feature>(Arrays.asList(features));
    }

    @Override
    protected ProducerType getProducerType()
    {
        return ProducerType.CONNECTOR;
    }

	@Override
	public Connection openConnection(String host, int port, AuthenticationOptions auth) throws ConnectionException,AuthenticationException
	{
		Connection connection = createConnection();
		ConnectionPropertiesBuilder.getInstance().setHost(connection, host);
		ConnectionPropertiesBuilder.getInstance().setPort(connection, port);
		ConnectionPropertiesBuilder.getInstance().setAuthenticationOptions(connection, auth);
		
		if(!InitialPropertiesBuilder.getInstance().isAsynchronous(connection))
		{
			//synchronous connect
			connection.connect();
			connection.authenticate();
		}
		else
		{
			connection.addListener(new EventListener()
				{

					@Override
					public void handle(Event event)
					{
						ConnectedEvent connectedEvent = (ConnectedEvent) event;
						if (connectedEvent.getConnection().isConnected())
						{
							try
							{
								connectedEvent.getConnection().authenticate();
							}
							catch (AuthenticationException e)
							{
								log.error("unexpected error, asynchronous method should't throw errors",e);								
							}
						}
					}
				}, new EventTypeFilter(EventType.CONNECTED));
			
			connection.connect();

		}
		
		return connection;
	}

}