package pl.jacci.mariobros.sprites.tileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import pl.jacci.mariobros.MarioBros;
import pl.jacci.mariobros.scenes.Hud;
import pl.jacci.mariobros.screens.PlayScreen;
import pl.jacci.mariobros.sprites.Mario;
import pl.jacci.mariobros.sprites.items.ItemDef;
import pl.jacci.mariobros.sprites.items.Mushroom;


public class Coin extends InteractiveTileObject{

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;                                          //taki numer ma ta grafika w Tiled (27+1 bo liczone od zera w tabeli)

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("NES - Super Mario Bros - Tileset");         // nazwa pliku z całym tileSet
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN){
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
        else{
            if (object.getProperties().containsKey("mushroom")){                    //jeśli ten coin-brick ma właściwość "mushroom" (nadaną przez nas w Tiled) to wypuść z niego grzybka
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else{
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            Hud.addScore(100);
        }
    }
}