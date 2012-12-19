package org.deegree.coverage.raster.cache;

import org.deegree.commons.config.AbstractResourceManager;
import org.deegree.commons.config.DeegreeWorkspace;
import org.deegree.commons.config.DefaultResourceManagerMetadata;
import org.deegree.commons.config.ResourceManager;

public class RasterCacheManager extends AbstractResourceManager<RasterCache> {

    private RasterCacheManagerMetadata metadata;

    @Override
    public void initMetadata( DeegreeWorkspace workspace ) {
        metadata = new RasterCacheManagerMetadata( workspace );
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ResourceManager>[] getDependencies() {
        return new Class[] {};
    }

    public RasterCacheManagerMetadata getMetadata() {
        return metadata;
    }

    static class RasterCacheManagerMetadata extends DefaultResourceManagerMetadata<RasterCache> {
        RasterCacheManagerMetadata( DeegreeWorkspace workspace ) {
            super( "raster cache", "datasources/coverage/", RasterCacheProvider.class, workspace );
        }
    }

}
