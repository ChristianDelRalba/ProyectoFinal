package mygame;

import com.jme3.app.SimpleApplication;
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

    public static void main(String[] args) {
        PersonajePrincipal app = new PersonajePrincipal();
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

        //carga de modelo
        Spatial model = assetManager.loadModel("Models/rocket/rocket.j3o");
        
        // Ajusta la posición, rotación y escala del modelo si es necesario
        model.setLocalTranslation(0, 0, 0);
        model.setLocalScale(10f);

        // Añade el modelo a la escena principal
        rootNode.attachChild(model);

        createBoundaries();
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

    @Override
    public void simpleUpdate(float tpf) {
        // Actualizaciones generales
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }
}
