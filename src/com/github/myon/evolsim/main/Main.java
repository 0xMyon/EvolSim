package com.github.myon.evolsim.main;

import com.github.myon.evolsim.World;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(final String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle( "Example" );



		final Group root = new Group();



		final Scene theScene = new Scene( root );
		primaryStage.setScene( theScene );

		final World world = new World();

		root.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(final ScrollEvent event) {
				world.zoom(event.getDeltaY());
				event.consume();
			}

		});

		theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent event) {
				world.move(
						((event.getCode() == KeyCode.LEFT)?1:0) + ((event.getCode() == KeyCode.RIGHT)?-1:0),
						((event.getCode() == KeyCode.UP)?1:0) + ((event.getCode() == KeyCode.DOWN)?-1:0)
						);
				if (event.getCode() == KeyCode.R) {
					world.selectRandom();
				}
				event.consume();
			}
		});

		final Canvas canvas = new Canvas( 512, 512 );
		root.getChildren().add( canvas );

		final GraphicsContext gc = canvas.getGraphicsContext2D();


		new AnimationTimer()
		{
			@Override
			public void handle(final long currentNanoTime)
			{
				world.step();
				world.draw(gc);
			}
		}.start();

		primaryStage.show();
	}

}
