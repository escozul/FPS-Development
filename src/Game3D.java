import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Game3D extends SimpleApplication
        implements ActionListener { //Need That to add listeners and implement catching the keypresses for the actions.
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player; //For movement
    private Vector3f walkDirection = new Vector3f(); //For Movement
    private boolean left = false, right = false, up = false, down = false;
    private TerrainQuad terrain;
    private Material mat_terrain;
    Geometry geom;
    public static void main(String[] args) {
        Game3D app = new Game3D();
        app.start();
    }
    private AnalogListener actionListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("MoveForward")) {
                player.setWalkDirection(player.getWalkDirection().add(0, 0, speed * tpf));
            } else if (name.equals("MoveBackward")) {
                player.setWalkDirection(player.getWalkDirection().add(0, 0, -speed * tpf));
            } else if (name.equals("MoveLeft")) {
                player.setViewDirection(player.getViewDirection().add(-speed * tpf, 0, 0));
            } else if (name.equals("MoveRight")) {
                player.setViewDirection(player.getViewDirection().add(speed * tpf, 0, 0));
            }
        }
    };


    @Override
    public void simpleInitApp() {
        /** Set up Physics */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //Uncomment for debugging.
        bulletAppState.setDebugEnabled(true);

        flyCam.setMoveSpeed(100);
        setUpKeys();

        ColorRGBA lightBlue = new ColorRGBA(0.5f, 0.5f, 1f, 1f); //Do not currently use it
        //Let's add the Sun
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        //And an ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(ambient);

        //A test box in the middle of the area
        Box box = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", box);
        //Unshaded:
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setColor("Color", ColorRGBA.Blue);
        //Lighted:
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        //Typical Sky Color-Not needed with Sky that uses the BrightSky Texture.
        //Material skyColor = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //skyColor.setColor("Color", lightBlue);

        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        //create a blue sky
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));

        //create a ground
        int width = 100;
        int height = 100;

        Mesh groundMesh = new Quad(width, height);

        Geometry ground = new Geometry("Ground", groundMesh);
        Texture groundTexture = assetManager.loadTexture("Textures/Ground/default.png");
        //Way to tile the textures:
        int repeatConst = 20;
        groundMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
                0, 0,
                repeatConst, 0,
                repeatConst, repeatConst,
                0, repeatConst,
        });
        groundTexture.setWrap(Texture.WrapMode.Repeat); // Wrap the texture around and tile it

        //Unshaded:
        //Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //groundMat.setColor("Color", ColorRGBA.Green);
        //groundMat.setTexture("ColorMap", groundTexture);
        //Shaded:
        Material groundMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        groundMat.setTexture("DiffuseMap", groundTexture);

        ground.setMaterial(groundMat);
        ground.rotate(-FastMath.HALF_PI, 0, 0);
        ground.center().move(-0, -5f, -0);
        rootNode.attachChild(ground);
    }

    /** We over-write some navigational key mappings here, so we can
     * add physics-controlled walking and jumping: */
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    /** These are our custom actions triggered by key presses.
     * We do not walk yet, we just keep track of the direction the user pressed. */
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) { left = true; } else { left = false; }
        } else if (binding.equals("Right")) {
            if (value) { right = true; } else { right = false; }
        } else if (binding.equals("Up")) {
            if (value) { up = true; } else { up = false; }
        } else if (binding.equals("Down")) {
            if (value) { down = true; } else { down = false; }
        } else if (binding.equals("Jump")) { //This does not work without physics
            //player.jump(new Vector3f(0,20f,0));
        }
    }
    float xPos = 0;
    @Override
    public void simpleUpdate(float tpf) {
        /* Interact with game events in the main loop */
        xPos += tpf;
        geom.setLocalTranslation(xPos, 0, 0);
        Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left)  { walkDirection.addLocal(camLeft); }
        if (right) { walkDirection.addLocal(camLeft.negate()); }
        if (up)    { walkDirection.addLocal(camDir); }
        if (down)  { walkDirection.addLocal(camDir.negate()); }
        //player.setWalkDirection(walkDirection); //These two lines do not work without physics. So Setup physics first.
        //cam.setLocation(player.getPhysicsLocation()); //These two lines do not work without physics. So Setup physics first.
    }
}