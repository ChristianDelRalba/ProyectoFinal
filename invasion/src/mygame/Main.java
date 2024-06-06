package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class Main extends SimpleApplication {

    protected final float floorSize = 25f;
    protected BitmapText hudText; // Texto para el contador de enemigos eliminados
    protected int enemiesEliminated = 0; // Contador de enemigos eliminados

    public static void main(String[] args) {
        PersonajePrincipal app = new PersonajePrincipal();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        createFloor();
        createBoundaries();
        addModel();
        initHUD();
    }

    private void createFloor() {
        // Crea el piso de la escena
        Box floorBox = new Box(floorSize / 2, 0.1f, floorSize / 2);
        Geometry floorGeom = new Geometry("Floor", floorBox);

        Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        try {
            Texture floorTex = assetManager.loadTexture("Textures/PISO2.jpg");
            floorMat.setTexture("ColorMap", floorTex);
        } catch (Exception e) {
            System.out.println("Error loading texture: " + e.getMessage());
            floorMat.setColor("Color", ColorRGBA.Brown);
        }
        floorGeom.setMaterial(floorMat);
        floorGeom.setLocalTranslation(new Vector3f(0, -0.1f, 0));
        rootNode.attachChild(floorGeom);
    }

    private void createBoundaries() {
        // Crea las paredes que limitan la escena
        float blockSize = 1.0f;
        Box boundaryBox = new Box(blockSize / 2, blockSize / 2, blockSize / 2);

        Material boundaryMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        try {
            Texture boundaryTex = assetManager.loadTexture("Textures/PISO2.jpg");
            boundaryMat.setTexture("ColorMap", boundaryTex);
        } catch (Exception e) {
            System.out.println("Error loading texture: " + e.getMessage());
            boundaryMat.setColor("Color", ColorRGBA.Gray);
        }

        for (float i = -floorSize / 2; i <= floorSize / 2; i += blockSize) {
            for (float j = -floorSize / 2; j <= floorSize / 2; j += blockSize) {
                if (i == -floorSize / 2 || i == floorSize / 2 || j == -floorSize / 2 || j == floorSize / 2) {
                    Geometry boundaryGeom = new Geometry("Boundary", boundaryBox);
                    boundaryGeom.setMaterial(boundaryMat);
                    boundaryGeom.setLocalTranslation(i, 0.5f, j);
                    rootNode.attachChild(boundaryGeom);
                }
            }
        }
    }

    private void addModel() {
        // Carga un modelo en la escena
        Spatial model = assetManager.loadModel("Models/rocket/rocket.j3o");
        model.setLocalTranslation(0, 0, 0);
        model.setLocalScale(10f);
        rootNode.attachChild(model);
    }

    private void initHUD() {
        // Inicializa el HUD para mostrar el contador de enemigos eliminados
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setColor(ColorRGBA.White);
        hudText.setText("Enemigos eliminados: 0");
        hudText.setLocalTranslation(10, settings.getHeight() - hudText.getLineHeight(), 0);
        guiNode.attachChild(hudText);
    }

    public void updateHUD() {
        // Actualiza el texto del HUD
        hudText.setText("Enemigos eliminados: " + enemiesEliminated);
    }

    public void checkGameOver() {
        // Verifica si el juego ha terminado
        if (enemiesEliminated > 30) {
            BitmapText gameOverText = new BitmapText(guiFont, false);
            gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
            gameOverText.setColor(ColorRGBA.Red);
            gameOverText.setText("FIN DEL JUEGO. Enemigos eliminados: " + enemiesEliminated);
            gameOverText.setLocalTranslation(settings.getWidth() / 2 - gameOverText.getLineWidth() / 2,
                    settings.getHeight() / 2 + gameOverText.getLineHeight() / 2, 0);
            guiNode.attachChild(gameOverText);
            stop();
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Actualización del juego, manejado en PersonajePrincipal
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Renderizado del juego
    }
}
