package com.spacetime.mario.items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.screens.PlayScreen;
import com.spacetime.mario.sprites.Mario;

/**
 * Created by mehul on 4/14/16.
 */
public abstract class Item extends Sprite{
    protected PlayScreen playScreen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body item;

    public Item(PlayScreen playScreen, float x, float y){
        this.playScreen = playScreen;
        this.world = playScreen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        toDestroy = false;
        destroyed = false;
        defineItem();
    }

    public abstract void defineItem();
    public abstract void use(Mario mario);

    public void update(float delta){
        if(toDestroy && !destroyed){
            world.destroyBody(item);
            destroyed = true;
        }
    }
    public void draw(SpriteBatch batch){
        if(!destroyed)
            super.draw(batch);
    }

    public void destroy(){
        toDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }

}
