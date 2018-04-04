package com.zigzag.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.math.*;

public class zigzag extends ApplicationAdapter {
	SpriteBatch batch;

	Random rand;

	int noOfPillars = 50;
	Texture pillar[];
	ArrayList<Integer> x;
	ArrayList<Integer> y;
	int x0 = 0;
	int y0 = 0;
	int xLast;
	int yLast;

	ArrayList<Integer> xTop;
	ArrayList<Integer> yTop;
	int yDiff = 184;
	int hTop = 130;
	int wTop = 182;

	int xNext = 91;
	int yNext = 65;

	int pillarVelocity = 6;

	Texture ball;
	int ballX;
	int ballY;
	int ballVelocity = 8;

	int gameState = 0;

	Texture gameOver;

	int score = 0;

	@Override
	public void create () {

		rand = new Random();

		batch = new SpriteBatch();
		pillar = new Texture[noOfPillars];
		x = new ArrayList<Integer>();
		y = new ArrayList<Integer>();

		xTop = new ArrayList<Integer>();
		yTop = new ArrayList<Integer>();

		for(int i=0;i<noOfPillars;i++){
			pillar[i] = new Texture("pillar.png");
		}

		ball = new Texture("ball.png");

		gameOver = new Texture("gameover.png");

		startGame();
	}

	public void startGame(){

		x.clear();
		y.clear();
		xTop.clear();
		yTop.clear();

		x0 = Gdx.graphics.getWidth()/2 - 91;
		y0 = Gdx.graphics.getHeight()/3 - 314 + 65;
		x.add(x0);
		y.add(y0);
		xTop.add(x0);
		yTop.add(y0 + yDiff);

		for(int i=1;i<4;i++){
			x.add(x.get(i-1) + xNext);
			y.add(y.get(i-1) + yNext);
			xTop.add(x.get(i));
			yTop.add(y.get(i) + yDiff);
		}

		for (int i=4;i<noOfPillars;i++) {

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

			xTop.add(x.get(i));
			yTop.add(y.get(i) + yDiff);
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

		if(gameState == 0){							// game not started
			if(Gdx.input.justTouched()){

				gameState =1;

			}
		}
		else if(gameState == 1){					// game in progress

			if(Gdx.input.justTouched()){

				ballVelocity *= -1;
				score++;

			}

			ballX += ballVelocity;

			for(int i=0;i<noOfPillars;i++){
				y.set(i,y.get(i)-pillarVelocity);
				yTop.set(i,yTop.get(i)-pillarVelocity);
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
					xTop.remove(i);
					xTop.add(xLast);
					yTop.remove(i);
					yTop.add(yLast + yDiff);
				}
			}

		}
		else{											// game over
			if(Gdx.input.justTouched()){
				gameState =0;
				ballVelocity = 8;
				startGame();
			}
		}


		/**
		 * draw all the pillars
		 */
		for (int i = noOfPillars - 1; i >= 0; i--) {
			batch.draw(pillar[i], x.get(i), y.get(i));
		}


		/**
		 * draw the ball
		 */
		batch.draw(ball, ballX - ball.getWidth() / 2, ballY);


		/**
		 *  checking whether ball is out of track
		 */
		if(gameState==1) {
			boolean over = true;
			for (int i = 0; i < noOfPillars; i++) {

				if(ballX==xTop.get(i) + wTop/2 || ballY==yTop.get(i) + hTop/2){
					over = false;
					break;
				}

				point[] polygon = new point[4];
				for(int j=0;j<4;j++){
					polygon[j] = new point();
				}

				polygon[0].x = xTop.get(i);
				polygon[0].y = yTop.get(i) + hTop/2;
				polygon[1].x = xTop.get(i) + wTop/2;
				polygon[1].y = yTop.get(i);
				polygon[2].x = xTop.get(i) + wTop;
				polygon[2].y = yTop.get(i) + hTop/2;
				polygon[3].x = xTop.get(i) + wTop/2;
				polygon[3].y = yTop.get(i) + hTop;

				point p = new point();
				p.x = ballX;
				p.y = ballY;

				if(isInside(polygon,4,p)){
					over = false;
					break;
				}

			}

			if (over) {
				gameState = 2;
			}
		}


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

	private boolean isInside(point polygon[],int n,point p){
		point extreme = new point();
		extreme.x = Gdx.graphics.getWidth();
		extreme.y = p.y;

		int count = 0, i = 0;
		do
		{
			int next = (i+1)%n;

			// Check if the line segment from 'p' to 'extreme' intersects
			// with the line segment from 'polygon[i]' to 'polygon[next]'
			if (doIntersect(polygon[i], polygon[next], p, extreme))
			{
				// If the point 'p' is colinear with line segment 'i-next',
				// then check if it lies on segment. If it lies, return true,
				// otherwise false
				if (orientation(polygon[i], p, polygon[next]) == 0)
					return onSegment(polygon[i], p, polygon[next]);

				count++;
			}
			i = next;
		} while (i != 0);

		// Return true if count is odd, false otherwise
		if(count%2==1){
			return true;
		}

		return false;
	}

	private boolean doIntersect(point p1, point q1, point p2, point q2){
		// Find the four orientations needed for general and
		// special cases
		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		// General case
		if (o1 != o2 && o3 != o4)
			return true;

		// Special Cases
		// p1, q1 and p2 are colinear and p2 lies on segment p1q1
		if (o1 == 0 && onSegment(p1, p2, q1)) return true;

		// p1, q1 and p2 are colinear and q2 lies on segment p1q1
		if (o2 == 0 && onSegment(p1, q2, q1)) return true;

		// p2, q2 and p1 are colinear and p1 lies on segment p2q2
		if (o3 == 0 && onSegment(p2, p1, q2)) return true;

		// p2, q2 and q1 are colinear and q1 lies on segment p2q2
		if (o4 == 0 && onSegment(p2, q1, q2)) return true;

		return false; // Doesn't fall in any of the above cases
	}

	private int orientation(point p, point q, point r){

		int val = (q.y - p.y) * (r.x - q.x) -
				(q.x - p.x) * (r.y - q.y);

		if (val == 0) return 0;  // colinear
		return (val > 0)? 1: 2; // clock or counterclock wise
	}

	private boolean onSegment(point p, point q, point r){

		if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
				q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
			return true;

		return false;
	}

}
