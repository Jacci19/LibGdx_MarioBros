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

        switch (cDef){
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:                                    //jeśli głowa mario koliduje z cegłą
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:                                     //jeśli głowa mario koliduje z cegło-monetą
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT){
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                }
                else{
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;

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
                if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT){
                    ((Mario) fixA.getUserData()).hit();
                }
                else{
                    ((Mario) fixB.getUserData()).hit();
                }
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
