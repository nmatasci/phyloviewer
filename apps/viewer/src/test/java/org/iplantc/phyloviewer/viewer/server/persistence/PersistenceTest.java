package org.iplantc.phyloviewer.viewer.server.persistence;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * creates a shared EntityManagerFactory for persistence tests.
 * TODO try making this a org.junit.runners.Suite and close the entityManagerFactory after the tests are run.
 */
public abstract class PersistenceTest
{
	protected static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory( "org.iplantc.phyloviewer.test" );

	public <T, K> void testPersist(T entity, Method getId)
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) entity.getClass();
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(entity);
		assertTrue(em.contains(entity));
		tx.commit();
		
		em.detach(entity);
		assertFalse(em.contains(entity));
		
		
		Object id = null;
		try
		{
			id = getId.invoke(entity);
		}
		catch(Exception e)
		{
			fail("Unable to get entity Id: " + e.getMessage());
		}
		
		T found = em.find(clazz, id);
		
		assertNotNull(found);
		assertNotSame(entity, found);
		assertEquals(entity, found);
	}
}
