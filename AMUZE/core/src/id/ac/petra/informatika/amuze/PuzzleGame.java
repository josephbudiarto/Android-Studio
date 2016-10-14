package id.ac.petra.informatika.amuze;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;

public class PuzzleGame extends ApplicationAdapter {
    private final Callback callback;
    private String imagePath;
    private String mId;
    float winDelay = 2;
    SpriteBatch batch;
    Texture img;
    TextureRegion[][] puzzledImages, correctImages;
    int[][] initialPosition, currentPosition;
    int gridWidth, gridHeight;
    int tileWidth, tileHeight;
    int cRow, cCol;
    public static int gameGridHeight, gameGridWidth;

    public interface Callback {
        public void onWin(String a);
    }

    public PuzzleGame(Callback c, String path, String id) {
        this.callback = c;
        imagePath = path;
        mId = id;
    }

    public void print(String msg) {
        Gdx.app.debug("DEBUG_SLIDING_PUZZLE", msg);
    }

    public void loadImage(String path) {
        img = new Texture(path);
    }

    public void swapTile(int row1, int col1, int row2, int col2){
        TextureRegion temp = puzzledImages[row1][col1];
        puzzledImages[row1][col1] = puzzledImages[row2][col2];
        puzzledImages[row2][col2] = temp;

        int tempInt = currentPosition[row1][col1];
        currentPosition[row1][col1] = currentPosition[row2][col2];
        currentPosition[row2][col2] = tempInt;
    }

    public void swipeUP(){
        if (cRow - 1 >= 0) {
            swapTile(cRow, cCol, --cRow, cCol);
        }
    }
    public void swipeRight(){
        if (cCol + 1 < gridWidth) {
            swapTile(cRow, cCol, cRow, ++cCol);
        }
    }
    public void swipeDown(){
        if (cRow + 1 < gridHeight) {
            swapTile(cRow,cCol,++cRow,cCol);
        }
    }
    public void swipeLeft(){
        if (cCol - 1 >= 0) {
            swapTile(cRow, cCol, cRow, --cCol);
        }
    }

    public void setGrid(int _width, int _height){
        //Setup
        gridWidth = _width;
        gridHeight = _height;
        tileWidth = img.getWidth()/gridWidth;
        tileHeight = img.getHeight()/gridHeight;
        correctImages = new TextureRegion[gridHeight][gridWidth];
        puzzledImages = new TextureRegion[gridHeight][gridWidth];

        correctImages = TextureRegion.split(img,tileWidth, tileHeight);
        puzzledImages = TextureRegion.split(img, tileWidth, tileHeight);
        initialPosition = new int [gridHeight][gridWidth];
        currentPosition = new int [gridHeight][gridWidth];
        int number = 0;
        for(int i=0;i<gridHeight;i++){
            for(int j=0;j<gridWidth;j++){
                initialPosition[i][j] = number++;
                currentPosition[i][j] = initialPosition[i][j];
            }
        }
        correctImages[gridHeight-1][gridWidth-1] = new TextureRegion(new Texture("Images/blackTile.png"));
        puzzledImages[gridHeight-1][gridWidth-1] = new TextureRegion(new Texture("Images/blackTile.png"));
        cCol = gridWidth-1;
        cRow = gridHeight-1;
        for(int totalMovement=0; totalMovement < 100;){
            int action = (int)(Math.floor(Math.random()*4));
            if(action==0) {swipeUP();totalMovement++;}
            else if(action==1) {swipeDown();totalMovement++;}
            else if(action==2) {swipeLeft();totalMovement++;}
            else if(action==3) {swipeRight();totalMovement++;}
        }

		/*
		boolean[][] selected = new boolean[gridHeight][gridWidth];
		for(int i=0;i<gridHeight;i++){
			for(int j=0;j<gridWidth;j++){
				selected[i][j]=false;
			}
		}*/

        //Scramble the images

//		for(int i=0;i<gridHeight;i++){
//			for(int j=0;j<gridWidth;j++){
//				int randRow = (int) (Math.random() * gridHeight);
//				int randCol = (int) (Math.random() * gridWidth);
//				while(selected[randRow][randCol]){
//					randRow = (int) (Math.random() * gridHeight);
//					randCol = (int) (Math.random() * gridWidth);
//				}
//				puzzledImages[i][j] = correctImages[randRow][randCol];
//				selected[randRow][randCol] = true;
//			}
//		}
    }

    public void setPuzzleSize(int w, int h){
        gameGridWidth = w;
        gameGridHeight = h;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        print(imagePath);
        batch = new SpriteBatch();
        img = new Texture(Gdx.files.local(imagePath));
        //loadImage("Images/sliding_puzzle01.png");
        //loadImage(Gdx.files.absolute("abc"));
        setGrid(gameGridWidth, gameGridHeight);
        Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {
            @Override
            public void onUp() {
                swipeUP();
            }

            @Override
            public void onRight() {
                swipeRight();
            }

            @Override
            public void onLeft() {
                swipeLeft();
            }

            @Override
            public void onDown() {
                swipeDown();
            }
        }));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int size = Gdx.graphics.getWidth()/gameGridWidth;
        int staryY  = (int) (Gdx.graphics.getHeight() * 0.8);
        batch.begin();
        for(int i=0;i<gameGridWidth;i++){
            for(int j=0;j<gameGridWidth;j++){
                batch.draw(puzzledImages[i][j], j*size, (staryY-size) - i*size, size, size);
            }
        }
        batch.draw(img, 0, staryY, Gdx.graphics.getWidth(), 5);

        boolean win = true;
        for(int i = 0 ;i<gameGridHeight && win;i++){
            for(int j=0;j<gameGridWidth && win;j++){
                if(initialPosition[i][j] != currentPosition[i][j]){
                    win = false;
                }
            }
        }
        if (win) {
            print("something");
            //draw sesuatu, tunggu 2 detik
            winDelay-=Gdx.graphics.getDeltaTime();
            BitmapFont font = new BitmapFont();
            font.getData().scale(10f);
            Color black = new Color(0f,0f,0f,1);

            font.setColor(black);
            font.draw(batch,"You Win",20f,150f);
            if(winDelay<0){
                callback.onWin(mId);
                Gdx.app.exit();
            }

        }
        batch.end();
    }





    public static class SimpleDirectionGestureDetector extends GestureDetector {
        public interface DirectionListener {
            void onLeft();

            void onRight();

            void onUp();

            void onDown();
        }

        public SimpleDirectionGestureDetector(DirectionListener directionListener) {
            super(new DirectionGestureListener(directionListener));
        }

        private static class DirectionGestureListener extends GestureAdapter {
            DirectionListener directionListener;

            public DirectionGestureListener(DirectionListener directionListener) {
                this.directionListener = directionListener;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) {
                        directionListener.onRight();
                    } else {
                        directionListener.onLeft();
                    }
                } else {
                    if (velocityY > 0) {
                        directionListener.onDown();
                    } else {
                        directionListener.onUp();
                    }
                }
                return super.fling(velocityX, velocityY, button);
            }

        }

    }
}


/*
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class PuzzleGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
*/