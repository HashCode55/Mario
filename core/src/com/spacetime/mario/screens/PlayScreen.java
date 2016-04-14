package com.spacetime.mario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.items.Item;
import com.spacetime.mario.items.ItemDef;
import com.spacetime.mario.items.Mushroom;
import com.spacetime.mario.scenes.Hud;
import com.spacetime.mario.enemies.Enemy;
import com.spacetime.mario.sprites.Mario;
import com.spacetime.mario.tools.Box2DWorldCreator;
import com.spacetime.mario.tools.WorldContactListener;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mehul on 4/13/16.
 */
public class PlayScreen extends ScreenAdapter{

    private MarioBros game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Hud hud;
    private TextureAtlas textureAtlas;

    //TILED MAP VARIABLES
    //for loading the map
    private TmxMapLoader mapLoader;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    //Box2d VARIABLES
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Mario mario;
    private Box2DWorldCreator box2DWorldCreator;

    //enemies
    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBros game){
        this.game = game;
        textureAtlas = new TextureAtlas("Mario_and_Enemies.pack");
        camera = new OrthographicCamera();
        viewport = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, camera);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        tiledMap = mapLoader.load("level1.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MarioBros.PPM);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10f), true);
        debugRenderer = new Box2DDebugRenderer();

        box2DWorldCreator = new Box2DWorldCreator(this);

        mario = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.assetManager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef itemDef){
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef itemDef = itemsToSpawn.poll();
            if(itemDef.type == Mushroom.class)
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
        }
    }

    public TextureAtlas getTextureAtlas(){
        return textureAtlas;
    }

    public TiledMap getTiledMap(){
        return tiledMap;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void render(float delta) {
        //super.render(delta);
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    private void update(float delta){
        handleInput(delta);
        handleSpawningItems();
        for(Enemy enemy : box2DWorldCreator.getGoombas()){
            enemy.update(delta);
            if(enemy.getX() < mario.getX() + 224 / MarioBros.PPM) {
                enemy.enemy.setActive(true);
            }
        }

        for(Item item : items){
            item.update(delta);
        }
        world.step(delta, 6, 2);
        hud.update(delta);
        mario.update(delta);
        if(mario.mario.getPosition().x > viewport.getWorldWidth() / 2)
            camera.position.x = mario.mario.getPosition().x;
        camera.update();
        orthogonalTiledMapRenderer.setView(camera);
    }

    private void handleInput(float delta){
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
            mario.mario.applyLinearImpulse(new Vector2(0, 4f), mario.mario.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && mario.mario.getLinearVelocity().x <= 2)
            mario.mario.applyLinearImpulse(new Vector2(0.1f, 0), mario.mario.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && mario.mario.getLinearVelocity().x >= -2)
            mario.mario.applyLinearImpulse(new Vector2(-0.1f, 0), mario.mario.getWorldCenter(), true);
    }

    private void clearScreen(){
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw(){
        orthogonalTiledMapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        mario.draw(game.batch);
        for(Enemy enemy : box2DWorldCreator.getGoombas()){
            enemy.draw(game.batch);
        }
        for(Item item : items){
            item.draw(game.batch);
        }
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    private void drawDebug(){
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dispose() {
        super.dispose();
        tiledMap.dispose();
        orthogonalTiledMapRenderer.dispose();
        world.dispose();
        debugRenderer.dispose();
        hud.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }
}
