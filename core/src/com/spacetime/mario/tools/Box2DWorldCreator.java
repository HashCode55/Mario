package com.spacetime.mario.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.screens.PlayScreen;
import com.spacetime.mario.sprites.Brick;
import com.spacetime.mario.sprites.Coin;
import com.spacetime.mario.enemies.Goomba;

/**
 * Created by mehul on 4/14/16.
 */
public class Box2DWorldCreator {

    private Array<Goomba> goombas;

    public Box2DWorldCreator(PlayScreen screen){

        World world = screen.getWorld();
        TiledMap tiledMap = screen.getTiledMap();

        BodyDef bodyDef = new BodyDef();
        PolygonShape polygonShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        for(MapObject mapObject : tiledMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bodyDef);

            polygonShape.setAsBox((rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getHeight() / 2) / MarioBros.PPM);
            fixtureDef.shape = polygonShape;

            body.createFixture(fixtureDef);
        }

        for(MapObject mapObject : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bodyDef);

            polygonShape.setAsBox((rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getHeight() / 2) / MarioBros.PPM);
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fixtureDef);
        }

        for(MapObject mapObject : tiledMap.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, mapObject);
        }

        for(MapObject mapObject : tiledMap.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, mapObject);
        }

        //create goombas
        goombas = new Array<Goomba>();

        for(MapObject mapObject : tiledMap.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
            goombas.add(new Goomba(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
        }
    }

    public Array<Goomba> getGoombas(){
        return goombas;
    }
}
