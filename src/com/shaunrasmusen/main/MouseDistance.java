package com.shaunrasmusen.main;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;

public class MouseDistance {

	double x = 0, y = 0, dx = 0, dy = 0;
	double in, ft, mi;
	static int dpi;
	String distStr;
	PointerInfo info;
	static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	Point p;
	boolean mouseClick;
	int timer = 0;

	static AngelCodeFont font, fontsmall;

	public void run() {
		info = MouseInfo.getPointerInfo();
		p = info.getLocation();
		x = p.getX();
		y = p.getY();

		load();

		while (!Display.isCloseRequested()) {
			timer++;
			if (Mouse.isInsideWindow() && Mouse.isButtonDown(0) && mouseClick == false) {
				showStats();
				timer = 0;
			}

			if (timer > 10)
				mouseClick = false;

			clearScreen();

			info = MouseInfo.getPointerInfo();
			p = info.getLocation();
			dx = p.getX() - x;
			dy = p.getY() - y;

			in += (Math.sqrt((dx * dx) + (dy * dy)) / dpi);

			info = MouseInfo.getPointerInfo();
			p = info.getLocation();
			x = p.getX();
			y = p.getY();

			if (in > 12.0) {
				ft++;
				in = 0;
			}
			if (ft > 5280.0) {
				mi++;
				ft = 0;
			}
			
			distStr = (int) (mi) + "mi " + (int) (ft) + "\' " + (int) (in) + "\"";

			font.drawString((Display.getWidth() / 2) - (font.getWidth("Distance traveled by cursor:") / 2), 0, "Distance traveled by cursor:");
			font.drawString((Display.getWidth() / 2) - (font.getWidth(distStr) / 2), 20, distStr);

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, Display.getWidth() * 2, Display.getHeight() * 2, 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			font.drawString(4, 110, "Click for Statistics");
			font.drawString((Display.getWidth() - (font.getWidth("(c) 2015, Shaun Rasmusen") / 2)) * 2 - 10, 110, "(c) 2015, Shaun Rasmusen");

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			Display.sync(30);
			Display.update();
			GL11.glLoadIdentity();
		}

		save();
		Display.destroy();
	}

	public void showStats() {
		Display.setLocation((int) (dim.getWidth()) - 400, 0);
		try {
			Display.setDisplayMode(new DisplayMode(400, 151));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		timer = 0;

		while (!mouseClick) {
			timer++;
			if (timer > 10)
				mouseClick = false;
			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}
			if (Mouse.isButtonDown(0) && Mouse.isInsideWindow() && mouseClick == false)
				mouseClick = true;

			clearScreen();
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			font.drawString(3, 0, "Statistics");
			GL11.glColor3d(256, 256, 256);
			GL11.glBegin(GL11.GL_LINE);
			GL11.glVertex2d(0, 15);
			GL11.glVertex2d(Display.getWidth(), 15);
			GL11.glEnd();

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, Display.getWidth() * 1.25, Display.getHeight() * 1.25, 0, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			font.drawString(3, 31, "The cursor has:");
			font.drawString(3, 53, "- climbed the Empire State Building " + (int) ((mi * 5280) + ft) /  		1454 + " time(s),");
			font.drawString(3, 75, "- dove down the Mariana Trench " + (int) ((mi * 5280) + ft) / 				36070 + " time(s),");
			font.drawString(3, 97, "- gone to the International Space Station " + (int) ((mi * 5280) + ft) / 	1314720 + " time(s),");
			font.drawString(3, 119, "- reached the Earth's core " + (int) ((mi * 5280) + ft) / 					20898240 + " time(s),");
			font.drawString(3, 141, "- circled the Earth " + (int) ((mi * 5280) + ft) / 						131477280 + " time(s),");
			font.drawString(3, 163, "- gone to the Moon " + (int) ((mi * 5280) + ft) / 							1261392000 + " time(s).");

			Display.sync(30);
			Display.update();
			GL11.glLoadIdentity();
		}
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		Display.setLocation((int) (dim.getWidth()) - 250, 0);
		try {
			Display.setDisplayMode(new DisplayMode(250, 70));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public void load() {
		Path file = FileSystems.getDefault().getPath("distance.txt");
		Charset charset = Charset.forName("UTF-8");

		try {
			BufferedReader br = Files.newBufferedReader(file, charset);
			in = Double.parseDouble(br.readLine());
			ft = Double.parseDouble(br.readLine());
			mi = Double.parseDouble(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("distance.txt"), "UTF-8"));
			write.write(Double.toString(in));
			write.newLine();
			write.write(Double.toString(ft));
			write.newLine();
			write.write(Double.toString(mi));
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glColor4d(0, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2d(0, 0);
		GL11.glVertex2d(Display.getWidth(), 0);
		GL11.glVertex2d(Display.getWidth(), Display.getWidth());
		GL11.glVertex2d(0, Display.getHeight());
		GL11.glEnd();
	}

	public static void main(String args[]) {
		Display.setTitle("Mouse Distance Tracker");
		Display.setLocation((int) (dim.getWidth()) - 250, 0);
		try {
			Display.setDisplayMode(new DisplayMode(250, 70));
			Display.create();
			dpi = Toolkit.getDefaultToolkit().getScreenResolution();
			font = new AngelCodeFont("font.fnt", new Image("font.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		MouseDistance md = new MouseDistance();
		md.run();
	}
}
