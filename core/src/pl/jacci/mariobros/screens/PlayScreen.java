package pl.jacci.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pl.jacci.mariobros.MarioBros;
import pl.jacci.mariobros.scenes.Hud;
import pl.jacci.mariobros.sprites.Mario;

public class PlayScreen implements Screen {

    private MarioBros game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;        //graphical representation of fixtures of body in box2d world

    //sprites
    private Mario player;

    public PlayScreen(MarioBros game) {

        this.game = game;
        //create cam used to follow mario through cam world
        gameCam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size

        //gamePort = new ScreenViewport(gameCam);                                           //można porównać wygląd apki na trzech różnych viewportach
        //gamePort = new StretchViewport(800, 480, gameCam);
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);


        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        //initially set our gamcam to be centered correctly at the start of of map
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);   //ustalamy początkową pozycję kamery (aby nie patrzyła na 0,0
                                                                                                        //gameCam.position.set(gamePort.getScreenWidth() / 2, gamePort.getScreenHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0,-10), true);        //w box2d jak obiekt się nie rusza to nie jest obliczany (sleep)
        b2dr = new Box2DDebugRenderer();                                //allows for debug lines of our box2d world.

        //create mario in our game world
        //player = new Mario(world, this);
        player = new Mario(world);

        BodyDef bdef = new BodyDef();                               //body definition
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();                         //fixture definition
        Body body;

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create pipe bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/ MarioBros.PPM, (rect.getY() + rect.getHeight() / 2)/ MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
/*
        //if our user is holding down mouse, move our camera through the game world
        if(Gdx.input.isTouched()){
            gameCam.position.x += 100 * dt;
        }
*/
        //control our player using immediate impulses
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){                                                                  //działa raz po naciśnięciu
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);      //center - aby nie powstał torque
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2){                      //działa aż do puszczenia
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        //handle user input first
        handleInput(dt);
        world.step(1/60f, 6, 2);

        gameCam.position.x = player.b2body.getPosition().x;                 //attach our gamecam to our players.x coordinate

        gameCam.update();                                                   //update our gamecam with correct coordinates after changes
        renderer.setView(gameCam);                                          //tell our renderer to draw only what our camera can see in our game world.
    }

    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game map
        renderer.render();

        //renderer our Box2DDebugLines
        b2dr.render(world, gameCam.combined);

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
        //hud.dispose();
    }
}
