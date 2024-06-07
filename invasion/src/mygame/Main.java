package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.PointLight;
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
    private BitmapText hudText;
    protected int enemiesEliminated = 0;

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
        // Carga de modelo
        Spatial model = assetManager.loadModel("Models/rocket/rocket.j3o");

        // Ajusta la posición, rotación y escala del modelo si es necesario
        model.setLocalTranslation(0, 0, 0);
        model.setLocalScale(10f);

        // Añade el modelo a la escena principal
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
        hudText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        guiNode.attachChild(hudText);
    }

    public void updateHUD() {
        // Actualiza el texto del HUD con el número de enemigos eliminados
        hudText.setText("Enemigos eliminados: " + enemiesEliminated);
    }

    public void checkGameOver() {
        // Verifica si el juego ha terminado
        if (enemiesEliminated > 50) {
            hudText.setText("RESUMEN DEL JUEGO");
            hudText.setText("FIN DEL JUEGO\nEnemigos eliminados: " + enemiesEliminated);
            stop();
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        // El método simpleUpdate ahora está vacío, ya que la lógica específica
        // del juego (movimiento del personaje principal y enemigos) se maneja en PersonajePrincipal.
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Código de renderización, si es necesario
    }
}

