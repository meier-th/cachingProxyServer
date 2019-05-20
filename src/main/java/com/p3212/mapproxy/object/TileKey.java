package com.p3212.mapproxy.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
// Represents an 'ID' for a tile. Stores numbers from URL ( .../x/y/z.png)
public class TileKey {

    private int x;
    private int y;
    private int z;

}
