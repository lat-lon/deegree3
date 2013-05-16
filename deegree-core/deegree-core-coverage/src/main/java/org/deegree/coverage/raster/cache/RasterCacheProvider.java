package org.deegree.coverage.raster.cache;

import java.net.URL;

import javax.xml.bind.JAXBException;

import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.config.ExtendedResourceProvider;
import org.deegree.commons.config.ResourceInitException;
import org.deegree.commons.config.ResourceManager;
import org.deegree.commons.xml.jaxb.JAXBUtils;
import org.deegree.coverage.raster.utils.RasterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RasterCacheProvider implements ExtendedResourceProvider<RasterCache> {

    private static final String CONFIG_NS = "http://www.deegree.org/datasource/coverage/raster/cache";

    private static final String CONFIG_JAXB_PACKAGE = "org.deegree.coverage.persistence.raster.cache.jaxb";

    private static final URL CONFIG_SCHEMA = RasterBuilder.class.getResource( "/META-INF/schemas/datasource/coverage/raster/3.1.0/rasterCache.xsd" );

    private final static Logger LOG = LoggerFactory.getLogger( RasterBuilder.class );

    private DeegreeWorkspace workspace;

    @Override
    public String getConfigNamespace() {
        return CONFIG_NS;
    }

    @Override
    public URL getConfigSchema() {
        return CONFIG_SCHEMA;
    }

    @Override
    public void init( DeegreeWorkspace workspace ) {
        this.workspace = workspace;
    }

    @Override
    public RasterCache create( URL configUrl )
                            throws ResourceInitException {
        try {
            org.deegree.coverage.persistence.raster.cache.jaxb.RasterCache config = (org.deegree.coverage.persistence.raster.cache.jaxb.RasterCache) JAXBUtils.unmarshall( CONFIG_JAXB_PACKAGE,
                                                                                                                                                                           CONFIG_SCHEMA,
                                                                                                                                                                           configUrl,
                                                                                                                                                                           workspace );
            long maxMemSize = config.getMaxMemSize();
            long maxDiskSize = config.getMaxDiskSize();
            LOG.info( "Configure RasterCache with maxMemSize: " + maxMemSize + " and maxDiskSize: " + maxDiskSize );
            RasterCache.setMaxMemAndDiskSize( maxMemSize, maxDiskSize );
        } catch ( JAXBException e ) {
            throw new ResourceInitException( "Error while reading raster cache confuguration.", e );
        }
        return RasterCache.getInstance();
    }

    @Override
    public Class<? extends ResourceManager>[] getDependencies() {
        return new Class[0];
    }

}
