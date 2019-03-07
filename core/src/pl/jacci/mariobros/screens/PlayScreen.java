package pl.jacci.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.concurrent.LinkedBlockingQueue;

import pl.jacci.mariobros.MarioBros;
import pl.jacci.mariobros.sprites.enemies.Enemy;
import pl.jacci.mariobros.sprites.items.Item;
import pl.jacci.mariobros.sprites.items.ItemDef;
import pl.jacci.mariobros.sprites.items.Mushroom;
import pl.jacci.mariobros.tools.B2WorldCreator;
import pl.jacci.mariobros.tools.WorldContactListener;
import pl.jacci.mariobros.scenes.Hud;
import pl.jacci.mariobros.sprites.Mario;


public class PlayScreen implements Screen {

        //Reference to our Game, used to set Screens
    private MarioBros game;
    private TextureAtlas atlas;
        //basic playscreen variables
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
        //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
        //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;                    //graphical representation of fixtures of body in box2d world
    private B2WorldCreator creator;
        //sprites
    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(MarioBros game) {

        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();                                              //create cam used to follow mario through cam world
                                                                            //create a FitViewport to maintain virtual aspect ratio despite screen size
        //gamePort = new ScreenViewport(gameCam);                                        //można porównać wygląd apki na trzech różnych viewportach
        //gamePort = new StretchViewport(800, 480, gameCam);
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);
        hud = new Hud(game.batch);                                                       //create our game HUD for scores/timers/level info
                                                                                         //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("level1_jp.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
                                                                                //initially set our gamcam to be centered correctly at the start of of map
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);   //ustalamy początkową pozycję kamery (aby nie patrzyła na 0,0
                                                            //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0,-10), true);                        //w box2d jak obiekt się nie rusza to nie jest obliczany (sleep)
        b2dr = new Box2DDebugRenderer();                                                //allows for debug lines of our box2d world.

        creator = new B2WorldCreator(this);
        player = new Mario(this);                                                 //create mario in our game world
        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setVolume(0.1f);
        music.setLooping(true);
        //music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();                                             //poll is like a pop
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt){

        if (player.currentState != Mario.State.DEAD){
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){                                                                  //działa raz po naciśnięciu
                player.jump();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2){                      //działa aż do puszczenia
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2){
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                player.fire();        }
            }
    }

    public void update(float dt){

        handleInput(dt);                                                    //handle user input first
        handleSpawningItems();
        world.step(1/60f, 6, 2);         //takes 1 step in the physics simulation(60 times per second)
        player.update(dt);
        for (Enemy enemy : creator.getEnemies()){
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM){         //wrogowie zaczynają się poruszać dopiero wtedy gdy mario jest niedaleko
                enemy.b2body.setActive(true);
            }
        }
        for(Item item : items){
            item.update(dt);
        }
        hud.update(dt);
        if (player.currentState != Mario.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;             //attach our gamecam to our players.x coordinate
        }
        gameCam.update();                                                   //update our gamecam with correct coordinates after changes
        renderer.setView(gameCam);                                          //tell our renderer to draw only what our camera can see in our game world.
    }

    @Override
    public void render(float delta) {

        update(delta);                                                      //separate our update logic from render

        Gdx.gl.glClearColor(0, 0, 0, 1);                //clear the game screen with Black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();                                                  //render our game map
        b2dr.render(world, gameCam.combined);                               //renderer our Box2DDebugLines

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies()){
            enemy.draw(game.batch);
        }
        for(Item item : items){
            item.draw(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);     //Set our batch to now draw what the Hud camera sees.
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);                                     //updated our game viewport
    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
            //dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
