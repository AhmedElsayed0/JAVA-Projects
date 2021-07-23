package lab4.geoPosition;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import lab1.geoPosition.GeoPosition;

class MapPanel extends JPanel implements MouseMotionListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private int mapWidth = 768;
	private int mapHigth = 512;
	InfoPanel infoPanel;
	private ArrayList<Point> points;
	private ArrayList<Point> greenPoints;
	private ArrayList<Point> userPoints;
	private ArrayList<GeoPosition> positions;

	public MapPanel(InfoPanel infoPanel) {
		points = new ArrayList<Point>();
		greenPoints = new ArrayList<Point>();
		userPoints = new ArrayList<Point>();
		positions = new ArrayList<GeoPosition>();

		try {
			File file = new File("Route.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				Point p = new Point();
				String[] cords = scanner.nextLine().split(",");
				p.x = Integer.parseInt(cords[0]);
				p.y = Integer.parseInt(cords[1]);
				points.add(p);
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("file read failed");
		}

		addMouseMotionListener(this);
		this.infoPanel = infoPanel;
	}

	public Dimension getPreferredSize() {
		return new Dimension(mapWidth, mapHigth);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage mapImage;
		try {
			mapImage = ImageIO.read(new File("OSM_BerlinerTor.png"));
			g.drawImage(mapImage, 0, 0, null);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int counter = 0;
			for (Point point : points) {
				g2.setColor(Color.red);
				g2.fillOval(point.x - 5, point.y - 5, 10, 10);
				g2.setColor(Color.black);
				g2.drawString(Integer.toString(counter), point.x + 10, point.y);
				counter += 1;
			}

			if (points.size() > 1) {
				g2.setColor(Color.red);
				Point p1, p2;
				for (int i = 0; i < points.size() - 1; i++) {
					p1 = points.get(i);
					p2 = points.get(i + 1);
					g2.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}

			if (userPoints.size() > 1) {
				g2.setColor(Color.blue);
				Point p1, p2;
				for (int i = 0; i < userPoints.size() - 1; i++) {
					p1 = userPoints.get(i);
					p2 = userPoints.get(i + 1);
					g2.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
			
			for (Point point : greenPoints) {
				g2.setColor(Color.green);
				g2.fillOval(point.x - 5, point.y - 5, 10, 10);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private GeoPosition pointToGeoPosition(Point p) {
		double top = 53.5631389;
		double left = 10.008555555555555;
		double bottom = 53.5566389;
		double right = 10.025;

		double longitude = left + ((right - left) * p.x / mapWidth);
		double latitude = top - ((top - bottom) * p.y / mapHigth);

		return new GeoPosition(latitude, longitude);
	}

	private void distanceUpdate() {
		if (positions.size() > 1) {
			GeoPosition p1, p2;
			double distance = 0.0;
			for (int i = 0; i < positions.size() - 1; i++) {
				p1 = positions.get(i);
				p2 = positions.get(i + 1);
				distance += GeoPosition.distanceInKm(p1, p2);
			}
			infoPanel.refreshDistance(distance);
		} else {
			infoPanel.refreshDistance(0);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point point = new Point();
		point.x = e.getX();
		point.y = e.getY();

		userPoints.add(point);
		positions.add(pointToGeoPosition(point));
		
		for (Point p: points) {
			if((Math.abs(point.x - p.x) < 5) && (Math.abs(point.y - p.y) < 5)){
				greenPoints.add(p);
			}
		}
		
		this.repaint();
		distanceUpdate();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point point = new Point();
		point.x = e.getX();
		point.y = e.getY();

		GeoPosition position = pointToGeoPosition(point);
		infoPanel.refreshPosition(position.getLatitude(), position.getLongitude());

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals("Delete Route")) {
			userPoints = new ArrayList<Point>();
			positions = new ArrayList<GeoPosition>();
			greenPoints = new ArrayList<Point>();
			distanceUpdate();
		}

		this.repaint();

	}

}

class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public ButtonPanel(ActionListener mapPanel) {
		this.setLayout(new FlowLayout());

		JButton buttonDeleteRoute = new JButton("Delete Route");
		// JButton buttonDeleteLastPoint = new JButton("Delete Last Destination");

		buttonDeleteRoute.addActionListener((ActionListener) mapPanel);
		buttonDeleteRoute.setActionCommand("Delete Route");
		// buttonDeleteLastPoint.addActionListener((ActionListener) mapPanel);
		// buttonDeleteLastPoint.setActionCommand("Delete Last Point");

		this.add(buttonDeleteRoute);
		// this.add(buttonDeleteLastPoint);
	}

}

class InfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	JLabel positionLabel;
	JLabel distanceLabel;

	public InfoPanel() {
		this.setLayout(new GridLayout(1, 2));

		positionLabel = new JLabel(String.format("Position: %9.2f, %9.2f", 0.0, 0.0), JLabel.CENTER);
		distanceLabel = new JLabel(String.format("Total distance: %.3f km", 0.0), JLabel.CENTER);

		this.add(distanceLabel);
		this.add(positionLabel);
	}

	public void refreshPosition(double latitude, double longitude) {
		positionLabel.setText(String.format("Position: %9.5f, %9.5f", latitude, longitude));
		this.repaint();
	}

	public void refreshDistance(double distance) {
		distanceLabel.setText(String.format("Total distance: %.3f km", distance));
		this.repaint();
	}

}

class ControlPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public ControlPanel(MapPanel mapPanel, InfoPanel infoPanel) {
		this.setLayout(new GridLayout(1, 2));

		this.add(new ButtonPanel(mapPanel));
		this.add(infoPanel);
	}

}

class Menu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 1L;

	public Menu() {
		JMenu menuFile = new JMenu("File"); // Create menu "File"
		this.add(menuFile);
		JMenuItem setting = new JMenuItem("Setting");
		menuFile.add(setting);
		setting.addActionListener(this);
		setting.setActionCommand("Setting");
		menuFile.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		menuFile.add(exit);
		exit.addActionListener(this);
		exit.setActionCommand("Exit");

		JMenu menuHelp = new JMenu("Help"); // Create menu "Help"
		this.add(menuHelp);
		JMenuItem about = new JMenuItem("About");
		menuHelp.add(about);
		about.addActionListener(this);
		about.setActionCommand("About");

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals("About")) {
			MapAppGUI2.showMassege("About");
		} else if (actionCommand.equals("Exit")) {
			System.exit(0);
		} else if (actionCommand.equals("Setting")) {
			MapAppGUI2.showInfoMassege("Setting");
		}

	}

}

public class MapAppGUI2 {

	private int width = 768;
	private int higth = 512 + 90;
	static JFrame frame = new JFrame("The Map App");;

	public MapAppGUI2() {
		// frame properties
		frame.setIconImage(new ImageIcon("map.png").getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, higth);
		frame.setResizable(false);
		frame.setLocation(50, 50);

		// menu bar
		JMenuBar menuBar = new Menu(); // Create menu bar and add to frame
		frame.setJMenuBar(menuBar);

		// Add labels to content pane
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		// Create contents
		InfoPanel infoPanel = new InfoPanel();
		MapPanel mapPanel = new MapPanel(infoPanel);
		ControlPanel controlPanel = new ControlPanel(mapPanel, infoPanel);

		contentPane.add(controlPanel);
		contentPane.add(mapPanel);

		frame.setVisible(true);
	}

	public static void showMassege(String title) {
		JOptionPane.showMessageDialog(frame,
				"This is app was developed by Malek Abbassi as part of his 4rd Software Construction 2 LAB.", title,
				JOptionPane.PLAIN_MESSAGE);
	}

	public static void showInfoMassege(String title) {
		JOptionPane.showMessageDialog(frame,
				"This fonction is under development.\nYou can send an email to check the progress: malek.abbassi@haw-hamburg.de",
				title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		new MapAppGUI2();
	}

}
