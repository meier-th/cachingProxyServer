package com.p3212.mapproxy.repository;

import static org.junit.Assert.*;

import com.p3212.mapproxy.object.TileKey;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TilesRepositoryTests {

    @Test
    // Tests if tiles are cached correctly
    public void testCachingTiles() {
        TileKey key = new TileKey();
        //Tile wasn't added yet
        assertFalse(TilesRepository.isCached(key));
        TilesRepository.addCachedTile(key);
        //Tile was added
        assertTrue(TilesRepository.isCached(key));
    }

    @Test
    // Tests if adding Future objects is performed correctly
    public void testAddingLoadingInfo() {
        TileKey key = new TileKey();

        //Tile wasn't added
        assertFalse(TilesRepository.isTileBeingLoaded(key));

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<BufferedImage> future = service.submit(() -> new BufferedImage(0, 0, 0));
        TilesRepository.addLoadingTile(key, future);

        //Tile was added
        assertTrue(TilesRepository.isTileBeingLoaded(key));
        assertEquals(TilesRepository.getFuture(key), future);
    }

    @Test
    // Tests if Future objects are removed correctly
    public void testRemovingLoadingInfo() {
        TileKey key = new TileKey();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<BufferedImage> future = service.submit(() -> new BufferedImage(0, 0, 0));
        TilesRepository.addLoadingTile(key, future);
        TilesRepository.removeLoaded(key);

        //Tile was removed
        assertFalse(TilesRepository.isTileBeingLoaded(key));
    }

}
