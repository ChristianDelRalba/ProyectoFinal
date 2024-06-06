package mygame;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PersonajePrincipal extends Main {

    private Geometry movingBox;
    private final Vector3f movementDirection = new Vector3f(0, 0, 0);
    private final float moveSpeed = 5f;
    private Enemigos enemigos;
    private List<Geometry> proyectiles;
    private final float proyectilSpeed = 10f;
    private List<Disparo> disparosActivos;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp(); // Llamar al método de inicialización de la clase principal
        addMainCharacter();
        setupKeys();
        enemigos = new Enemigos(this);
        proyectiles = new ArrayList<>();
        disparosActivos = new ArrayList<>();
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
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(actionListener, "MoveLeft", "MoveRight", "MoveUp", "MoveDown", "Shoot");
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
            } else if (name.equals("Shoot") && isPressed) {
                shoot();
            }
        }
    };

    private void shoot() {
        Sphere sphere = new Sphere(8, 8, 0.2f);
        Geometry proyectil = new Geometry("Proyectil", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        proyectil.setMaterial(mat);

        Vector3f position = movingBox.getLocalTranslation();
        proyectil.setLocalTranslation(position);

        rootNode.attachChild(proyectil);
        proyectiles.add(proyectil);
        disparosActivos.add(new Disparo(proyectil, movementDirection.clone()));
    }

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
        updateProyectiles(tpf);
    }

    private void updateProyectiles(float tpf) {
        Iterator<Disparo> iter = disparosActivos.iterator();
        while (iter.hasNext()) {
            Disparo disparo = iter.next();
            Geometry proyectil = disparo.getProyectil();
            Vector3f direction = disparo.getDirection();
            proyectil.move(direction.mult(tpf * proyectilSpeed));

            if (proyectil.getLocalTranslation().distance(movingBox.getLocalTranslation()) > floorSize) {
                rootNode.detachChild(proyectil);
                iter.remove();
                continue;
            }

            for (Geometry enemigo : enemigos.getEnemigos()) {
                if (proyectil.getLocalTranslation().distance(enemigo.getLocalTranslation()) < 0.5f) {
                    enemigos.hitEnemigo(enemigo);
                    rootNode.detachChild(proyectil);
                    iter.remove();
                    break;
                }
            }
        }
    }

    private class Disparo {
        private Geometry proyectil;
        private Vector3f direction;

        public Disparo(Geometry proyectil, Vector3f direction) {
            this.proyectil = proyectil;
            this.direction = direction;
        }

        public Geometry getProyectil() {
            return proyectil;
        }

        public Vector3f getDirection() {
            return direction;
        }
    }
}
