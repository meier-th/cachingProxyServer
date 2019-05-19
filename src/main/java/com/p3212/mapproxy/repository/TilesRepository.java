package com.p3212.mapproxy.repository;

import com.p3212.mapproxy.object.TileKey;

import java.util.HashSet;

public class TilesRepository {

    private static HashSet<TileKey> cachedTiles = new HashSet<>();

    public static boolean isCached(TileKey key) {
        return cachedTiles.contains(key);
    }

    public static void addCachedTile(TileKey key) {
        cachedTiles.add(key);
    }

}
