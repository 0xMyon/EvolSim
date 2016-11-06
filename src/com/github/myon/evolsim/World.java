package com.github.myon.evolsim;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.github.myon.evolsim.engine.LocationManager;
import com.github.myon.evolsim.engine.WorkItem;
import com.github.myon.evolsim.engine.Worker;
import com.github.myon.evolsim.generator.DefaultGenerator;
import com.github.myon.evolsim.generator.Generator;
import com.github.myon.gol.GameOfLife;
import com.github.myon.util.Statistics;
import com.github.myon.util.Util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class World implements WorkItem {

	public int age = 0;

	private final Vector<Creature> creatures = new Vector<>();

	private final Generator generator = DefaultGenerator.INSTANCE;

	public World() {
		this.worker.add(this);
	}

	private GameOfLife gol = new GameOfLife(512);

	@Override
	public void run() {
		this.age++;

		Statistics.INSTANCE.add("#Space", this.getCollisionSpace().count());

		while (this.size() < 50) {
			this.add(new Creature(this));
		}

		this.gol = this.gol.next();

		/*
		 * while(this.creatures.size() > Constants.MAX_CREATURES) { final int
		 * kill_index = Util.nextInt(this.creatures.size()-1); final Creature
		 * creature = this.creatures.get(kill_index); creature.kill();
		 * this.remove(creature); }
		 */

		Statistics.INSTANCE.reset();

	}

	private synchronized int size() {
		return this.creatures.size();
	}

	public Generator generator() {
		return this.generator;
	}

	public synchronized int creatures() {
		return this.creatures.size();
	}

	public synchronized void add(final Creature creature) {
		creature.locate(this.space);
		this.creatures.add(creature);
		this.worker.add(creature);
	}

	public synchronized void remove(final Creature creature) {
		this.creatures.remove(creature);
		creature.remove();
		this.worker.remove(creature);
	}

	public synchronized void draw(final GraphicsContext gc) {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, 512, 512);

		this.drawGOL(gc);

		gc.setFill(Color.BLACK);
		gc.fillText("Year: " + this.age, 10, 20);
		gc.fillText("Population: " + this.creatures.size(), 10, 35);
		final Map<String, Integer> types = new HashMap<>();
		for (final Creature creature : this.creatures) {
			types.put(creature.species(), types.getOrDefault(creature.species(), 0) + 1);
			if (!creature.isActive()) {
				gc.setStroke(javafx.scene.paint.Color.rgb(creature.getColor().red(), creature.getColor().green(),
						creature.getColor().blue()));
				gc.strokeOval(
						creature.getLoaction().x() * this.zoom + this.centerX - creature.getRadius() * this.zoom / 2,
						creature.getLoaction().y() * this.zoom + this.centerY - creature.getRadius() * this.zoom / 2,
						creature.getRadius() * this.zoom, creature.getRadius() * this.zoom);
			} else {
				gc.setFill(javafx.scene.paint.Color.rgb(creature.getColor().red(), creature.getColor().green(),
						creature.getColor().blue()));
				gc.fillOval(
						creature.getLoaction().x() * this.zoom + this.centerX - creature.getRadius() * this.zoom / 2,
						creature.getLoaction().y() * this.zoom + this.centerY - creature.getRadius() * this.zoom / 2,
						creature.getRadius() * this.zoom, creature.getRadius() * this.zoom);
			}
		}

		gc.setFill(Color.BLACK);
		int i = 1;
		gc.fillText("Spicies: " + types.size(), 10, 35 + (15 * i++));

		for (final Entry<String, Double> e : Statistics.INSTANCE.values()) {
			gc.fillText(e.getKey() + ": " + this.two.format(e.getValue()), 10, 35 + (15 * i++));
		}

		this.drawSelected(gc);
	}

	private void drawGOL(final GraphicsContext gc) {

		gc.setFill(javafx.scene.paint.Color.BLACK);
		for (int x = 0; x < this.gol.size(); x++) {
			for (int y = 0; y < this.gol.size(); y++) {
				if (this.gol.get(x, y)) {
					gc.fillRect(this.centerX + x * this.zoom, this.centerY + y * this.zoom, 1.0 * this.zoom,
							1.0 * this.zoom);
				}
			}
		}

	}

	private final DecimalFormat zero = new DecimalFormat("#0");
	private final DecimalFormat two = new DecimalFormat("#0.00");

	private void drawSelected(final GraphicsContext gc) {
		if (this.selected == null || this.selected.isDead()) {
			this.selectRandom();
		}

		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		gc.strokeLine(10 + 64, 512 - 10 - 64, this.selected.getLoaction().x() * this.zoom + this.centerX,
				this.selected.getLoaction().y() * this.zoom + this.centerY);

		gc.setLineWidth(5);
		gc.setFill(Color.WHITE);
		gc.fillOval(10, 512 - 10 - 128, 128, 128);
		gc.setStroke(javafx.scene.paint.Color.rgb(this.selected.getColor().red(), this.selected.getColor().green(),
				this.selected.getColor().blue()));
		gc.strokeOval(10, 512 - 10 - 128, 128, 128);

		gc.setLineWidth(2);
		for (final Neuron n : this.selected.neurons) {
			if (n.value() > 0.5) {
				gc.setFill(javafx.scene.paint.Color.rgb(n.color().red(), n.color().green(), n.color().blue()));
				gc.fillOval(10 + 64 + (n.position().x() / this.selected.getRadius() * 64) - World.neuronSize / 2,
						512 - 10 - 64 + (n.position().y() / this.selected.getRadius() * 64) - World.neuronSize / 2,
						World.neuronSize, World.neuronSize);
			} else {
				gc.setStroke(javafx.scene.paint.Color.rgb(n.color().red(), n.color().green(), n.color().blue()));
				gc.strokeOval(10 + 64 + (n.position().x() / this.selected.getRadius() * 64) - World.neuronSize / 2,
						512 - 10 - 64 + (n.position().y() / this.selected.getRadius() * 64) - World.neuronSize / 2,
						World.neuronSize, World.neuronSize);
			}
			gc.setFill(Color.BLACK);
			gc.fillText(n.type().symbol(),
					20 + 64 + (n.position().x() / this.selected.getRadius() * 64) - World.neuronSize / 2,
					10 + 512 - 10 - 64 + (n.position().y() / this.selected.getRadius() * 64) - World.neuronSize / 2);
		}

		gc.setFill(Color.BLACK);
		gc.fillText("Name: " + this.selected.name(), 10, 512 - 10 - 128 - 15 * 5);
		gc.fillText("Species: " + this.selected.species(), 10, 512 - 10 - 128 - 15 * 4);
		gc.fillText("Generation: " + this.selected.generation(), 10, 512 - 10 - 128 - 15 * 3);
		gc.fillText("Age: " + this.selected.age(), 10, 512 - 10 - 128 - 15 * 2);
		gc.fillText("Energy: " + this.zero.format(this.selected.energy()) + "/"
				+ this.zero.format(this.selected.capacity()), 10, 512 - 10 - 128 - 15 * 1);

	}

	private static float neuronSize = 8;

	private double zoom = 1.0;
	private double centerX = 256.0;
	private double centerY = 256.0;

	public void zoom(final double delta) {
		if (Math.signum(delta) > 0) {
			this.zoom *= 1.05;
		} else if (Math.signum(delta) < 0) {
			this.zoom /= 1.05;
		}
	}

	private Creature selected = null;
	private Integer selected_id = null;

	public void move(final double x, final double y) {
		this.centerX += x * 50 / this.zoom;
		this.centerY += y * 50 / this.zoom;
	}

	public void selectRandom() {
		this.selected_id = Util.nextInt(this.creatures.size() - 1);
		this.selected = this.creatures.get(this.selected_id);
	}

	// DONE

	private final LocationManager<Creature> space = new LocationManager<>(Constants.WORLD_SIZE, 1);

	public LocationManager<Creature> getCollisionSpace() {
		return this.space;
	}

	private final Worker worker = new Worker(2);

	@Override
	public boolean requeue() {
		return true;
	}

	/*
	 * private final LocationManager<Neuron> collectors = new
	 * LocationManager<>(Constants.WORLD_SIZE, 1); public
	 * LocationManager<Neuron> getCollectorSpace() { return this.collectors; }
	 */

}
