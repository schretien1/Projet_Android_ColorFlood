package p8.demo.p8sokoban;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    static int compteur=0;

    // Declaration des images
    private Bitmap 		block;
    private Bitmap 		block2;
    private Bitmap 		block3;
    private Bitmap 		block4;
    private Bitmap 		block5;
    private Bitmap 		block6;

    private Bitmap 		Cbleu;
    private Bitmap 		Crouge;
    private Bitmap 		Cvert;
    private Bitmap 		Corange;
    private Bitmap 		Cviolet;
    private Bitmap 		Cjaune;

    private Bitmap[] 	zone = new Bitmap[4];
    private Bitmap 		up;
    private Bitmap 		down;
    private Bitmap 		left;
    private Bitmap 		right; 
    private Bitmap 		win;

    private Bitmap      nouv;
    private Bitmap      facile;
    private Bitmap      normal;
    private Bitmap      difficile;

    private Bitmap      facile1;
    private Bitmap      normal1;
    private Bitmap      difficile1;

    private Bitmap      titre;
    private Bitmap      play;
    private Bitmap      score;
    private Bitmap      meilleurscore;

    //public Canvas c;

    int NbCouleurs;
    private Bitmap      gagne;
    boolean isFull;

    int AppuiePlay = 0;
    boolean AppuieGagne;

    boolean nb2 = false;
    boolean nb4 = false;
    boolean nb6 = false;

    boolean niv1 = false;
    boolean niv2 = false;
    boolean niv3 = false;


    // Declaration des objets Ressources et Context permettant d'acc�der aux ressources de notre application et de les charger
    private Resources 	mRes;    
    private Context 	mContext;

    // tableau modelisant la carte du jeu
    int[][] carte;
    int Case0;

    p8_Sokoban instance =new p8_Sokoban();

    Canvas Canvas2;
    
    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonn�es en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonn�es en X du point d'ancrage de notre carte

    // taille de la carte
    static final int    carteWidth    = 10;
    static final int    carteHeight   = 10;
    static final int    carteTileSize = 20;

    // constante modelisant les differentes types de cases
    static final int    CST_block   = 0;
    static final int    CST_block2  = 1;
    static final int    CST_block3  = 2;
    static final int    CST_block4  = 3;
    static final int    CST_block5  = 4;
    static final int    CST_block6  = 5;

    // position de reference des diamants
    // position de reference du joueur
    int refxPlayer = 4;
    int refyPlayer = 1;    

    // position courante des diamants


    // position courante du joueur
        int xPlayer = 4;
        int yPlayer = 1;
        
        /* compteur et max pour animer les zones d'arriv�e des diamants */
        int currentStepZone = 0;
        int maxStepZone     = 4;  

        // thread utiliser pour animer les zones de depot des diamants
        private     boolean in      = true;
        private     Thread  cv_thread;
        SurfaceHolder holder;

        int MaxCase = carteWidth * carteHeight;

    Paint paint;
        
    /**
     * The constructor called from the main JetBoy activity
     * 
     * @param context 
     * @param attrs 
     */
    public SokobanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        
        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed        
    	holder = getHolder();
        holder.addCallback(this);    
        
        // chargement des images
        mContext	= context;
        mRes 		= mContext.getResources();        
        block 		= BitmapFactory.decodeResource(mRes, R.drawable.bleu);
        block2 		= BitmapFactory.decodeResource(mRes, R.drawable.rouge);
        block3 		= BitmapFactory.decodeResource(mRes, R.drawable.vert);
        block4 		= BitmapFactory.decodeResource(mRes, R.drawable.orange);
        block5 		= BitmapFactory.decodeResource(mRes, R.drawable.violet);
        block6 		= BitmapFactory.decodeResource(mRes, R.drawable.jaune);

        nouv        =BitmapFactory.decodeResource(mRes, R.drawable.nouveau);
        titre       =BitmapFactory.decodeResource(mRes, R.drawable.titre);
        play        =BitmapFactory.decodeResource(mRes, R.drawable.play);
        score        =BitmapFactory.decodeResource(mRes, R.drawable.score1);
        meilleurscore        =BitmapFactory.decodeResource(mRes, R.drawable.meilleurscore);

        gagne       =BitmapFactory.decodeResource(mRes, R.drawable.gagne);

        Cbleu 		= BitmapFactory.decodeResource(mRes, R.drawable.b11);
        Crouge 		= BitmapFactory.decodeResource(mRes, R.drawable.b21);
        Cvert 		= BitmapFactory.decodeResource(mRes, R.drawable.b31);
        Corange 		= BitmapFactory.decodeResource(mRes, R.drawable.b41);
        Cviolet 		= BitmapFactory.decodeResource(mRes, R.drawable.b51);
        Cjaune 		= BitmapFactory.decodeResource(mRes, R.drawable.b61);

        facile        =BitmapFactory.decodeResource(mRes, R.drawable.facile);
        normal        =BitmapFactory.decodeResource(mRes, R.drawable.normal);
        difficile        =BitmapFactory.decodeResource(mRes, R.drawable.difficile);

        facile1        =BitmapFactory.decodeResource(mRes, R.drawable.facile1);
        normal1       =BitmapFactory.decodeResource(mRes, R.drawable.normal1);
        difficile1        =BitmapFactory.decodeResource(mRes, R.drawable.difficile1);


    	// initialisation des parmametres du jeu
    	initparameters();

    	// creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);
    }


    /**
     * Remplir le tableau aleatoirement
     * @param NombreCouleurs
     */
    public void fillAlea(int NombreCouleurs)
    {
        NbCouleurs = NombreCouleurs;
        int nbAleatHauteur;
        int nbAleatLargeur;
        Random rand = new Random();
        int nbAleat;
        int nbCases = 0;

        if(nbCases == MaxCase) { nbCases = 0; }

        do {

            nbAleatHauteur = rand.nextInt((carteHeight-1) - 0 + 1) ;

            nbAleatLargeur = rand.nextInt((carteWidth-1) - 0 + 1) ;

            nbAleat = rand.nextInt(NombreCouleurs);
            //nombre = (int) Math.random() * (4);
            if (nbAleat == 0) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_block;
                nbCases++;
            }
            if (nbAleat == 1) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_block4;
                nbCases++;
            }
            if (nbAleat == 2) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_block5;
                nbCases++;
            }
            if (nbAleat == 3) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_block2;
                nbCases++;
            }
            if (nbAleat == 4) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_block3;
                nbCases++;
            }
            if(nbAleat == 5){
                carte[nbAleatLargeur][nbAleatHauteur] = CST_block6;
                nbCases++;
            }
        }while(nbCases != MaxCase);
    }


    /**
     * initialisation du jeu
     */
    public void initparameters()
    {
    	paint = new Paint();
    	paint.setColor(0xff0000);
    	
    	paint.setDither(true);
    	paint.setColor(0xFFFFFF00);
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeJoin(Paint.Join.ROUND);
    	paint.setStrokeCap(Paint.Cap.ROUND);
    	paint.setStrokeWidth(3);    	
    	paint.setTextAlign(Paint.Align.LEFT);
        carte           = new int[carteHeight][carteWidth];
        //loadlevel();
        fillAlea(6);
        carteTopAnchor  = (getHeight()- carteHeight*carteTileSize)/2;
        carteLeftAnchor = (getWidth()- carteWidth*carteTileSize)/2;
        xPlayer = refxPlayer;
        yPlayer = refyPlayer;

        if ((cv_thread!=null) && (!cv_thread.isAlive())) {        	
        	cv_thread.start();
        	Log.e("-FCT-", "cv_thread.start()");
        }
    }

    /**
     * Teste si on a fini le jeu
     * C'est a dire que toutes les cases doivent avoir une seul et même couleur
     * @param couleur
     * @return
     */
    private boolean TestGagne(int couleur)
    {
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                if(carte[j][i] != couleur)
                    return isFull = false;
            }
        }
        return isFull = true;
    }

    /**
     * dessine les bouton du niveau 1
     * @param canvas
     */
    private void paintar2Couleur(Canvas canvas) {
    	canvas.drawBitmap(Cbleu, ((getWidth()- Cbleu.getWidth())/2)-100, 450, null);
        canvas.drawBitmap(Corange, ((getWidth()- Cbleu.getWidth())/2)-100, 390, null);
    }

    /**
     * dessine les bouton du niveau 2
     * @param canvas
     */
    private void paintar4Couleur(Canvas canvas){
        canvas.drawBitmap(Cbleu, ((getWidth()- Cbleu.getWidth())/2)-100, 450, null);
        canvas.drawBitmap(Corange, ((getWidth()- Cbleu.getWidth())/2)-100, 390, null);
        canvas.drawBitmap(Cviolet, (getWidth()- Cbleu.getWidth())/2, 390, null);
        canvas.drawBitmap(Crouge, (getWidth()- Cbleu.getWidth())/2, 450, null);
    }

    /**
     * dessine les bouton du niveau 3
     * @param canvas
     */
    private void paintar6Couleur(Canvas canvas){
        canvas.drawBitmap(Cbleu, ((getWidth()- Cbleu.getWidth())/2)-100, 450, null);
        canvas.drawBitmap(Corange, ((getWidth()- Cbleu.getWidth())/2)-100, 390, null);
        canvas.drawBitmap(Cviolet, (getWidth()- Cbleu.getWidth())/2, 390, null);
        canvas.drawBitmap(Crouge, (getWidth()- Cbleu.getWidth())/2, 450, null);
        canvas.drawBitmap(Cjaune, ((getWidth()- Cbleu.getWidth())/2)+100, 390, null);
        canvas.drawBitmap(Cvert, ((getWidth()- Cbleu.getWidth())/2)+100, 450, null);

    }

    /**
     * dessine les bouton des niveaux
     * @param canvas
     */
    private void paintarrowheader(Canvas canvas){
        canvas.drawBitmap(facile, 0,100,null);
        canvas.drawBitmap(normal, (getWidth()-normal.getWidth())/2,100,null);
        canvas.drawBitmap(difficile, getWidth()-difficile.getWidth(),100,null);
    }

    /**
     * dessine image "valide" sous le niveau Facile
     * @param canvas
     */
    private void paintFacile(Canvas canvas) {
        canvas.drawBitmap(facile1, 0,100,null);
    }

    /**
     * dessine image "valide" sous le niveau Normal
     * @param canvas
     */
    private void paintNormal(Canvas canvas) {
        canvas.drawBitmap(normal1, (getWidth()-normal1.getWidth())/2,100,null);

    }

    /**
     * dessine image "valide" sous le niveau Difficile
     * @param canvas
     */
    private void paintDifficile(Canvas canvas) {
        canvas.drawBitmap(difficile1, getWidth()-difficile1.getWidth(),100,null);
    }

    /**
     * Affiche le titre en haut et le bouton play au milieu
     * @param canvas
     */
    private void paintarrowplay(Canvas canvas){
        canvas.drawBitmap(titre, (getWidth()-titre.getWidth())/2,0,null);
        canvas.drawBitmap(play, (getWidth()-play.getWidth())/2,(getHeight()-play.getHeight())/2 + 20 ,null);

    }

    /**
     * dessin du "gagne" si on gagne la partie
     * @param canvas
     */
    private void paintwin(Canvas canvas) {
    	canvas.drawBitmap(gagne, (getWidth()-gagne.getWidth())/2,(getHeight()-gagne.getHeight())/2 + 20, null);
    }    

    /**
     * dessin de la carte du jeu
     * @param canvas
     */
    private void paintcarte(Canvas canvas) {
    	for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                switch (carte[i][j]) {
                    case CST_block:
                    	canvas.drawBitmap(block, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                    	break;
                    case CST_block2:
                        canvas.drawBitmap(block2, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block3:
                        canvas.drawBitmap(block3, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block4:
                        canvas.drawBitmap(block4, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block5:
                        canvas.drawBitmap(block5, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block6:
                        canvas.drawBitmap(block6, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;

                }
            }
        }
    }

    /**
     * Appel de la fonction qui affiche le bouton play
     * @param canvas
     */
    private void showFirst(Canvas canvas) {
        paintarrowplay(canvas);
    }

    /**
     * Affiche la table de jeu , les niveaux et les boutons
     * @param canvas
     */
    private void showSecond(Canvas canvas) {
        paintcarte(canvas);
        paintarrowheader(canvas);

        if(niv1 == true && niv2 == false && niv3 == false)
            paintFacile(canvas);
        if(niv1 == false && niv2 == true && niv3 == false)
            paintNormal(canvas);
        if(niv1 == false && niv2 == false && niv3 == true)
            paintDifficile(canvas);

        if(nb2 == true && nb4 == false && nb6 == false)
            paintar2Couleur(canvas);
        if(nb2 == true && nb4 == true && nb6 == false)
            paintar4Couleur(canvas);
        if(nb2 == true && nb4 == true && nb6 == true)
            paintar6Couleur(canvas);
    }


    /**
     * dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau)
     * @param canvas
     */
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(255,255, 255);

        showFirst(canvas);
        if(AppuiePlay==1){ // si on a bien appuyer sur play , on execute la fonction showSecond
            showSecond(canvas);
        }
    }

    /**
     * Va changer la couleur de la première case en fonction de la couleur choisi
     * Ensuite on teste si les cases autours d'elle sont de la même couleur que l'ancienne
     * Si oui on change ses cases à la couleur choisi
     * @param x
     * @param y
     * @param OldColor
     * @param NewColor
     */
    public void ColorFlood(int x, int y, int OldColor, int NewColor)
    {
        if(Canvas2 == null)
        {
            Canvas2 = holder.lockCanvas(null);
        }
        if ((x < 0) || (x >= carteWidth)) return;
        if ((y < 0) || (y >= carteHeight)) return;
        if (carte[x][y] == OldColor) {
            carte[x][y] = NewColor;
            nDraw(Canvas2);

            ColorFlood(x + 1, y, OldColor, NewColor);
            nDraw(Canvas2);

            ColorFlood(x, y + 1, OldColor, NewColor);
            nDraw(Canvas2);

            ColorFlood(x - 1, y, OldColor, NewColor);
            nDraw(Canvas2);

            ColorFlood(x, y - 1, OldColor, NewColor);
        }
    }

    /**
     * callback sur le cycle de vie de la surfaceview
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged "+ width + " - " + height);
        initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }

    
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }    


    /**
     * run (run du thread cree)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on libere le canvas
     */
    public void run() {
    	Canvas c = null;

        while (in) {
            try {
                cv_thread.sleep(40);
                currentStepZone = (currentStepZone + 1) % maxStepZone;
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);

                } finally {
                	if (c != null) {
                		holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
            	Log.e("-> RUN <-", "PB DANS RUN");
            }
        }

    }

    /**
     * fonction permettant de recuperer les evenements tactiles
     * @param event
     * @return
     */
     public boolean onTouchEvent (MotionEvent event) {
         Log.i("-> FCT <-", "onTouchEvent X: "+ event.getX());
         Log.i("-> FCT <-", "onTouchEvent Y: "+ event.getY());

         if(event.getAction() ==  MotionEvent.ACTION_DOWN && isFull == true)
         {

                 Canvas Canvas3;
                 Canvas3 = null;
                 Canvas3 = holder.lockCanvas(null);
                 paintwin(Canvas3);

                 AppuieGagne = true;
                 holder.unlockCanvasAndPost(Canvas3);


         }

        //////////////////////////////////

        if(event.getY()>160 && event.getY()<360){ // BOUTON PLAY
            AppuiePlay = 1;
        }

        /////////////////////// Bouton Niveau //////////////////////
        // Bouton Facile

        //on rempli la carte aleatoirement avec les 2 première couleurs
        //On fait apperaitre que les 2 première couleur et on affiche l'image "valide" en bas de l'image Facile
        if(event.getX() > 0  && event.getX() < 80 && event.getY() >99  && event.getY() < 122) {
            for (int i = 0; i < 6; i++) {
                fillAlea(2);
                instance.LanceChrono();

            }

            niv1 = true;
            niv2 = false;
            niv3 = false;

            nb2 = true;
            nb4 = false;
            nb6 = false;
        }


        // Bouton Normal

        //on rempli la carte Aleatoirement avec les 4 premières couleurs
        //On fait apperaitre que les 4 première couleur et on affiche l'image "valide" en bas du niveau normal
        if(event.getX() > 120  && event.getX() < 200 && event.getY() >99  && event.getY() < 122)
        {
            for (int i = 0; i < 6; i++) {
                fillAlea(4);
                instance.LanceChrono();

            }

            niv1 = false;
            niv2 = true;
            niv3 = false;

            nb2 = true;
            nb4 = true;
            nb6 = false;
        }

        // Bouton Difficile

        //on rempli la carte Aleatoirement avec les 6 couleurs
        //On fait apperaitre que les 6 couleur et on affiche l'image "valide" en bas du niveau Difficile
        if(event.getX() > 240  && event.getX() < 320 && event.getY() > 99  && event.getY() < 122)
        {
            fillAlea(6);
            instance.LanceChrono();


            niv1 = false;
            niv2 = false;
            niv3 = true;

            nb2 = true;
            nb4 = true;
            nb6 = true;
        }

        /////////////////////// Bouton Couleur /////////////////////

        int i,j;

        Case0 = carte[0][0];
        Canvas2 = null;

        AppuieGagne = false;


        if(AppuieGagne == true && event.getY()> 160 && event.getY()<360) {
            initparameters();
        }


        // Affiche que les bouttons du niveau 1 : Orange et Bleu
        if(NbCouleurs == 2) {

            if (event.getX() > 35 && event.getX() < 75 && event.getY() > 390 && event.getY() < 433) {
                // carre orange CST_block4
                if(Case0 != CST_block4) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block4);
                    compteur++;
                    TestGagne(CST_block4);
                }
            }

            if (event.getX() > 41 && event.getX() < 80 && event.getY() > 451 && event.getY() < 492) {
                // carre bleu CST_block
                if(Case0 != CST_block) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block);
                    compteur++;
                    TestGagne(CST_block);
                }
            }
        }

        // Affiche que les bouttons du niveau 2 : Orange, Bleu, violet, rouge
        if (NbCouleurs == 4) {

            if (event.getX() > 35 && event.getX() < 75 && event.getY() > 390 && event.getY() < 433) {
                // carre orange CST_block4
                if(Case0 != CST_block4) {

                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block4);
                    compteur++;
                    TestGagne(CST_block4);
                }

            }

            if (event.getX() > 41 && event.getX() < 80 && event.getY() > 451 && event.getY() < 492) {
                // carre bleu CST_block
                if(Case0 != CST_block) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block);
                    compteur++;
                    TestGagne(CST_block);
                }
            }

            if (event.getX() > 142 && event.getX() < 180 && event.getY() > 390 && event.getY() < 433) {
                // carre violet CST_block5
                if(Case0 != CST_block5) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block5);
                    compteur++;
                    TestGagne(CST_block5);
                }
            }
            if (event.getX() > 140 && event.getX() < 180 && event.getY() > 451 && event.getY() < 495) {
                // carre rouge CST_block2
                if(Case0 != CST_block2) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block2);
                    compteur++;
                    TestGagne(CST_block2);
                }
            }
        }

        // Affiche que les bouttons du niveau 3 : Orange, Bleu, violet, rouge, jaune, vert
        if (NbCouleurs == 6) {

            if (event.getX() > 35 && event.getX() < 75 && event.getY() > 390 && event.getY() < 433) {
                // carre orange CST_block4
                if(Case0 != CST_block4) {

                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block4);
                    compteur++;
                    TestGagne(CST_block4);
                }
            }

            if (event.getX() > 41 && event.getX() < 80 && event.getY() > 451 && event.getY() < 492) {
                // carre bleu CST_block
                if(Case0 != CST_block) {

                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block);
                    compteur++;
                    TestGagne(CST_block);
                }
            }

            if (event.getX() > 142 && event.getX() < 180 && event.getY() > 390 && event.getY() < 433) {
                // carre violet CST_block5
                if(Case0 != CST_block5) {

                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block5);
                    compteur++;
                    TestGagne(CST_block5);
                }
            }
            if (event.getX() > 140 && event.getX() < 180 && event.getY() > 451 && event.getY() < 495) {
                // carre rouge CST_block2
                if(Case0 != CST_block2) {

                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block2);
                    compteur++;
                    TestGagne(CST_block2);
                }
            }

            if (event.getX() > 240 && event.getX() < 280 && event.getY() > 390 && event.getY() < 433) {
                // carre jaune CST_block6
                if(Case0 != CST_block6) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block6);
                    compteur++;
                    TestGagne(CST_block6);
                }
            }

            if (event.getX() > 239 && event.getX() < 279 && event.getY() > 447 && event.getY() < 487) {
                // carre vert CST_block3
                if(Case0 != CST_block3) {
                    isFull = true;
                    ColorFlood(0, 0, Case0, CST_block3);
                    compteur++;
                    TestGagne(CST_block3);
                }
            }

        }

        if (Canvas2 != null)
        {
            holder.unlockCanvasAndPost(Canvas2);
        }

        Log.i("-> FCT <-", " compteur: "+ compteur);

        return super.onTouchEvent(event);
    }

}
