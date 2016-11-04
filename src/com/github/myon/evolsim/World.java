package com.github.myon.evolsim;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.github.myon.evolsim.engine.CollisionSpace;
import com.github.myon.evolsim.generator.DefaultGenerator;
import com.github.myon.evolsim.generator.Generator;
import com.github.myon.util.Statistics;
import com.github.myon.util.Util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class World {

	public int age = 0;

	private final Vector<Creature> creatures = new Vector<>();

	private final Generator generator = DefaultGenerator.INSTANCE;

	public void step() {
		Statistics.INSTANCE.reset();
		//Statistics.INSTANCE.add("#CollisionSpace", this.space.count());
		//Statistics.INSTANCE.add("#CollisionSpace coll", this.collectors.count());
		Statistics.INSTANCE.add("#Creatures", this.creatures.size());

		final long time = System.currentTimeMillis();
		try {

			//System.out.println("Year "+this.age+": "+this.creatures.size());
			this.age++;

			while (this.creatures.size() < 50) {
				final Creature add = new Creature(this);
				add.locate(this.space);
				this.creatures.add(add);
			}

			while(this.creatures.size() > Constants.MAX_CREATURES) {
				final int kill_index = Util.nextInt(this.creatures.size()-1);
				final Creature kill = this.creatures.get(kill_index);
				kill.kill();
				this.creatures.remove(kill);
				kill.cleanup();
			}

			final Iterator<Creature> it = this.creatures.iterator();
			while(it.hasNext()) {
				final Creature current = it.next();
				current.modify(1);
				current.step();
				if (current.isDead() || Util.nextInt(Constants.MAX_CREATURES) == 0) {
					current.cleanup();
					it.remove();
				}
			}
			this.creatures.addAll(this.newborns);
			this.newborns.clear();

		} finally {
			Statistics.INSTANCE.add("FPS", 1.0/(((double)System.currentTimeMillis()-time)/1000));
		}
	}


	public void remove(final Creature creature) {
		this.creatures.remove(creature);
		creature.cleanup();
	}

	public Generator generator() {
		return this.generator;
	}

	public int creatures() {
		return this.creatures.size();
	}

	private final Set<Creature> newborns = new HashSet<>();

	public void add(final Creature creature) {
		this.newborns.add(creature);
	}

	public void draw(final GraphicsContext gc) {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, 512, 512);
		gc.setFill(Color.BLACK);
		gc.fillText("Year: "+this.age, 10, 20);
		gc.fillText("Population: "+this.creatures.size(), 10, 35);
		final Map<String,Integer> types = new HashMap<>();
		for (final Creature creature : this.creatures) {
			types.put(creature.species(), types.getOrDefault(creature.species(), 0)+1);
			if (!creature.isActive()) {
				gc.setStroke(javafx.scene.paint.Color.rgb(creature.color().red(), creature.color().green(), creature.color().blue()));
				gc.strokeOval(creature.x()*this.zoom+this.centerX - creature.radius()*this.zoom/2, creature.y()*this.zoom+this.centerY - creature.radius()*this.zoom/2, creature.radius()*this.zoom, creature.radius()*this.zoom);
			} else {
				gc.setFill(javafx.scene.paint.Color.rgb(creature.color().red(), creature.color().green(), creature.color().blue()));
				gc.fillOval(creature.x()*this.zoom+this.centerX - creature.radius()*this.zoom/2, creature.y()*this.zoom+this.centerY - creature.radius()*this.zoom/2, creature.radius()*this.zoom, creature.radius()*this.zoom);
			}
		}

		gc.setFill(Color.BLACK);
		int i = 1;
		gc.fillText("Spicies: "+types.size(), 10, 35+(15*i++));

		for (final Entry<String, Double> e: Statistics.INSTANCE.values.entrySet()) {
			gc.fillText(e.getKey()+": "+this.two.format(e.getValue()), 10, 35+(15*i++));
		}


		this.drawSelected(gc);
	}

	private final DecimalFormat zero = new DecimalFormat("#0");
	private final DecimalFormat two = new DecimalFormat("#0.00");


	private void drawSelected(final GraphicsContext gc) {
		if(this.selected == null || this.selected.isDead()) {
			this.selectRandom();
		}

		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		gc.strokeLine(10+64, 512-10-64,
				this.selected.x()*this.zoom+this.centerX,
				this.selected.y()*this.zoom+this.centerY
				);

		gc.setLineWidth(5);
		gc.setFill(Color.WHITE);
		gc.fillOval(10, 512-10-128, 128, 128);
		gc.setStroke(javafx.scene.paint.Color.rgb(this.selected.color().red(), this.selected.color().green(), this.selected.color().blue()));
		gc.strokeOval(10, 512-10-128, 128, 128);

		gc.setLineWidth(2);
		for(final Neuron n : this.selected.neurons) {
			if (n.value() > 0.5) {
				gc.setFill(javafx.scene.paint.Color.rgb(n.color().red(), n.color().green(), n.color().blue()));
				gc.fillOval(10+64+(n.position().x()/this.selected.radius()*64)-World.neuronSize/2, 512-10-64+(n.position().y()/this.selected.radius()*64)-World.neuronSize/2, World.neuronSize, World.neuronSize);
			} else {
				gc.setStroke(javafx.scene.paint.Color.rgb(n.color().red(), n.color().green(), n.color().blue()));
				gc.strokeOval(10+64+(n.position().x()/this.selected.radius()*64)-World.neuronSize/2, 512-10-64+(n.position().y()/this.selected.radius()*64)-World.neuronSize/2, World.neuronSize, World.neuronSize);
			}
			gc.setFill(Color.BLACK);
			gc.fillText(n.type().symbol(), 20+64+(n.position().x()/this.selected.radius()*64)-World.neuronSize/2, 10+512-10-64+(n.position().y()/this.selected.radius()*64)-World.neuronSize/2);
		}

		gc.setFill(Color.BLACK);
		gc.fillText("Name: "+this.selected.name(), 10, 512-10-128-15*5);
		gc.fillText("Species: "+this.selected.species(), 10, 512-10-128-15*4);
		gc.fillText("Generation: "+this.selected.generation(), 10, 512-10-128-15*3);
		gc.fillText("Age: "+this.selected.age(), 10, 512-10-128-15*2);
		gc.fillText("Energy: "+this.zero.format(this.selected.energy())+"/"+this.zero.format(this.selected.capacity()), 10, 512-10-128-15*1);


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
		this.centerX += x*50/this.zoom;
		this.centerY += y*50/this.zoom;
	}

	public void selectRandom() {
		this.selected_id = Util.nextInt(this.creatures.size()-1);
		this.selected = this.creatures.get(this.selected_id);
	}


	// DONE

	private final CollisionSpace<Creature> space = new CollisionSpace<>(Constants.WORLD_SIZE, 1);
	public CollisionSpace<Creature> getCollisionSpace() {
		return this.space;
	}

	private final CollisionSpace<Neuron> collectors = new CollisionSpace<>(Constants.WORLD_SIZE, 1);
	public CollisionSpace<Neuron> getCollectorSpace() {
		return this.collectors;
	}

}
