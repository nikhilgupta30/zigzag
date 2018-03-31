package com.zigzag.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Random;

public class zigzag extends ApplicationAdapter {
	SpriteBatch batch;

	Random rand;

	int noOfPillars = 50;
	Texture pillar[];
	//int x[];
	//int y[];
	ArrayList<Integer> x;
	ArrayList<Integer> y;
	int x0 = 0;
	int y0 = 0;
	int xLast;
	int yLast;

//	Texture pillarTop[];
//	int xTop[];
//	int yTop[];
//	int x0Top = 0;
//	int y0Top = 184;

	int xNext = 91;
	int yNext = 65;

	int pillarVelocity = 5;

	Texture ball;
	int ballX;
	int ballY;
	int ballVelocity = 7;

	int gameState = 0;

	Texture gameOver;

	int score = 0;

	@Override
	public void create () {

		rand = new Random();

		batch = new SpriteBatch();
		pillar = new Texture[noOfPillars];
//		x = new int[noOfPillars];
//		y = new int[noOfPillars];
		x = new ArrayList<Integer>();
		y = new ArrayList<Integer>();

		for(int i=0;i<noOfPillars;i++){
			pillar[i] = new Texture("pillar.png");
		}

//		pillarTop = new Texture[noOfPillars];
//		xTop = new int[noOfPillars];
//		yTop = new int[noOfPillars];
//
//		for(int i=0;i<noOfPillars;i++){
//			pillarTop[i] = new Texture("pillarTop.png");
//		}

		ball = new Texture("ball.png");

		gameOver = new Texture("gameover.png");

		startGame();
	}

	public void startGame(){
//		x[0] = x0;
//		y[0] = y0;
		x.add(x0);
		y.add(y0);

//		xTop[0] = x0Top;
//		yTop[0] = y0Top;



		for (int i=1;i<noOfPillars;i++) {

			if(x.get(i-1)-xNext < 0){
				x.add(x.get(i-1) + xNext);
			}
			else if( x.get(i-1)+xNext+pillar[i].getWidth() > Gdx.graphics.getWidth() ){
				x.add(x.get(i-1) - xNext);
			}
			else{
				x.add(x.get(i-1) + xNext*(rand.nextInt(2)*2 - 1));
			}

			y.add(y.get(i-1) + yNext);
		}

		xLast = x.get(noOfPillars-1);
		yLast = y.get(noOfPillars-1);

		ballX = Gdx.graphics.getWidth()/2;
		ballY = Gdx.graphics.getHeight()/3;

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		if(gameState == 0){
			if(Gdx.input.justTouched()){

				gameState =1;

			}
		}
		else if(gameState == 1){

			if(Gdx.input.justTouched()){

				ballVelocity *= -1;
				score++;

			}

//			for (int i=1;i<noOfPillars;i++){
//				x.get(i) = x.get(i-1) + xNext;
//				y.get(i) = y.get(i-1) + yNext;


//			xTop[i] = xTop[i-1] + xNext;
//			yTop[i] = xTop[i-1] + yNext;
//			batch.draw(pillarTop[i],xTop[i],yTop[i]);
//			}

			ballX += ballVelocity;

			for(int i=0;i<noOfPillars;i++){
				y.set(i,y.get(i)-pillarVelocity);
			}

			yLast -= pillarVelocity;

			for(int i=0;i<noOfPillars;i++){
				if(y.get(i)<-320){
					if(xLast-xNext < 0){
						x.set(i,xLast + xNext);
					}
					else if( xLast + xNext + pillar[i].getWidth() > Gdx.graphics.getWidth() ){
						x.set(i,xLast - xNext);
					}
					else{
						x.set(i,xLast + xNext*(rand.nextInt(2)*2 - 1));
					}

					y.set(i,yLast + yNext);

					xLast = x.get(i);
					yLast = y.get(i);

					x.remove(i);
					x.add(xLast);
					y.remove(i);
					y.add(yLast);
				}
			}

		}
		else{
			if(Gdx.input.justTouched()){
				gameState =0;
				ballVelocity = 1;
				startGame();
			}
		}

		/**
		 * draw all the pillars
		 */
		for(int i=noOfPillars-1;i>=0;i--){
			batch.draw(pillar[i], x.get(i), y.get(i));
		}

		//batch.draw(pillar[0], x[0], y[0]);
		//batch.draw(pillarTop[0], xTop[0], yTop[0]);

		/**
		 * draw the ball
		 */
		batch.draw(ball,ballX-ball.getWidth()/2,ballY);

		/**
		 * if game over display : game over
		 */
		if(gameState==2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,
					Gdx.graphics.getHeight()*3/5);
//			fontScore.getData().setScale(10);
//			fontScore.draw(batch,String.valueOf(score),
//					Gdx.graphics.getWidth()/2-50,
//					Gdx.graphics.getHeight()/2-50);
		}

		batch.end();
	}

}
