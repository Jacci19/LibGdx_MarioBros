package pl.jacci.mariobros.sprites.items;

import com.badlogic.gdx.math.Vector2;


public class ItemDef {
    public Vector2 position;
    public Class<?> type;                           //nie wiemy jaki typ klasy

    public ItemDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
