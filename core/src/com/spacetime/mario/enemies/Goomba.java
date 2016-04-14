package com.spacetime.mario.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public class Goomba extends com.spacetime.mario.enemies.Enemy {

    private float animationTimer;
    private Animation enemyWalk;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++){
            frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        enemyWalk = new Animation(0.4f, frames);
        enemyWalk.setPlayMode(Animation.PlayMode.LOOP);
        animationTimer = 0;
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float delta){
        animationTimer += delta;
        if(setToDestroy && !destroyed){
            world.destroyBody(enemy);
            destroyed = true;
            setRegion(new TextureRegion(playScreen.getTextureAtlas().findRegion("goomba"), 2 * 16, 0, 16, 16));
            animationTimer = 0;
        }
        else if(!destroyed) {
            setPosition(enemy.getPosition().x - getWidth() / 2, enemy.getPosition().y - getHeight() / 2);
            setRegion(enemyWalk.getKeyFrame(animationTimer));
            enemy.setLinearVelocity(velocity);
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(6 / MarioBros.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.MARIO_BIT;

        fixtureDef.shape = circleShape;
        enemy = world.createBody(bodyDef);
        enemy.createFixture(fixtureDef).setUserData(this);

        //create the head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
        vertices[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
        vertices[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertices[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);

        head.set(vertices);
        fixtureDef.shape = head;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        enemy.createFixture(fixtureDef).setUserData(this);
    }

    public void draw(SpriteBatch spriteBatch){
        if(!destroyed || animationTimer < 1){
            super.draw(spriteBatch);
        }
    }

    @Override
    public void hitOnHead(){
        MarioBros.assetManager.get("audio/sounds/stomp.wav", Sound.class).play();
        setToDestroy = true;
    }
}
