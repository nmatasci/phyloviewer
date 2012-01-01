package org.iplantc.phyloviewer.viewer.server.persistence;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

public class PersistenceBeanTransformerFactory implements CustomBeanTransformerSpi.Factory {
	
    public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) 
    {
        return new PersistenceBeanTransformer();
    }
    
    public class PersistenceBeanTransformer implements CustomBeanTransformerSpi
    {

    	@Override
    	public <T> T transform(Object arg0, Class<T> arg1, PropertyInfo arg2)
    	{
    		return null;
    	}

    	@Override
    	public boolean isTransformable(Object arg0, Class<?> arg1, PropertyInfo arg2)
    	{
    		return !Persistence.getPersistenceUtil().isLoaded(arg0);
    	}
    }
}