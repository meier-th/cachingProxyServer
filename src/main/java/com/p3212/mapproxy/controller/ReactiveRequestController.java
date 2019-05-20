package com.p3212.mapproxy.controller;

import com.p3212.mapproxy.object.TileKey;
import com.p3212.mapproxy.repository.TilesRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.ServletContextAware;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
public class ReactiveRequestController implements ServletContextAware {

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext context) {
        this.servletContext = context;
    }

    @GetMapping(value = "/{first}/{second}/{third}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMapTile(@PathVariable(value = "first") int x,
                                             @PathVariable(value = "second") int y, @PathVariable(value = "third") int z) {
        //key object is an 'id' of a tile
        TileKey key = new TileKey();
        key.setX(x);
        key.setY(y);
        key.setZ(z);

        //Check if image was already loaded and cached
        if (TilesRepository.isCached(key)) {
            return getCachedTile(key);
        }

        //If tile wasn't cached yet
        else {

            // If another thread has already requested for the same tile
            // Then a Future object for this tile was created in TilesRepository
            // This thread waits for the tile to be loaded
            if (TilesRepository.isTileBeingLoaded(key))
                return getTileFromFuture(key);

                // If this is the first time this tile was requested
            else
                return getTileFromOpenStreets(key);
        }
    }

    // Loads a cached tile from disk
    private ResponseEntity<byte[]> getCachedTile(TileKey key) {
        try {
            //Getting tile from disk
            InputStream in = servletContext.getResourceAsStream("./" + key.getX() + "_" + key.getY() + "_" + key.getZ() + ".png");
            return ResponseEntity.ok(IOUtils.toByteArray(in));
        } catch (IOException error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Loads a tile which is currently being loaded from openStreets by another thread
    private ResponseEntity<byte[]> getTileFromFuture(TileKey key) {
        try {
            BufferedImage image;
            image = TilesRepository.getFuture(key).get();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return ResponseEntity.ok(outputStream.toByteArray());
        } catch (IOException | InterruptedException | ExecutionException error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Saves a loaded tile on disk and removes a Future object
    private void saveTileToCache(TileKey key, BufferedImage image) {
        try {
            //caching tile on disk
            File picFile = new File("./" + key.getX() + "_" + key.getY() + "_" + key.getZ() + ".png");
            ImageIO.write(image, "png", picFile);

            //Add info about caching
            TilesRepository.addCachedTile(key);

            //Remove future object since tile was already cached on disk
            TilesRepository.removeLoaded(key);
        } catch (IOException error) {
            Logger logger = LoggerFactory.getLogger(ReactiveRequestController.class);
            logger.error(error.getMessage());
        }
    }

    // Sends a request for tile to openStreetsMap
    private ResponseEntity<byte[]> getTileFromOpenStreets(TileKey key) {
        //If this is the first request for a specific tile
        //Requesting tile from openStreetMap
        //Using Future
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<BufferedImage> imageFuture = executor.submit(() ->
                ImageIO.read(new URL("https://a.tile.openstreetmap.org/" + key.getX() + "/" + key.getY() + "/" + key.getZ() + ".png")));

        try {
            //Adding a Future object to repository
            //So that if a new request for the same tile is received
            //No additional request to openStreetMap will be sent
            TilesRepository.addLoadingTile(key, imageFuture);
            BufferedImage image = imageFuture.get();

            //Creating a Flux so that tile will be sent to user
            //And cached on disk simultaneously
            Flux.just(image)
                    .subscribeOn(Schedulers.newSingle("cachingThread"))
                    .subscribe((data) -> saveTileToCache(key, data));

            //Sending tile to user concurrently with caching
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return ResponseEntity.ok(outputStream.toByteArray());

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
