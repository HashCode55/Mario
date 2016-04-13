package com.spacetime.mario.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.MarioBros;

/**
 * Created by mehul on 4/14/16.
 */
public class Mario extends Sprite {
    private World world;
    public Body mario;

    public Mario(World world){
        this.world = world;
        defineMario();
    }

    private void defineMario(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(5 / MarioBros.PPM);

        mario = world.createBody(bodyDef);
        mario.createFixture(circleShape, 0);

    }
}
