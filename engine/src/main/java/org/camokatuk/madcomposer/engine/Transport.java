package org.camokatuk.madcomposer.engine;

public interface Transport
{
	void initialize() throws TransportException;

	void shutdown() throws TransportException;
}
