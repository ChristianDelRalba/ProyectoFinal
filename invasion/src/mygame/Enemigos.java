package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemigos {

    private final Main app;
    private final List<Geometry> enemigos;
    private final Random random;
    private final float spawnInterval = 15f;
    private float timeSinceLastSpawn = 0f;
    private final int maxEnemigos = 5;
    private final Vector3f[] spawnPositions;

    public Enemigos(Main app) {
        this.app = app;
        this.enemigos = new ArrayList<>();
        this.random = new Random();
        this.spawnPositions = new Vector3f[]{
            new Vector3f(-app.floorSize / 2, 0.5f, -app.floorSize / 2),
            new Vector3f(app.floorSize / 2, 0.5f, -app.floorSize / 2),
            new Vector3f(-app.floorSize / 2, 0.5f, app.floorSize / 2),
            new Vector3f(app.floorSize / 2, 0.5f, app.floorSize / 2)
        };
    }

    public void update(float tpf, Vector3f playerPosition) {
        timeSinceLastSpawn += tpf;
        if (timeSinceLastSpawn >= spawnInterval && enemigos.size() < maxEnemigos) {
            spawnEnemigo();
            timeSinceLastSpawn = 0f;
        }

        for (Geometry enemigo : enemigos) {
            Vector3f direction = playerPosition.subtract(enemigo.getLocalTranslation()).normalize();
            enemigo.move(direction.mult(tpf * 2f)); // Velocidad del enemigo
        }
    }

    private void spawnEnemigo() {
        Sphere sphere = new Sphere(16, 16, 0.5f);
        Geometry enemigo = new Geometry("Enemigo", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        try {
            Texture tex = app.getAssetManager().loadTexture("Textures/enemigo.jpg");
            mat.setTexture("ColorMap", tex);
        } catch (Exception e) {
            System.out.println("Error loading texture: " + e.getMessage());
            mat.setColor("Color", ColorRGBA.Red);
        }
        enemigo.setMaterial(mat);

        Vector3f spawnPosition = spawnPositions[random.nextInt(spawnPositions.length)];
        enemigo.setLocalTranslation(spawnPosition);
        app.getRootNode().attachChild(enemigo);
        enemigos.add(enemigo);
    }
}
