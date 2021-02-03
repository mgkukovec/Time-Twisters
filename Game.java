import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -1442798787354930462L;

	public static final int WIDTH = 640, HEIGHT = WIDTH / 16 * 9;
	private Thread thread;
	private boolean running = false;
	private Handler handler;

	public Game() {
		handler = new Handler();
		new Window(WIDTH, HEIGHT, "GAME TITLE", this);
		handler.addEntity(new Player(100, 100, EntityID.Player));
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Game loop
	public void run() {
		long timeLast = System.nanoTime();
		int ticksPerSecond = 60;
		double nsPerTick = 1000000000.0 / ticksPerSecond;
		double delta = 0; // units "tick"
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			long timeNow = System.nanoTime();
			delta += (timeNow - timeLast) / nsPerTick; // One tick has passed
			timeLast = timeNow;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running) {
				render();
			}
			frames++;

			// Prints FPS once per second
			// if statement checks if current time is 1 sec larger than timer
			// if yes, update timer to 1 sec later
			// frames is displayed and reset once per second, so displays FPS
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}

	private void tick() {
		handler.tick();
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		handler.render(g);

		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		new Game();
	}
}
