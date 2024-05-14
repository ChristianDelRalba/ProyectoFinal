package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;


public class Main extends SimpleApplication {

    private Geometry movingBox;
    private final Vector3f movementDirection = new Vector3f(0, 0, 0);
    private final float moveSpeed = 5f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        Box floorBox = new Box(10, 0.1f, 10);  
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

       
        Box smallBox = new Box(1, 1, 1);
        movingBox = new Geometry("SmallBox", smallBox);
        Material smallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        smallMat.setColor("Color", ColorRGBA.Blue);
        movingBox.setMaterial(smallMat);

        
        movingBox.setLocalTranslation(new Vector3f(0, 1, 0)); 
        rootNode.attachChild(movingBox);

       
        cam.setLocation(new Vector3f(0, 20, 0));  
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y.negate());  
        flyCam.setEnabled(false); 

        setupKeys();
    }

    private void setupKeys() {
        inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("MoveUp", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("MoveDown", new KeyTrigger(KeyInput.KEY_S));

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
        movingBox.setLocalTranslation(translation.add(movementDirection.mult(tpf * moveSpeed)));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }
}
