package com.p3212.mapproxy;

import com.p3212.mapproxy.controller.ReactiveRequestController;
import com.p3212.mapproxy.object.TileKey;
import com.p3212.mapproxy.repository.TilesRepository;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ReactiveRequestControllerTests {

    @Test
    // Tests getting tile from openStreetMap
    public void testGetTileFromOpenStreets() {
        TileKey key = new TileKey();
        key.setX(1);
        key.setY(1);
        key.setZ(1);
        ReactiveRequestController controller = new ReactiveRequestController();
        ResponseEntity<byte[]> response = controller.getTileFromOpenStreets(key);

        // Tile should not be null
        assertNotNull(response);

        // Tile should be cached
        assertTrue(TilesRepository.isCached(key));
    }

    @Test
    // Tests getting tile from Future object
    public void testGetTileFromFuture() {
        TileKey key = new TileKey();
        key.setX(1);
        key.setY(1);
        key.setZ(1);
        BufferedImage image = new BufferedImage(1, 1, 1);
        ReactiveRequestController controller = new ReactiveRequestController();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<BufferedImage> future = service.submit(() -> {
            Thread.sleep(200);
            return image;
        });
        TilesRepository.addLoadingTile(key, future);
        ResponseEntity<byte[]> response = controller.getTileFromFuture(key);

        // Tile should not be null
        assertNotNull(response);
    }

}
