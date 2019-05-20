package com.p3212.mapproxy.repository;

import com.p3212.mapproxy.object.TileKey;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Future;

public class TilesRepository {

    // 'cachedTiles' stores 'TileKey' objects of tiles that were already cached on disk
    private static HashSet<TileKey> cachedTiles = new HashSet<>();

    // 'futures' stores Future objects of tiles that are currently being loaded
    // from openStreetsMap
    // If multiple requests for the same tile were received simultaneously, than
    // only one thread will send a request to openStreetsMap
    // while all other will wait for a Future here
    private static HashMap<TileKey, Future<BufferedImage>> futures = new HashMap<>();

    // Checks if tile was already cached
    public static boolean isCached(TileKey key) {
        return cachedTiles.contains(key);
    }

    // Adds info about new cached tile
    public static synchronized void addCachedTile(TileKey key) {
        cachedTiles.add(key);
    }

    // Checks if tile is being currently loaded
    public static boolean isTileBeingLoaded(TileKey key) {
        return futures.containsKey(key);
    }

    // Adds info about tile being currently loaded
    public static synchronized void addLoadingTile(TileKey key, Future<BufferedImage> future) {
        futures.put(key, future);
    }

    // Removes info about tile being currently loaded
    public static synchronized void removeLoaded(TileKey key) {
        futures.remove(key);
    }

    // Returns a Future for a specific TileKey
    public static Future<BufferedImage> getFuture(TileKey key) {
        return futures.get(key);
    }

}
