package com.p3212.mapproxy.config;

import com.p3212.mapproxy.object.TileKey;
import com.p3212.mapproxy.repository.TilesRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.regex.Pattern;

@Component
public class StartUpListener implements ApplicationListener<ContextRefreshedEvent> {

    //Initializing application, processing previously cached tiles
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String regex = "\\d_\\d_\\d\\.png";
        File currentDirectory = new File(".");
        for (File file : currentDirectory.listFiles()) {
            if (Pattern.matches(regex, file.getName())) {
                TileKey key = new TileKey();
                int x = Integer.valueOf(file.getName().charAt(0));
                int y = Integer.valueOf(file.getName().charAt(2));
                int z = Integer.valueOf(file.getName().charAt(4));
                key.setX(x);
                key.setY(y);
                key.setZ(z);
                TilesRepository.addCachedTile(key);
            }
        }
    }

}
