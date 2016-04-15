package com.spacetime.mario.sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.enemies.Enemy;
import com.spacetime.mario.enemies.Turtle;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public class Mario extends Sprite {
    public enum State{FALLING, JUMPING, RUNNING, STANDING, GROWING, DEAD};
    public State currentState;
    public State previousState;
    private World world;
    public Body mario;
    //the texture region that represents mario standing.
    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;
    private TextureRegion marioDead;

    private boolean marioIsBig;
    private boolean runGrowAnimation;

    private boolean runningRight;
    private float animationTimer;

    private boolean timeToDefineBigMario;
    private boolean timeToReDefineMario;
    private boolean isMarioDead;

    public Mario(PlayScreen playScreen){
        this.world = playScreen.getWorld();
        defineMario();

        //get texture from the sprite, create textures for mario standing....
        marioStand = new TextureRegion(playScreen.getTextureAtlas().findRegion("little_mario"), 0, 0, 16, 16 );
        bigMarioStand = new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        marioDead = new TextureRegion(playScreen.getTextureAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        setBounds(0, 10, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);

        currentState = State.STANDING;
        previousState = State.STANDING;

        animationTimer = 0;
        runningRight = true;

        //for mario running
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        //for growing the mario
        frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);

        //for mario jump
        marioJump = new TextureRegion(playScreen.getTextureAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(playScreen.getTextureAtlas().findRegion("big_mario"), 80, 0, 16, 32);

    }

    public void update(float delta){
        if(marioIsBig)
            setPosition(mario.getPosition().x - getWidth() / 2, mario.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else
            setPosition(mario.getPosition().x - getWidth() / 2, mario.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToReDefineMario)
            reDefineMario();
    }

    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        MarioBros.assetManager.get("audio/sounds/powerup.wav", Sound.class).play();
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
    }

    private TextureRegion getFrame(float delta){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(animationTimer);
                if(growMario.isAnimationFinished(animationTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(animationTimer, true) : marioRun.getKeyFrame(animationTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }
        //flip the mario
        if((mario.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if((mario.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }
        //if prev state is not equal to current state restart the timer.
        animationTimer = currentState == previousState ? animationTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    private State getState(){
        if(runGrowAnimation)
            return State.GROWING;
        else if(isMarioDead)
            return State.DEAD;
        else if(mario.getLinearVelocity().y > 0 || (mario.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(mario.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(mario.getLinearVelocity().x != 0)
            return State.RUNNING;
        return State.STANDING;
    }

    private void defineMario(){
        //we can add more than one fixtures in a body
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(6 / MarioBros.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                                                            |MarioBros.OBJECT_BIT
                                                            |MarioBros.ENEMY_BIT
                                                            |MarioBros.ENEMY_HEAD_BIT
                                                            |MarioBros.ITEM_BIT;

        fixtureDef.shape = circleShape;
        mario = world.createBody(bodyDef);
        mario.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2( 2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        mario.createFixture(fixtureDef).setUserData(this);
        fixtureDef.isSensor = true;
    }

    private void defineBigMario(){

        Vector2 currentPos = mario.getPosition();
        world.destroyBody(mario);
        //we can add more than one fixtures in a body
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPos.add(0, 10 / MarioBros.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(6 / MarioBros.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                |MarioBros.OBJECT_BIT
                |MarioBros.ENEMY_BIT
                |MarioBros.ENEMY_HEAD_BIT
                |MarioBros.ITEM_BIT;

        fixtureDef.shape = circleShape;
        mario = world.createBody(bodyDef);
        mario.createFixture(fixtureDef).setUserData(this);
        circleShape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        mario.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2( 2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        mario.createFixture(fixtureDef).setUserData(this);
        fixtureDef.isSensor = true;
        timeToDefineBigMario = false;
    }

    public void reDefineMario(){
        //we can add more than one fixtures in a body
        Vector2 pos = mario.getPosition();
        world.destroyBody(mario);
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(pos);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(6 / MarioBros.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                |MarioBros.OBJECT_BIT
                |MarioBros.ENEMY_BIT
                |MarioBros.ENEMY_HEAD_BIT
                |MarioBros.ITEM_BIT;

        fixtureDef.shape = circleShape;
        mario = world.createBody(bodyDef);
        mario.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2( 2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        mario.createFixture(fixtureDef).setUserData(this);
        fixtureDef.isSensor = true;
        timeToReDefineMario = false;
    }

    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle)enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if (isBig()) {
                marioIsBig = false;
                timeToReDefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.assetManager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                MarioBros.assetManager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBros.assetManager.get("audio/sounds/mariodie.wav", Sound.class).play();
                isMarioDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : mario.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                mario.applyLinearImpulse(new Vector2(0, 4f), mario.getWorldCenter(), true);
            }
        }
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isDead(){
        return isMarioDead;
    }

    public float getStateTimer(){
        return animationTimer;
    }
}
