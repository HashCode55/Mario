package com.spacetime.mario.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.screens.PlayScreen;
import com.spacetime.mario.sprites.Mario;

/**
 * Created by mehul on 4/14/16.
 */
public class Mushroom extends Item {
    public Mushroom(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
        setRegion(new TextureRegion(playScreen.getTextureAtlas().findRegion("mushroom"), 0, 0, 16, 16));
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(6 / MarioBros.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = MarioBros.ITEM_BIT;
        fixtureDef.filter.maskBits = MarioBros.MARIO_BIT
                                    | MarioBros.OBJECT_BIT
                                    | MarioBros.GROUND_BIT
                                    | MarioBros.COIN_BIT
                                    | MarioBros.BRICK_BIT;
        item = world.createBody(bodyDef);
        item.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        mario.grow();
        destroy();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setPosition(item.getPosition().x - getWidth() / 2, item.getPosition().y - getHeight() / 2);
        velocity.y = item.getLinearVelocity().y;
        item.setLinearVelocity(velocity);
        item.setLinearVelocity(velocity);
    }
}
