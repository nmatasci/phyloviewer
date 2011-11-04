package org.iplantc.phyloviewer.viewer.server.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * creates a shared EntityManagerFactory for persistence tests.
 * TODO try making this a org.junit.runners.Suite and close the entityManagerFactory after the tests are run.
 */
public abstract class PersistenceTest
{
	protected static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory( "org.iplantc.phyloviewer.test.postgres" );
}
