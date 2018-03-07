package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {

    //Objeto que contiene el tileset
    private TiledMap mapa;
    //Objeto con el que se pinta el mapa
    //private TiledMapRenderer mapaRenderer;
    private OrthogonalTiledMapRenderer mapaRenderer;
    //Objeto camara del juego
    private OrthographicCamera camara;
    // ​Atributo en el que se cargará la imagen del mosquetero.
    private Texture img;
    // ​Atributo que permitirá la representación de la imagen de textura anterior.
    //private Sprite sprite;
    // ​Atributo que permite dibujar imágenes 2D, en este caso el sprite.
    private SpriteBatch sb;

    //Indicamos el numero de filas y columnas del spritesheet
    private static final int FRAME_COLS = 3;
    private static final int FRAME_ROWS = 4;
    //Animacion que se mostrara en el metodo render()
    private Animation jugador;
    //Animaciones para las cuatro direcciones
    private Animation jugadorArriba;
    private Animation jugadorDerecha;
    private Animation jugadorAbajo;
    private Animation jugadorIzquierda;

    //Posicion eje actual del jugador
    private float jugadorX, jugadorY;

    // ​Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación,
    // ​servirá para determinar cual es el frame que se debe representar​
    private float stateTime;

    // ​Contendrá el frame que se va a mostrar en cada momento.
    private TextureRegion cuadroActual;

    //Tamaño del mapa de baldosas
    private int anchoMapa, altoMapa;
    //Altura y anchura de un tile del mapa de baldosas
    int anchoCelda, altoCelda;

    private boolean obstaculo[][];

    //Anchura y altura del sprite animado del jugador
    int anchoJugador, altoJugador;

    //float anchura = 1000;//Gdx.graphics.getWidth();
    //float altura = 1000;//Gdx.graphics.getHeight();

    //Animaciones posicionales relacionadas con los NPC del juego
    private Animation noJugadorArriba;
    private Animation noJugadorDerecha;
    private Animation noJugadorAbajo;
    private Animation noJugadorIzquierda;

    //Array con los objetos Animation de los NPC
    private Animation[] noJugador;
    //Atributos que indican la anchura y altura del sprite animado de los NPC.
    int anchoNoJugador, altoNoJugador;
    //Posición inicial X de cada uno de los NPC
    private float[] noJugadorX;
    //Posición inicial Y de cada uno de los NPC
    private float[] noJugadorY;
    //Posición final X de cada uno de los NPC
    private float[] destinoX;
    //Posición final Y de cada uno de los NPC
    private float[] destinoY;
    //Número de NPC que van a aparecer en el juego
    private static final int numeroNPCs = 20;
    // Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación
    // de los NPC , servirá para determinar cual es el frame que se debe representar.
    private float stateTimeNPC;

    //---------------------------------------------------------------------------CREATE-----------

    @Override
    public void create() {

        //Creamos la camara y la vinculamos con el lienzo del juego
        //Le damos un valor que coincida con el de todas las plataformas (800x480)
        camara = new OrthographicCamera(800, 480);
        //Posicionamos la vista de la camara para q su vertice inferior izquierdo sea (0,0)
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        //​Indicamos que los eventos de entrada sean procesados por esta clase.
        Gdx.input.setInputProcessor(this);
        camara.update();

        // ​Cargamos la imagen del mosquetero en el objeto img de la clase Texture.
        img = new Texture(Gdx.files.internal("mosquetero.png"));

        //Sacamos los frames de img en un array de TextureRegion
        TextureRegion[][] tmp = TextureRegion.split(img, img.getWidth() / FRAME_COLS, img.getHeight() / FRAME_ROWS);

        //Creamos las distintas animaciones
        jugadorArriba = new Animation(0.150f, tmp[0]);
        jugadorDerecha = new Animation(0.150f, tmp[1]);
        jugadorAbajo = new Animation(0.150f, tmp[2]);
        jugadorIzquierda = new Animation(0.150f, tmp[3]);

        // ​En principio se utiliza la animación del jugador arriba como animación por defecto.
        jugador = jugadorAbajo;

        // ​Posición inicial del jugador.
        jugadorX = jugadorY = 0;

        // ​Ponemos a cero el atributo stateTime, que marca el tiempo e ejecución de la animación.
        stateTime = 0f;

        // ​Asignamos la imagen al objeto sprite para que pueda ser presentada en pantalla.
        //sprite = new Sprite(img);
        //​ Creamos el objeto SpriteBatch que nos permitirá representar adecuadamente el sprite
        // ​en el método render()
        sb = new SpriteBatch();

        // ​Cargamos el mapa de baldosas desde la carpeta de assets
        //mapaRenderer = new OrthogonalTiledMapRenderer(mapa);

        //Cargamos el mapa o tileset
        mapa = new TmxMapLoader().load("paris.tmx");
        mapaRenderer = new OrthogonalTiledMapRenderer(mapa);

        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        anchoCelda = (int) capa.getTileWidth();
        altoCelda = (int) capa.getTileHeight();
        anchoMapa = capa.getWidth() * anchoCelda;
        altoMapa = capa.getHeight() * altoCelda;

        //Cargamos la capa de los obstaculos, que es la tercera en el TileMap
        TiledMapTileLayer capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(2);
        //Cargamos el array de obstaculos
        int anchoCapa = capaObstaculos.getWidth(), altoCapa = capaObstaculos.getHeight();
        obstaculo = new boolean[altoCapa][anchoCapa];
        for (int x = 0; x < anchoCapa; x++) {
            for (int y = 0; y < altoCapa; y++) {
                obstaculo[x][y] = capaObstaculos.getCell(x, y) != null;
            }
        }

        //Cargamos los valores de ancho y alto del sprite
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime);
        anchoJugador = cuadroActual.getRegionWidth();
        altoJugador = cuadroActual.getRegionHeight();

        //Inicializamos el apartado referente a los NPC
        noJugador = new Animation[numeroNPCs];
        noJugadorX = new float[numeroNPCs];
        noJugadorY = new float[numeroNPCs];
        destinoX = new float[numeroNPCs];
        destinoY = new float[numeroNPCs];

        //Creamos las animaciones posicionales de los NPC
        //Cargamos la imagen de los frames del monstruo en el objeto img de la clase Texture.
        img = new Texture(Gdx.files.internal("magorojo.png"));

        //Sacamos los frames de img en un array de TextureRegion.
        tmp = TextureRegion.split(img, img.getWidth() / FRAME_COLS, img.getHeight() /
                FRAME_ROWS);

        // Creamos las distintas animaciones, teniendo en cuenta que el tiempo de muestra de cada frame
        // será de 150 milisegundos.
        noJugadorArriba = new Animation(0.150f, tmp[0]);
        noJugadorArriba.setPlayMode(Animation.PlayMode.LOOP);
        noJugadorDerecha = new Animation(0.150f, tmp[1]);
        noJugadorDerecha.setPlayMode(Animation.PlayMode.LOOP);
        noJugadorAbajo = new Animation(0.150f, tmp[2]);
        noJugadorAbajo.setPlayMode(Animation.PlayMode.LOOP);
        noJugadorIzquierda = new Animation(0.150f, tmp[3]);
        noJugadorIzquierda.setPlayMode(Animation.PlayMode.LOOP);

        //Cargamos en los atributos del ancho y alto del sprite del monstruo sus valores
        cuadroActual = (TextureRegion) noJugadorAbajo.getKeyFrame(stateTimeNPC);
        anchoNoJugador = cuadroActual.getRegionWidth();
        altoNoJugador = cuadroActual.getRegionHeight();

        //Se inicializan, la animación por defecto y, de forma aleatoria, las posiciones
        //iniciales y finales de los NPC. Para simplificar un poco, los NPC pares, se moveran
        //de forma vertical y los impares de forma horizontal.
        for (int i = 0; i < numeroNPCs; i++) {
            noJugadorX[i] = (float) (Math.random() * anchoMapa);
            noJugadorY[i] = (float) (Math.random() * altoMapa);

            if (i % 2 == 0) {
                // NPC par => mover de forma vertical
                destinoX[i] = noJugadorX[i];
                destinoY[i] = (float) (Math.random() * altoMapa);
                // Determinamos cual de las animaciones verticales se utiliza.
                if (noJugadorY[i] < destinoY[i]) {
                    noJugador[i] = noJugadorArriba;
                } else {
                    noJugador[i] = noJugadorAbajo;
                }
            } else {
                // NPC impar => mover de forma horizontal
                destinoX[i] = (float) (Math.random() * anchoMapa);
                destinoY[i] = noJugadorY[i];
                // Determinamos cual de las animaciones horizontales se utiliza.
                if (noJugadorX[i] < destinoX[i]) {
                    noJugador[i] = noJugadorDerecha;
                } else {
                    noJugador[i] = noJugadorIzquierda;
                }
            }
        }

        // Ponemos a cero el atributo stateTimeNPC, que marca el tiempo e ejecución de la animación
        // de los NPC.
        stateTimeNPC = 0f;

    }//FINAL

    //---------------------------------------------------------------------------RENDER----------

    @Override
    public void render() {

        //Ponemos color de fondo a negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        //Borramos la pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Movemos la camara para que se centre en el mosquetero
        camara.position.set(jugadorX, jugadorY, 0f);
        //Comprobamos que la cámara no se salga de los límites del mapa de baldosas,
        //Verificamos, con el método clamp(), que el valor de la posición x de la cámara
        //esté entre la mitad de la anchura de la vista de la cámara y entre la diferencia entre
        //la anchura del mapa restando la mitad de la anchura de la vista de la cámara,
        camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth / 2f, anchoMapa - camara.viewportWidth / 2f);
        //Verificamos, con el método clamp(), que el valor de la posición y de la cámara
        // esté entre la mitad de la altura de la vista de la cámara y entre la diferencia entre
        //la altura del mapa restando la mitad de la altura de la vista de la cámara,
        camara.position.y = MathUtils.clamp(camara.position.y, camara.viewportHeight / 2f, altoMapa - camara.viewportHeight / 2f);

        //Actualizamos la camara del juego
        camara.update();
        //Vinculamos el objeto que dibuja el mapa con la camara
        mapaRenderer.setView(camara);
        //Dibujamos las tres primeras capas del TiledMap
        int[] capas = {0,1,2};
        //Dibujamos el mapa
        mapaRenderer.render();

        //Extraemos el tiempo de la ultima actualizacion del sprite y la acumulamos a stateTime
        stateTime += Gdx.graphics.getDeltaTime();

        //Extraemos el frame que debe ir asociado al momento actual
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime); //1

        //Indicamos al spriteBatch que se muestre en las coordenadas de la camara
        sb.setProjectionMatrix(camara.combined);

        //Inicializamos el objeto SpriteBatch
        sb.begin();
        //Pintamos el sprite a traves del objeto SpriteBatch
        sb.draw(cuadroActual, jugadorX, jugadorY); //2

        //Dibujamos las animaciones de los NPC
        for (int i = 0; i < numeroNPCs; i++) {
            actualizaNPC​(i, 0.5f);
            cuadroActual = (TextureRegion) noJugador[i].getKeyFrame(stateTimeNPC);
            sb.draw(cuadroActual, noJugadorX[i], noJugadorY[i]);
        }
        //Finalizamos el objeto SpriteBatch
        sb.end();

        //Pintamos la cuarta capa del mapa de baldosas
        capas = new int[1];
        capas[0]=3;
        mapaRenderer.render(capas);

        detectaColisiones();
    }

    @Override
    public void dispose() {
        sb.dispose();
    }

    //METODOS DE LA INTERFAZ InputProcessor ---------------------------------------------------

    @Override
    public boolean keyDown(int keycode) {

        //Al pulsar el cursor se mueve el sprite 5 pixel y se pone a cero el atributo que marca
        //el tiempo de ejecucion de la animacion, provocando que la misma se reinicie.

        //Guardamos la posicion anterior del jugador por si al moverse se topa con un obstaculo
        //y podamos volverlo a la posicion anterior
        float jugadorAnteriorX = jugadorX;
        float jugadorAnteriorY = jugadorY;

        stateTime = 0;

        if (keycode == Input.Keys.LEFT) {
            jugadorX += -5;
            jugador = jugadorIzquierda;
        }

        if (keycode == Input.Keys.RIGHT) {
            jugadorX += 5;
            jugador = jugadorDerecha;
        }

        if (keycode == Input.Keys.UP) {
            jugadorY += 5;
            jugador = jugadorArriba;
        }

        if (keycode == Input.Keys.DOWN) {
            jugadorY += -5;
            jugador = jugadorAbajo;
        }

        //Al chocar el jugador vuelve a su posicion inicial
        //la parte izquierda de X es "X+1/4 del ancho del jugador"
        //la parte derecha de X es "X+3/4 del ancho del jugador"
        if ((jugadorX < 0 || jugadorY < 0 || jugadorX > (anchoMapa - anchoJugador) || jugadorY > (altoMapa - altoJugador)) || (obstaculo[(int) ((jugadorX + anchoJugador / 4) / anchoCelda)][((int) (jugadorY) / altoCelda)])
                || (obstaculo[(int) ((jugadorX + 3 * anchoJugador / 4) / anchoCelda)][((int) (jugadorY) / altoCelda)])) {
            jugadorX = jugadorAnteriorX;
            jugadorY = jugadorAnteriorY;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        //Si pulsamos uno de los cursores, se desplaza la cámara
        //de forma adecuada.
        /*if (keycode == Input.Keys.LEFT)
            camara.translate(-32, 0);
        if (keycode == Input.Keys.RIGHT)

            camara.translate(32, 0);
        if (keycode == Input.Keys.UP)
            camara.translate(0, 32);
        if (keycode == Input.Keys.DOWN)
            camara.translate(0, -32);
    */
        //Si pulsamos la tecla del número 1, se alterna la visibilidad de la primera capa
        //del mapa de baldosas.
        if (keycode == Input.Keys.NUM_1)
            mapa.getLayers().get(0).setVisible(!mapa.getLayers().get(0).isVisible());
        //Si pulsamos la tecla del número 2, se alterna la visibilidad de la segunda capa
        //del mapa de baldosas.
        if (keycode == Input.Keys.NUM_2)
            mapa.getLayers().get(1).setVisible(!mapa.getLayers().get(1).isVisible());

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //Vector en 3 dimensiones que recoge donde se ha pulsado la pantalla
        Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
        //Transforma las coordenadas del vector a las de la camara
        Vector3 posicion = camara.unproject(clickCoordinates);

        //Ponemos a cero la animacion
        stateTime = 0;

        //​Guardamos la posición anterior del jugador por si al desplazarlo se topa
        // ​con un obstáculo y podamos volverlo a la posición anterior.
        float jugadorAnteriorX = jugadorX;
        float jugadorAnteriorY = jugadorY;

        //Si se ha pulsado por encima de la animacion, se sube en 5 pixeles y se reproduce la
        //animacion del jugador moviendose hacia arriba
        if (jugadorY < posicion.y) {
            jugadorY += 5;
            jugador = jugadorArriba;
        } else if (jugadorY > posicion.y) {
            jugadorY -= 5;
            jugador = jugadorAbajo;
        }
        //Movimiento derecha izquierda
        if (jugadorX < posicion.x) {
            jugadorX += 5;
            jugador = jugadorDerecha;
        } else if (jugadorX > posicion.x) {
            jugadorX -= 5;
            jugador = jugadorIzquierda;
        }

        // ​al chocar con un obstáculo el jugador vuelve a su posición inicial
        if ((jugadorX < 0 || jugadorY < 0 || jugadorX > (anchoMapa - anchoJugador) || jugadorY > (altoMapa - altoJugador)) || (obstaculo[(int) ((jugadorX + anchoJugador / 4) / anchoCelda)][((int) (jugadorY)
                / altoCelda)])
                || (obstaculo[(int) ((jugadorX + 3 * anchoJugador / 4) / anchoCelda)][((int)
                (jugadorY) / altoCelda)])) {
            jugadorX = jugadorAnteriorX;
            jugadorY = jugadorAnteriorY;

        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    //Método que permite cambiar las coordenadas del NPC en la posición "i",
// dada una variación "delta" en ambas coordenadas.
    private void actualizaNPC​(int i, float delta) {

        if (destinoY[i] > noJugadorY[i]) {
            noJugadorY[i] += delta;
            noJugador[i] = noJugadorArriba;
        }

        if (destinoY[i] < noJugadorY[i]) {
            noJugadorY[i] -= delta;
            noJugador[i] = noJugadorAbajo;
        }

        if (destinoX[i] > noJugadorX[i]) {
            noJugadorX[i] += delta;
            noJugador[i] = noJugadorDerecha;
        }

        if (destinoX[i] < noJugadorX[i]) {
            noJugadorX[i] -= delta;
            noJugador[i] = noJugadorIzquierda;
        }
    }


    private void detectaColisiones() {
        //Vamos a comprobar que el rectángulo que rodea al jugador, no se solape
        //con el rectángulo de alguno de los NPC. Primero calculamos el rectángulo
        //en torno al jugador.
        Rectangle rJugador = new Rectangle(jugadorX, jugadorY, anchoJugador, altoJugador);
        Rectangle rNPC;
        //Ahora recorremos el array de NPC, para cada uno generamos su rectángulo envolvente
        //y comprobamos si se solapa o no con el del Jugador.
        for (int i = 0; i < numeroNPCs; i++) {
            rNPC = new Rectangle(noJugadorX[i], noJugadorY[i],
                    anchoNoJugador, altoNoJugador);
            //Se comprueba si se solapan.

            if (rJugador.overlaps(rNPC)) {
                //hacer lo que haya que hacer en este caso, como puede ser reproducir un efecto
                //de sonido, una animación del jugador alternativa y, posiblemente, que este muera
                //y se acabe la partida actual. En principio, en este caso, lo único que se hace
                //es mostrar un mensaje en la consola de texto.
                System.out.println("Hay colisión!!!");
            }
        }
    }

}//FINAL DE LA CLASE PRINCIPAL DEL JUEGO
