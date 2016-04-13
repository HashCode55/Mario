package com.spacetime.mario.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.MarioBros;

/**
 * Created by mehul on 4/14/16.
 */
public class Coin extends InteractiveTileObject {

    public Coin(World world, TiledMap tiledMap, Rectangle bounds){
        super(world, tiledMap, bounds);
    }
}
