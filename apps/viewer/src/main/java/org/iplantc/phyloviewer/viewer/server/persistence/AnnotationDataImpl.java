package org.iplantc.phyloviewer.viewer.server.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadataImpl;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.server.AnnotationData;

/**
 * JPA implementation of AnnotationData
 */
public class AnnotationDataImpl implements AnnotationData
{
	private EntityManagerFactory emf;
	
	/**
	 * Create a new AnnotationDataImpl
	 * @param emf the EntityManagerFactory used to interact with the persistence unit.
	 */
	public AnnotationDataImpl(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@Override
	public List<AnnotationMetadata> getAnnotationMetadata(RemoteTree tree)
	{
		List<AnnotationMetadata> list = new ArrayList<AnnotationMetadata>();
		EntityManager em = emf.createEntityManager();
		
		String query = "SELECT DISTINCT a.property, a.datatype"
				+ " FROM AnnotatedNode n, IN (n.annotations) a "
				+ " WHERE n.topology.rootNode.id = :rootNodeID"; 
		Query q = em.createQuery(query);
		q.setParameter("rootNodeID", tree.getRootNode().getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();
		
		for (Object[] result : results)
		{
			String name = (String) result[0];
			Class<?> clazz = getClassFor((String)result[1]);
			//TODO get bounds for numeric data and make a NumericAnnotationMetadata for it
			AnnotationMetadata metadata = new AnnotationMetadataImpl(name, clazz);
			list.add(metadata);
		}
		
		em.close();
		return list;
	}

	@Override
	public List<AnnotationMetadata> getAnnotationMetadata(RemoteTree tree, String propertyOrRel)
	{
		List<AnnotationMetadata> filteredList = new ArrayList<AnnotationMetadata>();
		
		List<AnnotationMetadata> list = getAnnotationMetadata(tree);
		for (AnnotationMetadata element : list) {
			if (element.getName().equals(propertyOrRel)) {
				filteredList.add(element);
			}
		}
		
		return filteredList;
	}

	private Class<?> getClassFor(String datatype)
	{
		if (datatype == null || datatype.isEmpty())
		{
			return String.class;
		}
		
		Class<?> clazz = null;
		datatype = datatype.trim();
		datatype = datatype.replace("xsd:", "");
		datatype = datatype.substring(0, 1).toUpperCase() + datatype.substring(1).toLowerCase();
		
		try {
			clazz = Class.forName("java.lang." + datatype);
		}
		catch (ClassNotFoundException e)
		{
			//fail
			clazz = String.class;
		}
		
		return clazz;
	}
}
