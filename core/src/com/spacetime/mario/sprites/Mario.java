package com.spacetime.mario.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.screens.PlayScreen;

/**
 * Created by mehul on 4/14/16.
 */
public class Mario extends Sprite {
    public enum State{FALLING, JUMPING, RUNNING, STANDING};
    public State currentState;
    public State previousState;
    private World world;
    public Body mario;
    //the texture region that represents mario standing.
    private TextureRegion marioStand;

    private Animation marioRun;
    private Animation marioJump;

    private boolean runningRight;
    private float animationTimer;

    public Mario(PlayScreen playScreen){
        super(playScreen.getTextureAtlas().findRegion("little_mario"));
        this.world = playScreen.getWorld();
        defineMario();
        //get texture from the sprite.
        marioStand = new TextureRegion(getTexture(), 0, 10, 16, 16 );
        setBounds(0, 10, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);

        currentState = State.STANDING;
        previousState = State.STANDING;

        animationTimer = 0;

        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(getTexture(), i * 16, 10, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);
        frames.clear();
        //initialise animations

        for(int i = 4; i < 6; i++){
            frames.add(new TextureRegion(getTexture(), i * 16, 10, 16, 16));
        }
        marioJump = new Animation(0.1f, frames);
    }

    public void update(float delta){
        setPosition(mario.getPosition().x - getWidth() / 2, mario.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
    }

    private TextureRegion getFrame(float delta){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case JUMPING:
                region = marioJump.getKeyFrame(animationTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(animationTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
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
        animationTimer = currentState == previousState ? animationTimer += delta : 0;
        previousState = currentState;
        return region;
    }

    private State getState(){
        if(mario.getLinearVelocity().y > 0 || (mario.getLinearVelocity().y < 0 && previousState == State.JUMPING))
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
                                                            |MarioBros.ENEMY_HEAD_BIT;

        fixtureDef.shape = circleShape;
        mario = world.createBody(bodyDef);
        mario.createFixture(fixtureDef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2( 2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fixtureDef.shape = head;
        mario.createFixture(fixtureDef).setUserData("head");
        fixtureDef.isSensor = true;
    }
}
