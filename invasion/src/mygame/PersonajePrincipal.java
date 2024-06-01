package mygame;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class PersonajePrincipal extends Main {

    private Geometry movingBox;
    private final Vector3f movementDirection = new Vector3f(0, 0, 0);
    private final float moveSpeed = 5f;
    private Enemigos enemigos;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp(); // Llamar al método de inicialización de la clase principal
        addMainCharacter();
        setupKeys();
        enemigos = new Enemigos(this);
    }

    private void addMainCharacter() {
        Box smallBox = new Box(0.5f, 0.5f, 0.5f);
        movingBox = new Geometry("SmallBox", smallBox);
        Material smallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cubeTex = assetManager.loadTexture("Textures/terra.jpg");
        smallMat.setTexture("ColorMap", cubeTex);
        movingBox.setMaterial(smallMat);
        // Colocando en una orilla del piso
        movingBox.setLocalTranslation(new Vector3f(-floorSize / 2 + 0.5f, 1, -floorSize / 2 + 0.5f));
        rootNode.attachChild(movingBox);

        // Configuración de la cámara
        cam.setLocation(new Vector3f(0, 20, 20));
        cam.lookAt(movingBox.getLocalTranslation(), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
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
        super.simpleUpdate(tpf); // Llamar al método de actualización de la clase principal
        
        Vector3f translation = movingBox.getLocalTranslation();
        Vector3f newTranslation = translation.add(movementDirection.mult(tpf * moveSpeed));

        if (newTranslation.x > -floorSize / 2 + 0.5f && newTranslation.x < floorSize / 2 - 0.5f &&
            newTranslation.z > -floorSize / 2 + 0.5f && newTranslation.z < floorSize / 2 - 0.5f) {
            movingBox.setLocalTranslation(newTranslation);
        }

        Vector3f camPosition = movingBox.getLocalTranslation().add(10 * FastMath.cos(FastMath.PI / 6), 10, 10 * FastMath.sin(FastMath.PI / 6));
        cam.setLocation(camPosition);
        cam.lookAt(movingBox.getLocalTranslation(), Vector3f.UNIT_Y);

        enemigos.update(tpf, movingBox.getLocalTranslation());
    }
}
