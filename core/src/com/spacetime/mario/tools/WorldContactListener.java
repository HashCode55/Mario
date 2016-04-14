package com.spacetime.mario.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.spacetime.mario.MarioBros;
import com.spacetime.mario.enemies.Enemy;
import com.spacetime.mario.items.Item;
import com.spacetime.mario.sprites.InteractiveTileObject;
import com.spacetime.mario.sprites.Mario;

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

        switch (cDef){
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT)
                    ((InteractiveTileObject)fixtureB.getUserData()).onHeadHit((Mario)(fixtureA.getUserData()));
                else
                    ((InteractiveTileObject)fixtureA.getUserData()).onHeadHit((Mario)(fixtureB.getUserData()));
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_HEAD_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixtureA.getUserData()).hitOnHead();
                else if(fixtureB.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixtureB.getUserData()).hitOnHead();
                break;

            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario)fixtureA.getUserData()).hit();
                else
                    ((Mario)fixtureB.getUserData()).hit();
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy)fixtureB.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixtureA.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixtureA.getUserData()).use((Mario)(fixtureB.getUserData()));
                else
                    ((Item)fixtureB.getUserData()).use((Mario)(fixtureA.getUserData()));
                break;
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
