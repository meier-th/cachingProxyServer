package com.p3212.mapproxy.repository;

import com.p3212.mapproxy.object.TileKey;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class TilesRepository {

    private HashSet<TileKey> cachedTiles;

    public boolean isCached(TileKey key) {
        return this.cachedTiles.contains(key);
    }

    public void addCachedTile(TileKey key) {
        this.cachedTiles.add(key);
    }

}
