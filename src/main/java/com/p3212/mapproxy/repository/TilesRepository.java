package com.p3212.mapproxy.repository;

import com.p3212.mapproxy.object.TileKey;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Future;

public class TilesRepository {

    private static HashSet<TileKey> cachedTiles = new HashSet<>();
    private static HashMap<TileKey, Future<BufferedImage>> futures = new HashMap<>();

    public static boolean isCached(TileKey key) {
        return cachedTiles.contains(key);
    }

    public static synchronized void addCachedTile(TileKey key) {
        cachedTiles.add(key);
    }

    public static boolean isPileBeingLoaded(TileKey key) {
        return futures.containsKey(key);
    }

    public static synchronized void addLoadingPile(TileKey key, Future<BufferedImage> future) {
        futures.put(key, future);
    }

    public static synchronized void removeLoaded(TileKey key) {
        futures.remove(key);
    }

    public static Future<BufferedImage> getFuture(TileKey key) {
        return futures.get(key);
    }

}
