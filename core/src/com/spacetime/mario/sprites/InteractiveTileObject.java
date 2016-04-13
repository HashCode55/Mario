package com.spacetime.mario.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
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
public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap tiledMap;
    protected TiledMapTile tiledMapTile;
    protected Rectangle bounds;
    protected Body body;

    public InteractiveTileObject(World world, TiledMap tiledMap, Rectangle bounds){
        this.world = world;
        this.tiledMap = tiledMap;
        this.bounds = bounds;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);
        body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        polygonShape.setAsBox((bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getHeight() / 2) / MarioBros.PPM);
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);

    }
}
