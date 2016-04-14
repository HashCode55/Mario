package com.spacetime.mario.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.scenes.Hud;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tiledMapTileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        tiledMapTileSet = tiledMap.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        if(getCell().getTile().getId() == BLANK_COIN)
            MarioBros.assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        else
            MarioBros.assetManager.get("audio/sounds/coin.wav", Sound.class).play();
        getCell().setTile(tiledMapTileSet.getTile(BLANK_COIN));
        Hud.addScore(100);
        Gdx.app.log("Coin", "Collision");
    }
}
