package pl.jacci.mariobros.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import pl.jacci.mariobros.MarioBros;
import pl.jacci.mariobros.sprites.Mario;
import pl.jacci.mariobros.sprites.enemies.Enemy;
import pl.jacci.mariobros.sprites.items.Item;
import pl.jacci.mariobros.sprites.tileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        //Gdx.app.log("Begin contact","a");
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;           //cdef - Collision Definition - https://youtu.be/87He9A4kTQ0?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=346


        if(fixA.getUserData() == "head" || fixB.getUserData() == "head"){
            Fixture head = (fixA.getUserData() == "head") ? fixA : fixB;
            Fixture object = (head == fixA) ? fixB : fixA;

            if(object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())){          //https://youtu.be/tcH6Mp03KC0?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=680
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }


        switch (cDef){
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:                                    //jeśli czubek głowy wroga koliduje z mario...
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                    ((Enemy)fixA.getUserData()).hitOnHead();
                }
                else{
                    ((Enemy)fixB.getUserData()).hitOnHead();
                }
                break;

            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:                                        //jeśli wróg koliduje z obiektem (np. rurą)...
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);                 //...to odwróć kierunek chodzenia wroga
                }
                else{
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;

            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:                                         //jeśli wróg koliduje z mario...
                Gdx.app.log("MARIO", "DIED");
                break;

            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:                                         //jeśli wróg koliduje z innym wrogiem...
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:                                         //jeśli item koliduje z obiektem...
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                }
                else{
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;

            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:                                          //jeśli item koliduje z Mario...
                if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT){                        //jeśli fixtur_A = ITEM_BIT to...
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                }
                else{
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        //Gdx.app.log("End contact","b");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
