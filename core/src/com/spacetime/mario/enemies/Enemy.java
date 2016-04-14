package com.spacetime.mario.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen playScreen;
    public Vector2 velocity;
    public Body enemy;

    public Enemy(PlayScreen playScreen, float x, float y){
        this.world = playScreen.getWorld();
        this.playScreen = playScreen;
        velocity = new Vector2(1, 0);
        setPosition(x, y);
        defineEnemy();
        enemy.setActive(false);
    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead();

    public abstract void update(float delta);

    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
