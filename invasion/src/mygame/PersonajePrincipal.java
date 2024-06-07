package mygame;

import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PersonajePrincipal extends Main {

    private Spatial mainCharacter;
    private final Vector3f movementDirection = new Vector3f(0, 0, 0);
    private final Vector3f shootingDirection = new Vector3f(0, 0, 1); // Dirección inicial de los disparos
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
        mainCharacter = assetManager.loadModel("Models/Principial/Principial.j3o");
        mainCharacter.setLocalScale(0.5f);
        mainCharacter.setLocalTranslation(new Vector3f(-floorSize / 2 + 0.5f, 1, -floorSize / 2 + 0.5f));
        rootNode.attachChild(mainCharacter);

        // Configuración de la cámara
        cam.setLocation(new Vector3f(0, 20, 20));
        cam.lookAt(mainCharacter.getLocalTranslation(), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
    }

    private void setupKeys() {
        inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("MoveUp", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("MoveDown", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("ShootLeft", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("ShootRight", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("ShootUp", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("ShootDown", new KeyTrigger(KeyInput.KEY_LEFT));

        inputManager.addListener(actionListener, "MoveLeft", "MoveRight", "MoveUp", "MoveDown", "Shoot", "ShootLeft", "ShootRight", "ShootUp", "ShootDown");
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
            } else if (name.equals("ShootLeft")) {
                shootingDirection.set(-1, 0, 0);
            } else if (name.equals("ShootRight")) {
                shootingDirection.set(1, 0, 0);
            } else if (name.equals("ShootUp")) {
                shootingDirection.set(0, 0, -1);
            } else if (name.equals("ShootDown")) {
                shootingDirection.set(0, 0, 1);
            }
        }
    };

    private void shoot() {
        // Crea y dispara un proyectil
        Sphere sphere = new Sphere(8, 8, 0.2f);
        Geometry proyectil = new Geometry("Proyectil", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        proyectil.setMaterial(mat);

        Vector3f position = mainCharacter.getLocalTranslation().clone();
        proyectil.setLocalTranslation(position);

        rootNode.attachChild(proyectil);
        proyectiles.add(proyectil);
        disparosActivos.add(new Disparo(proyectil, shootingDirection.clone()));
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf); // Llama al método de actualización de la clase principal

        // Actualiza la posición del personaje
        Vector3f translation = mainCharacter.getLocalTranslation();
        Vector3f newTranslation = translation.add(movementDirection.mult(tpf * moveSpeed));

        // Limita el movimiento del personaje dentro de los bordes del suelo
        if (newTranslation.x > -floorSize / 2 + 0.5f && newTranslation.x < floorSize / 2 - 0.5f &&
            newTranslation.z > -floorSize / 2 + 0.5f && newTranslation.z < floorSize / 2 - 0.5f) {
            mainCharacter.setLocalTranslation(newTranslation);
        }

        // Actualiza la posición de la cámara para seguir al personaje
        Vector3f camPosition = mainCharacter.getLocalTranslation().add(10 * FastMath.cos(FastMath.PI / 6), 10, 10 * FastMath.sin(FastMath.PI / 6));
        cam.setLocation(camPosition);
        cam.lookAt(mainCharacter.getLocalTranslation(), Vector3f.UNIT_Y);

        // Actualiza los enemigos y los proyectiles
        enemigos.update(tpf, mainCharacter.getLocalTranslation());
        updateProyectiles(tpf);

        // Comprueba colisiones entre el personaje y los enemigos
        for (Geometry enemigo : enemigos.getEnemigos()) {
            if (mainCharacter.getLocalTranslation().distance(enemigo.getLocalTranslation()) < 0.5f) {
                gameOver();
                return;
            }
        }

        // Actualiza el HUD y verifica si el juego ha terminado
        updateHUD();
        checkGameOver();
    }

    private void updateProyectiles(float tpf) {
        // Actualiza la posición de los proyectiles
        Iterator<Disparo> iter = disparosActivos.iterator();
        while (iter.hasNext()) {
            Disparo disparo = iter.next();
            Geometry proyectil = disparo.getProyectil();
            Vector3f direction = disparo.getDirection();
            proyectil.move(direction.mult(tpf * proyectilSpeed));

            // Elimina el proyectil si sale del suelo
            if (proyectil.getLocalTranslation().distance(mainCharacter.getLocalTranslation()) > floorSize) {
                rootNode.detachChild(proyectil);
                iter.remove();
                continue;
            }

            // Verifica colisiones con los enemigos
            for (Geometry enemigo : enemigos.getEnemigos()) {
                if (proyectil.getLocalTranslation().distance(enemigo.getLocalTranslation()) < 0.5f) {
                    enemigos.hitEnemigo(enemigo);
                    rootNode.detachChild(proyectil);
                    iter.remove();
                    enemiesEliminated++;
                    break;
                }
            }
        }
    }

    private class Disparo {
        // Clase interna para manejar los disparos
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

    public void gameOver() {
        updateHUD();
        guiNode.detachAllChildren();
        BitmapText gameOverText = new BitmapText(guiFont, false);
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        gameOverText.setColor(ColorRGBA.Red);
        gameOverText.setText("FIN DEL JUEGO\nEnemigos eliminados: " + enemiesEliminated);
        gameOverText.setLocalTranslation(settings.getWidth() / 2 - gameOverText.getLineWidth() / 2, settings.getHeight() / 2 + gameOverText.getLineHeight() / 2, 0);
        guiNode.attachChild(gameOverText);
        stop();
    }
}
