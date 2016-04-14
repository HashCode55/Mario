package com.spacetime.mario.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen playScreen;

    public Body enemy;

    public Enemy(PlayScreen playScreen, float x, float y){
        this.world = playScreen.getWorld();
        this.playScreen = playScreen;
        setPosition(x, y);
        defineEnemy();
    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead();
}
