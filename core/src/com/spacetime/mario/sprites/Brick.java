package com.spacetime.mario.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by mehul on 4/14/16.
 */
public class Brick extends InteractiveTileObject {
    public Brick(World world, TiledMap tiledMap, Rectangle rectangle){
        super(world, tiledMap, rectangle);
    }
}
