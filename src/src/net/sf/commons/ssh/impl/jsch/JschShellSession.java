/*
 * Copyright 2009-2009 CommonsSSH Project.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.commons.ssh.impl.jsch;

import java.io.*;

import net.sf.commons.ssh.ShellSession;
import net.sf.commons.ssh.utils.AutoflushPipeOutputStream;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
class JschShellSession extends JschAbstractSession implements ShellSession {
    private final PipedInputStream inputStream;

    private final PipedOutputStream outputStream;

    JschShellSession(final ChannelShell session, int soTimeout)
	    throws IOException, JSchException {
	super(session);

	this.inputStream = new PipedInputStream();
	final AutoflushPipeOutputStream out = new AutoflushPipeOutputStream(
		this.inputStream);
	session.setOutputStream(out);
	session.setExtOutputStream(out);

	this.outputStream = new AutoflushPipeOutputStream();
	session.setInputStream(new PipedInputStream(this.outputStream));

	session.connect(soTimeout);
    }

    public InputStream getInputStream() throws IOException {
	log.trace("getInputStream()");

	return inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
	log.trace("getOutputStream()");

	return outputStream;
    }
    
    public void close() throws IOException {
    	log.trace("close()");
    	super.close();
		OutputStream os = getOutputStream();
		if (os != null)
			os.close();
		InputStream is = getInputStream();
		if (is != null)
			is.close();
    }
}