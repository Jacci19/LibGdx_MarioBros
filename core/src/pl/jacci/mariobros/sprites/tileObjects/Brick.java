package pl.jacci.mariobros.sprites.tileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;

import pl.jacci.mariobros.MarioBros;
import pl.jacci.mariobros.scenes.Hud;
import pl.jacci.mariobros.screens.PlayScreen;
import pl.jacci.mariobros.sprites.Mario;


public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);                                            // zapewnia znikanie uderzonych cegieł
            Hud.addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        else{
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }


}