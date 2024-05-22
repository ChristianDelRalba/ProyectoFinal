package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class Main extends SimpleApplication {

    private Geometry movingBox;
    private final Vector3f movementDirection = new Vector3f(0, 0, 0);
    private final float moveSpeed = 5f;
    private final float floorSize = 25f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        Box floorBox = new Box(floorSize / 2, 0.1f, floorSize / 2);
        Geometry floorGeom = new Geometry("Floor", floorBox);

        Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        try {
            Texture floorTex = assetManager.loadTexture("Textures/terrain.jpg");
            floorMat.setTexture("ColorMap", floorTex);
        } catch (Exception e) {
            System.out.println("Error loading texture: " + e.getMessage());
            floorMat.setColor("Color", ColorRGBA.Brown);
        }
        floorGeom.setMaterial(floorMat);
        floorGeom.setLocalTranslation(new Vector3f(0, -0.1f, 0));
        rootNode.attachChild(floorGeom);

        Box smallBox = new Box(0.5f, 0.5f, 0.5f);
        movingBox = new Geometry("SmallBox", smallBox);
        Material smallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        movingBox.setMaterial(smallMat);
        Texture cubeTex = assetManager.loadTexture("Textures/terra.jpg");
        smallMat.setTexture("ColorMap", cubeTex);
        // Colocando en una orilla del piso
        movingBox.setLocalTranslation(new Vector3f(-floorSize / 2 + 0.5f, 1, -floorSize / 2 + 0.5f));
        rootNode.attachChild(movingBox);

        // Configuración de la cámara
        cam.setLocation(new Vector3f(0, 20, 20));
        cam.lookAt(movingBox.getLocalTranslation(), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        

        //
        //carga de modelo
        Spatial model = assetManager.loadModel("Models/rocket/rocket.j3o");
        
        // Ajusta la posición, rotación y escala del modelo si es necesario
        model.setLocalTranslation(0, 0, 0);
        model.setLocalScale(10f);

        // Añade el modelo a la escena principal
        rootNode.attachChild(model);

        createBoundaries();

        setupKeys();
        
        
        
        
    }

    private void createBoundaries() {
        float blockSize = 1.0f;
        Box boundaryBox = new Box(blockSize / 2, blockSize / 2, blockSize / 2);

        Material boundaryMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        try {
            Texture boundaryTex = assetManager.loadTexture("Textures/barrera.jpg");
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

    private void setupKeys() {
        inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("MoveUp", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("MoveDown", new KeyTrigger(KeyInput.KEY_A));

        inputManager.addListener(actionListener, "MoveLeft", "MoveRight", "MoveUp", "MoveDown");
    }

    private final ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("MoveLeft")) {
                movementDirection.x = isPressed ? -1 : 0;
            } else if (name.equals("MoveRight")) {
                movementDirection.x = isPressed ? 1 : 0;
            } else if (name.equals("MoveUp")) {
                movementDirection.z = isPressed ? -1 : 0;
            } else if (name.equals("MoveDown")) {
                movementDirection.z = isPressed ? 1 : 0;
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        
        Vector3f translation = movingBox.getLocalTranslation();
        Vector3f newTranslation = translation.add(movementDirection.mult(tpf * moveSpeed));

        if (newTranslation.x > -floorSize / 2 + 0.5f && newTranslation.x < floorSize / 2 - 0.5f &&
            newTranslation.z > -floorSize / 2 + 0.5f && newTranslation.z < floorSize / 2 - 0.5f) {
            movingBox.setLocalTranslation(newTranslation);
        }

        Vector3f camPosition = movingBox.getLocalTranslation().add(10 * FastMath.cos(FastMath.PI / 6), 10, 10 * FastMath.sin(FastMath.PI / 6));
        cam.setLocation(camPosition);
        cam.lookAt(movingBox.getLocalTranslation(), Vector3f.UNIT_Y);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }
}

