package mygame;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Enemigos {

    private final Main app;
    private final List<Geometry> enemigos;
    private final Random random;
    private final float spawnInterval = 15f;
    private float timeSinceLastSpawn = 0f;
    private final int maxEnemigos = 5;
    private final Vector3f[] spawnPositions;
    private final Map<Spatial, Integer> impactosEnemigos;

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
        this.impactosEnemigos = new HashMap<>();
    }

    public void update(float tpf, Vector3f playerPosition) {
        // Controla la aparición de nuevos enemigos
        timeSinceLastSpawn += tpf;
        if (timeSinceLastSpawn >= spawnInterval && enemigos.size() < maxEnemigos) {
            spawnEnemigo();
            timeSinceLastSpawn = 0f;
        }

        // Actualiza la posición de los enemigos para que se muevan hacia el jugador
        for (Spatial enemigo : enemigos) {
            Vector3f direction = playerPosition.subtract(enemigo.getLocalTranslation()).normalize();
            enemigo.move(direction.mult(tpf * 2f)); // Velocidad del enemigo
        }
    }

    private void spawnEnemigo() {
        // Carga un nuevo modelo de enemigo
        Spatial enemigo = app.getAssetManager().loadModel("Models/Enemigo/Enemigo.j3o");

        Vector3f spawnPosition = spawnPositions[random.nextInt(spawnPositions.length)];
        enemigo.setLocalTranslation(spawnPosition);
        app.getRootNode().attachChild(enemigo);
        enemigos.add((Geometry) enemigo); // Cambiado a Geometry
        impactosEnemigos.put(enemigo, 0);
    }

    public List<Geometry> getEnemigos() {
        return enemigos;
    }

    public void hitEnemigo(Spatial enemigo) {
        // Maneja el impacto de un enemigo
        int impactos = impactosEnemigos.getOrDefault(enemigo, 0);
        impactos++;
        if (impactos >= 3) {
            // Si el enemigo ha sido golpeado 3 veces, se elimina
            app.getRootNode().detachChild(enemigo);
            enemigos.remove(enemigo);
            impactosEnemigos.remove(enemigo);
        } else {
            impactosEnemigos.put(enemigo, impactos);
        }
    }
}
