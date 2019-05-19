package com.p3212.mapproxy.controller;

import com.p3212.mapproxy.object.TileKey;
import com.p3212.mapproxy.repository.TilesRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.ServletContextAware;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Controller
public class ReactiveRequestController implements ServletContextAware {

    private ServletContext servletContext;

    @Override
    public void setServletContext (ServletContext context) {
        this.servletContext = context;
    }

    @GetMapping(value = "/{first}/{second}/{third}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getMapTile(@PathVariable(value = "first") int x,
               @PathVariable(value = "second") int y, @PathVariable(value = "third") int z) {
        TileKey key = new TileKey();
        key.setX(x);
        key.setY(y);
        key.setZ(z);
        if (TilesRepository.isCached(key)) {
            try {
                InputStream in = servletContext.getResourceAsStream("./"+x+"_"+y+"_"+z+".png");
                return ResponseEntity.ok(IOUtils.toByteArray(in));
            } catch (IOException error) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            try {
                BufferedImage image = ImageIO.read(new URL("https://a.tile.openstreetmap.org/"+x+"/"+y+"/"+z+".png"));
                File picFile = new File("./"+x+"_"+y+"_"+z+".png");
                ImageIO.write(image, "png", picFile);
                TilesRepository.addCachedTile(key);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", outputStream);
                return ResponseEntity.ok(outputStream.toByteArray());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }


}
