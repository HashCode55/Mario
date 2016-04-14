package com.spacetime.mario.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.sprites.Enemy;
import com.spacetime.mario.sprites.InteractiveTileObject;

/**
 * Created by mehul on 4/14/16.
 */
public class WorldContactListener implements ContactListener {

    public static final String TAG = "WorldContactListener";

    //contact listener gets activated when two fixtures make contact with each other.
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        //if any of the fixtures is mario's head then do this.
        if(fixtureA.getUserData() == "head" || fixtureB.getUserData() == "head"){
            Fixture head = fixtureA.getUserData() == "head" ? fixtureA : fixtureB;
            Fixture object = head == fixtureA ? fixtureB : fixtureA;
            //if the object is an instance of InteactiveTileObject
            if(object.getUserData() instanceof InteractiveTileObject)
                ((InteractiveTileObject)object.getUserData()).onHeadHit();
        }

        switch (cDef){
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_HEAD_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixtureA.getUserData()).hitOnHead();
                else if(fixtureB.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixtureB.getUserData()).hitOnHead();


        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
