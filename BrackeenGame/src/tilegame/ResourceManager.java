package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.tilegame.sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private ArrayList alTiles2;
    private ArrayList alTiles3;
    private ArrayList alSpecialTiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    private Sprite Mugre3Sprite;
    private Sprite Mugre4Sprite;
    private Sprite Mugre5Sprite;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }
    
    public int GetCurrentMap() {
        return currentMap;
    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap ++ ;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
                new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);
                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < 24) {
                    if(currentMap == 1)
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                    else if(currentMap == 2)
                        newMap.setTile(x, y, (Image)alTiles2.get(tile));
                    else if(currentMap == 3)
                        newMap.setTile(x, y, (Image)alTiles3.get(tile));
                }
                //Tile especial de agua
                else if(ch == '9') {
                    newMap.setTile(x, y, (Image)alSpecialTiles.get(0));
                }
                //Tile especial de agua
                else if(ch == ';') {
                    newMap.setTile(x, y, (Image)alSpecialTiles.get(1));
                }
                //Tile especial de agua
                else if(ch == '=') {
                    newMap.setTile(x, y, (Image)alSpecialTiles.get(2));
                }
                //Tile especial de agua
                else if(ch == '@') {
                    newMap.setTile(x, y, (Image)alSpecialTiles.get(3));
                }
                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
                else if (ch == '3') {
                    addSprite(newMap, Mugre3Sprite, x, y);
                }
                else if (ch == '4') {
                    addSprite(newMap, Mugre4Sprite, x, y);
                }
                else if (ch == '5') {
                    addSprite(newMap, Mugre5Sprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        alTiles2 = new ArrayList();
        alTiles3 = new ArrayList();
        alSpecialTiles = new ArrayList();
        char ch = 'A';
        alSpecialTiles.add(loadImage("Tilesets/9.png"));
        alSpecialTiles.add(loadImage("Tilesets/;.png"));
        alSpecialTiles.add(loadImage("Tilesets/=.png"));
        alSpecialTiles.add(loadImage("Tilesets/@.png"));
        while (true) {
            String name = "Tilesets/" + ch + "_" + 1 + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            name = "Tilesets/" + ch + "_" + 3 + ".png";
            file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            alTiles2.add(loadImage(name));
            name = "Tilesets/" + ch + "_" + 5 + ".png";
            file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            alTiles3.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[10][];
        // load left-facing images
        images[0] = new Image[] {
            loadImage("Pomf/PomfW1.png"),
            loadImage("Pomf/PomfW2.png"),
            loadImage("Pomf/PomfW3.png"),
            loadImage("Enemigo2/Enemigo2W1.png"),
            loadImage("Enemigo2/Enemigo2W2.png"),
            loadImage("Enemigo2/Enemigo2W3.png"),
            loadImage("Enemigo1/Enemigo1W1.png"),
            loadImage("Enemigo1/Enemigo1W2.png"),
            loadImage("Enemigo1/Enemigo1W3.png"),
            loadImage("Enemigo3/Enemigo3W1.png"),
            loadImage("Enemigo3/Enemigo3W2.png"),
            loadImage("Enemigo3/Enemigo3W3.png"),
            loadImage("Enemigo4/Enemigo4W1.png"),
            loadImage("Enemigo4/Enemigo4W2.png"),
            loadImage("Enemigo4/Enemigo4W3.png"),
            loadImage("Enemigo6/Enemigo6W1.png"),
            loadImage("Enemigo6/Enemigo6W2.png"),
            loadImage("Enemigo6/Enemigo6W3.png"),
            
        };

        images[1] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
        }
        
        
        // load left-facing "dead" images
        images[2]= new Image[] {
            loadImage("Pomf/PomfD1.png"),
            loadImage("Pomf/PomfD2.png"),
            loadImage("Pomf/PomfD3.png"),
            loadImage("Pomf/PomfD4.png"),
            loadImage("Pomf/PomfD5.png"),
            loadImage("Pomf/PomfD6.png"),
            loadImage("Pomf/PomfD7.png"),
            loadImage("Pomf/PomfD8.png"),
            loadImage("Pomf/PomfD3.png"),
            loadImage("Enemigo2/Enemigo2D1.png"),
            loadImage("Enemigo2/Enemigo2D2.png"),
            loadImage("Enemigo2/Enemigo2D3.png"),
            loadImage("Enemigo2/Enemigo2D4.png"),
            loadImage("Enemigo2/Enemigo2D5.png"),
            loadImage("Enemigo2/Enemigo2D6.png"),
            loadImage("Enemigo2/Enemigo2D7.png"),
            loadImage("Enemigo2/Enemigo2D8.png"),
            loadImage("Enemigo2/Enemigo2D9.png"),
            loadImage("Enemigo1/Enemigo1D1.png"),
            loadImage("Enemigo1/Enemigo1D2.png"),
            loadImage("Enemigo1/Enemigo1D3.png"),
            loadImage("Enemigo1/Enemigo1D4.png"),
            loadImage("Enemigo1/Enemigo1D5.png"),
            loadImage("Enemigo1/Enemigo1D6.png"),
            loadImage("Enemigo1/Enemigo1D7.png"),
            loadImage("Enemigo1/Enemigo1D8.png"),
            loadImage("Enemigo1/Enemigo1D9.png"),
            loadImage("Enemigo3/Enemigo3D1.png"),
            loadImage("Enemigo3/Enemigo3D2.png"),
            loadImage("Enemigo3/Enemigo3D3.png"),
            loadImage("Enemigo3/Enemigo3D4.png"),
            loadImage("Enemigo3/Enemigo3D5.png"),
            loadImage("Enemigo3/Enemigo3D6.png"),
            loadImage("Enemigo3/Enemigo3D7.png"),
            loadImage("Enemigo3/Enemigo3D8.png"),
            loadImage("Enemigo3/Enemigo3D9.png"),
            loadImage("Enemigo4/Enemigo4D1.png"),
            loadImage("Enemigo4/Enemigo4D2.png"),
            loadImage("Enemigo4/Enemigo4D3.png"),
            loadImage("Enemigo4/Enemigo4D4.png"),
            loadImage("Enemigo4/Enemigo4D5.png"),
            loadImage("Enemigo4/Enemigo4D6.png"),
            loadImage("Enemigo4/Enemigo4D7.png"),
            loadImage("Enemigo4/Enemigo4D8.png"),
            loadImage("Enemigo4/Enemigo4D9.png"),
            loadImage("Enemigo6/Enemigo6D1.png"),
            loadImage("Enemigo6/Enemigo6D2.png"),
            loadImage("Enemigo6/Enemigo6D3.png"),
            loadImage("Enemigo6/Enemigo6D4.png"),
            loadImage("Enemigo6/Enemigo6D5.png"),
            loadImage("Enemigo6/Enemigo6D6.png"),
            loadImage("Enemigo6/Enemigo6D7.png"),
            loadImage("Enemigo6/Enemigo6D8.png"),
            loadImage("Enemigo6/Enemigo6D9.png"),
        };
        images[3] = new Image[images[2].length];
        for(int i=0; i<images[2].length; i++) {
            // right-facing "dead" images
            images[3][i] = getMirrorImage(images[2][i]);
        }
        
        
        // load jumping images for Pomf
        images[4] = new Image[]{
            loadImage("Pomf/PomfJ1.png"),
            loadImage("Pomf/PomfJ2.png"),
            loadImage("Pomf/PomfJ3.png"),
            loadImage("Pomf/PomfJ4.png"),
        };
        images[5] = new Image[images[4].length];
        for(int i=0; i<images[4].length; i++) {
            // right-facing jump images
            images[5][i] = getMirrorImage(images[4][i]);
        }
        //load hurt 
        
        //Estos espacios los dejo para las demas animaciones
        //(De hecho creo que necesito mas espacio (modificar la variable arriba)
        images[8] = new Image[images[0].length];

        // create creature animations
        Animation[] playerAnim = new Animation[6];
        Animation[] Mugre1Anim = new Animation[5];
        Animation[] Mugre2Anim = new Animation[5];
        Animation[] Mugre3Anim = new Animation[5];
        Animation[] Mugre4Anim = new Animation[5];
        Animation[] Mugre5Anim = new Animation[5];
        
        //Se guardan las imagenes para la animacion de caminado
        for (int i=0; i<2; i++) {
            playerAnim[i] = createWalkingAnim(images[i][0], images[i][1],
                images[i][2]);
            Mugre1Anim[i] = createWalkingAnim(
                images[i][3], images[i][4], images[i][5]);
            Mugre2Anim[i] = createWalkingAnim(
                images[i][6], images[i][7], images[i][8]);
            Mugre3Anim[i] = createWalkingAnim(images[i][9],
                images[i][10], images[i][11]);
            Mugre4Anim[i] = createWalkingAnim(images[i][12],
                images[i][13], images[i][14]);
            Mugre5Anim[i] = createWalkingAnim(images[i][15],
                images[i][16], images[i][17]);
        }
        // Se guardan las imagenes para la animacion de muerte
        for (int i=2; i<4; i++) {
            playerAnim[i] = createDeadAnim(images[i][0], images[i][1],
                images[i][2],images[i][3], images[i][4], images[i][5],
                images[i][6], images[i][7],images[i][8]);
            Mugre1Anim[i] = createDeadAnim(images[i][9], images[i][10],
                images[i][11],images[i][12], images[i][13], images[i][14],
                images[i][15], images[i][16],images[i][17]);
            Mugre2Anim[i] = createDeadAnim(images[i][18], images[i][19],
                images[i][20],images[i][21], images[i][22], images[i][23],
                images[i][24], images[i][25],images[i][26]);
            Mugre3Anim[i] = createDeadAnim(images[i][27], images[i][28],
                images[i][29],images[i][30], images[i][31], images[i][32],
                images[i][33], images[i][34],images[i][35]);
            Mugre4Anim[i] = createDeadAnim(images[i][36], images[i][37],
                images[i][38],images[i][39], images[i][40], images[i][41],
                images[i][42], images[i][43],images[i][44]);
            Mugre5Anim[i] = createDeadAnim(images[i][45], images[i][46],
                images[i][47],images[i][48], images[i][49], images[i][50],
                images[i][51], images[i][52],images[i][53]);
        }
        //Se guardan las imagenes de salto para pomf
        for (int i=4; i<6; i++) {
            playerAnim[i] = createJumpingAnim(images[i][0], images[i][1],
                    images[i][2], images[i][3]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2],playerAnim[3],playerAnim[4],playerAnim[5]);
        flySprite = new Mugre1(Mugre1Anim[0], Mugre1Anim[1],
            Mugre1Anim[2], Mugre1Anim[3]);
        grubSprite = new Mugre2(Mugre2Anim[0], Mugre2Anim[1],
            Mugre2Anim[2], Mugre2Anim[3]);
        Mugre3Sprite = new Mugre3(Mugre3Anim[0], Mugre3Anim[1],
            Mugre3Anim[2], Mugre3Anim[3]);
        Mugre4Sprite = new Mugre4(Mugre4Anim[0], Mugre4Anim[1], Mugre4Anim[2],
        Mugre4Anim[3]);
        Mugre5Sprite =new Mugre5(Mugre5Anim[0], Mugre5Anim[1], Mugre5Anim[2],
        Mugre5Anim[3]);
    }

    private Animation createWalkingAnim(Image img1, Image img2,//Se cargan las animaciones de las mugres (enemigos)
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 100);
        anim.addFrame(img2, 100);
        anim.addFrame(img3, 100);
        return anim;
    }
    
    private Animation createJumpingAnim(Image img1, Image img2, Image img3, 
            Image img4){
        Animation anim = new Animation();
        anim.addFrame(img1, 100);
        anim.addFrame(img2, 100);
        anim.addFrame(img3, 100);
        anim.addFrame(img4, 100);
        return anim;
    }
    
    private Animation createDeadAnim(Image img1,Image img2, 
            Image img3,Image img4, Image img5, Image img6, 
            Image img7,Image img8, Image img9) {
        Animation anim = new Animation();
        anim.addFrame(img1, 70);
        anim.addFrame(img2, 70);
        anim.addFrame(img3, 70);
        anim.addFrame(img4, 70);
        anim.addFrame(img5, 70);
        anim.addFrame(img6, 70);
        anim.addFrame(img7, 70);
        anim.addFrame(img8, 70);
        anim.addFrame(img9, 70);
        
        return anim;
    }


    private void loadPowerUpSprites() {//Se cargan las animaciones de los objetos a agarrar
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("/Mascaras/Mask1P.png"), 150);
        anim.addFrame(loadImage("/Mascaras/Mask1P.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(loadImage("/Mascaras/Mask2P.png"), 100);
        anim.addFrame(loadImage("/Mascaras/Mask2P.png"), 100);
        coinSprite = new PowerUp.Gota(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("/Mascaras/Mask3P.png"), 150);
        anim.addFrame(loadImage("/Mascaras/Mask3P.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
