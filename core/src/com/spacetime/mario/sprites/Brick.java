package com.spacetime.mario.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.scenes.Hud;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle rectangle){
        super(screen, rectangle);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick", "Collision");
        setCategoryFilter(MarioBros.DESTROYED_BIT);
        Hud.addScore(200);
        MarioBros.assetManager.get("audio/sounds/breakblock.wav", Sound.class).play();
        getCell().setTile(null);
    }
}
