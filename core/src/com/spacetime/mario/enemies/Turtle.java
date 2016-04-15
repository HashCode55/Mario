package com.spacetime.mario.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.screens.PlayScreen;
import com.spacetime.mario.sprites.Mario;

/**
 * Created by mehul on 4/15/16.
 */
public class Turtle extends Enemy{

    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD};
    public State currentState;
    public State previousState;
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    private float animationTimer;
    private Animation enemyWalk;
    private TextureRegion shell;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private float deadRotationDegrees;
    public Turtle(PlayScreen playScreen, float x, float y) {
        super(playScreen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(playScreen.getTextureAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(playScreen.getTextureAtlas().findRegion("turtle"), 64, 0, 16, 24);
        enemyWalk = new Animation(0.2f, frames);
        currentState = previousState = State.WALKING;

        setBounds(getX(), getY(), 16 / MarioBros.PPM, 24 / MarioBros.PPM);
        deadRotationDegrees = 0;

    }

    @Override
    public void update(float delta) {
        animationTimer += delta;
        setRegion(getFrame(delta));
        if(currentState == State.STANDING_SHELL && animationTimer > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(enemy.getPosition().x - getWidth() / 2, enemy.getPosition().y - 8 / MarioBros.PPM);

        if(currentState == State.DEAD){
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if(animationTimer > 5 && !destroyed){
                destroyed = true;
            }
        }
        else {
            enemy.setLinearVelocity(velocity);
        }
    }

    public TextureRegion getFrame(float delta){
        TextureRegion region;

        switch (currentState){
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = enemyWalk.getKeyFrame(animationTimer, true);
                break;
        }
        if(velocity.x > 0 && region.isFlipX() == false){
            region.flip(true, false);
        }
        else if(velocity.x < 0 && region.isFlipX() == true){
            region.flip(true, false);
        }

        animationTimer = currentState == previousState ? animationTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle){
            if(((Turtle)enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }
            else if(currentState == State.MOVING_SHELL && ((Turtle)enemy).currentState == State.WALKING){
                return;
            }
            else
                reverseVelocity(true, false);
        }
        else if(currentState != State.MOVING_SHELL){
            reverseVelocity(true, false);
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
        fixtureDef.restitution = 1.5f;
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        enemy.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else{
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public State getCurrentState(){
        return currentState;
    }

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void draw(SpriteBatch batch){
        if(!destroyed){
            super.draw(batch);
        }
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;
        for(Fixture fixture : enemy.getFixtureList()){
            fixture.setFilterData(filter);
        }
        enemy.applyLinearImpulse(new Vector2(0, 5f), enemy.getWorldCenter(), true);

    }

}
