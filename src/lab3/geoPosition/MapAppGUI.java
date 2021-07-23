package lab3.geoPosition;

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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

class MapPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private int mapWidth = 1024;
	private int mapHigth = 768;
	InfoPanel infoPanel;
	private ArrayList<Point> points;
	private ArrayList<GeoPosition> positions;

	public MapPanel(InfoPanel infoPanel) {
		points = new ArrayList<Point>();
		positions = new ArrayList<GeoPosition>();
		addMouseListener(this);
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
			mapImage = ImageIO.read(new File("OSM_Map.png"));
			g.drawImage(mapImage, 0, 0, null);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.red);

			for (Point point : points) {
				g2.fillOval(point.x - 5, point.y - 5, 10, 10);
			}

			if (points.size() > 1) {
				g2.setColor(Color.blue);
				Point p1, p2;
				for (int i = 0; i < points.size() - 1; i++) {
					p1 = points.get(i);
					p2 = points.get(i + 1);
					g2.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private GeoPosition pointToGeoPosition(Point p) {
		double top = 54.5720556;
		double left = 8.4375;
		double bottom = 53.3325;
		double right = 11.24725;

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
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (positions.size() >= 1) {
			GeoPosition lastPosition = positions.get(positions.size() - 1);
			infoPanel.refreshPosition(lastPosition.getLatitude(), lastPosition.getLongitude());
		} else {
			infoPanel.refreshPosition(0, 0);
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point point = new Point(e.getX(), e.getY());
		points.add(point);
		positions.add(pointToGeoPosition(point));
		this.repaint();
		distanceUpdate();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

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
			points = new ArrayList<Point>();
			positions = new ArrayList<GeoPosition>();
			distanceUpdate();
		} else if (actionCommand.equals("Delete Last Point")) {
			if (points.size() > 0) {
				points.remove(points.size() - 1);
				positions.remove(positions.size() - 1);
			}
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
		JButton buttonDeleteLastPoint = new JButton("Delete Last Destination");

		buttonDeleteRoute.addActionListener((ActionListener) mapPanel);
		buttonDeleteRoute.setActionCommand("Delete Route");
		buttonDeleteLastPoint.addActionListener((ActionListener) mapPanel);
		buttonDeleteLastPoint.setActionCommand("Delete Last Point");

		this.add(buttonDeleteRoute);
		this.add(buttonDeleteLastPoint);
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
		positionLabel.setText(String.format("Position: %9.2f, %9.2f", latitude, longitude));
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
			MapAppGUI.showMassege("About");
		} else if (actionCommand.equals("Exit")) {
			System.exit(0);
		} else if (actionCommand.equals("Setting")) {
			MapAppGUI.showInfoMassege("Setting");
		}

	}

}

public class MapAppGUI {

	private int width = 1024;
	private int higth = 768 + 90;
	static JFrame frame = new JFrame("The Map App");;

	public MapAppGUI() {
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
				"This is app was developed by Malek Abbassi as part of his 3rd Software Construction 2 LAB.", title,
				JOptionPane.PLAIN_MESSAGE);
	}

	public static void showInfoMassege(String title) {
		JOptionPane.showMessageDialog(frame,
				"This fonction is under development.\nYou can send an email to check the progress: malek.abbassi@haw-hamburg.de",
				title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		new MapAppGUI();
	}

}
