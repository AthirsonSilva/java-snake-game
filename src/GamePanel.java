import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	final int[] x = new int[GAME_UNITS];
	final int[] y = new int[GAME_UNITS];
	int bodyParts = 4;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'D';
	boolean running = false;
	Timer timer;
	Random random;

	GamePanel() {
		random = new Random();

		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // Set panel size
		this.setFocusable(true); // Allow focus
		this.setBackground(Color.BLACK);
		this.addKeyListener(new MyKeyAdapter()); // Add key listener

		startGame(); // Start game
	}

	public void startGame() {
		newApple();

		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Paint background
		draw(g); // Draw grid and snake
	}

	public void draw(Graphics g) {
		if (running) {
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) { // Draw grid
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}

			g.setColor(Color.RED); // Draw apple
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) { // Snake's head
					g.setColor(Color.GREEN);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else { // Snake's body
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}

			FontMetrics metrics = getFontMetrics(g.getFont());

			writeDisplayText(g,
					Color.GREEN,
					24,
					"Score: " + applesEaten,
					(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
					(SCREEN_HEIGHT / g.getFont().getSize()) - metrics.stringWidth("Score: " + applesEaten) / 2);

		} else {
			gameOver(g);
		}
	}

	public void move() {
		// Move snake's body
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		switch (direction) {
			case 'U' -> // Move up
					y[0] = y[0] - UNIT_SIZE;
			case 'D' -> // Move down
					y[0] = y[0] + UNIT_SIZE;
			case 'L' -> // Move left
					x[0] = x[0] - UNIT_SIZE;
			case 'R' -> // Move right
					x[0] = x[0] + UNIT_SIZE;
			default -> {
			}
		}
	}

	public void newApple() {
		// Create new apple at random position
		appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
		appleY = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
	}

	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++; // increase snake's body
			applesEaten++; // increase apples eaten
			newApple(); // create new apple
		}
	}

	public void checkCollisions() {
		// Checks if snake's head collides with its body
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
				break;
			}
		}

		// Check if snake's head touches screen borders
		if (x[0] < 0) { // left border
			running = false;
		}

		if (x[0] > SCREEN_WIDTH) { // right border
			running = false;
		}

		if (y[0] < 0) { // top border
			running = false;
		}

		if (y[0] > SCREEN_HEIGHT) { // bottom border
			running = false;
		}

		if (!running) {
			timer.stop();
		}
	}

	public void gameOver(Graphics g) {
		// Displays score
		FontMetrics scoreMetrics = getFontMetrics(g.getFont());
		writeDisplayText(g,
				Color.RED,
				24,
				"Score: " + applesEaten,
				SCREEN_WIDTH / 2 - (scoreMetrics.stringWidth("Score: " + applesEaten) * 2) / 2,
				SCREEN_HEIGHT / 2 + (g.getFont().getSize() * 4));

		// Game over text
		FontMetrics gameOverMetrics = getFontMetrics(g.getFont());
		writeDisplayText(g,
				Color.RED,
				75,
				"Game Over",
				gameOverMetrics.stringWidth("Game Over") / 2,
				SCREEN_HEIGHT / 2);
		/*
		 * g.setColor(Color.RED);
		 * g.setFont(new Font("Ink Free", Font.BOLD, 75));
		 * 
		 * 
		 * g.drawString("Game Over", (SCREEN_WIDTH -
		 * gameOverMetrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
		 */
	}

	private void writeDisplayText(Graphics g, Color fontColor, int fontSize, String text,
								  int x, int y) {
		g.setColor(fontColor);
		g.setFont(new Font("Ink Free", Font.BOLD, fontSize));

		g.drawString(text, x, y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Check if game is running and move snake and check for collisions
		if (running) {
			move();
			checkApple();
			checkCollisions();

		}

		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT -> {
					if (direction != 'R') { // Prevents snake from moving in opposite direction
						direction = 'L';
					}
				}
				case KeyEvent.VK_RIGHT -> {
					if (direction != 'L') { // Prevents snake from moving in opposite direction
						direction = 'R';
					}
				}
				case KeyEvent.VK_UP -> {
					if (direction != 'D') { // Prevents snake from moving in opposite direction
						direction = 'U';
					}
				}
				case KeyEvent.VK_DOWN -> {
					if (direction != 'U') { // Prevents snake from moving in opposite direction
						direction = 'D';
					}
				}
			}
		}
	}
}