import java.awt.Color;
import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Button;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Checkbox;

abstract class Shapes{
	protected Color color;
	protected boolean isSolid;
	protected int parameter1,parameter2,parameter3,parameter4;
	
	public void setParameter1(int parameter1){
		this.parameter1=parameter1;
	}
	public void setParameter2(int parameter2){
		this.parameter2=parameter2;
	}
	public void setParameter3(int parameter3){
		this.parameter3=parameter3;
	}
	public void setParameter4(int parameter4){
		this.parameter4=parameter4;
	}
	public void setShapeColor(Color c){
		color = c;
	}
	public void setSolid(boolean state){
		isSolid=state;
	}
	
	public int getParameter1(){
		return parameter1;
	}
	public int getParameter2(){
		return parameter2;
	}
	public int getParameter3(){
		return parameter3;
	}
	public int getParameter4(){
		return parameter4;
	}
	public Color getColor(){
		return color;
	}
	public boolean getIsSolid(){
		return isSolid;
	}
	
	abstract void draw(Graphics g);
}

class Line extends Shapes{
	void draw(Graphics g){
		g.setColor(getColor());
		if(isSolid || !isSolid){
			g.drawLine(parameter1,parameter2,parameter3,parameter4);
		} 
	}
}

class RectangleShape extends Shapes{
	void draw(Graphics g){
		g.setColor(getColor());
		if(isSolid){
			g.fillRect(parameter1,parameter2,parameter3,parameter4);
		} else{
			g.drawRect(parameter1,parameter2,parameter3,parameter4);
		}
	}
}

class Oval extends Shapes{
	void draw(Graphics g){
		g.setColor(getColor());
		if(isSolid){
			g.fillOval(parameter1,parameter2,parameter3,parameter4);
		} else{
			g.drawOval(parameter1,parameter2,parameter3,parameter4);
		}
	}
	
}

class Eraser extends RectangleShape {
    void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(parameter1, parameter2, parameter3, parameter4);
    }
}
class FreePencil extends Shapes {
    private ArrayList<int[]> points;

    public FreePencil() {
        points = new ArrayList<>();
    }

    public void addPoint(int x, int y) {
        points.add(new int[]{x, y});
    }

    void draw(Graphics g) {
        g.setColor(getColor());
        for (int i = 1; i < points.size(); i++) {
            int[] p1 = points.get(i - 1);
            int[] p2 = points.get(i);
            g.drawLine(p1[0], p1[1], p2[0], p2[1]);
        }
    }
}

public class PaintBrush extends Applet{
	private Button LineButton,RectButton,OvalButton, RedButton, GreenButton, BlueButton,BlackButton;
	private Button clearButton, undoButton,eraserButton,pencileButton;
	private Checkbox checkBox;
	private ArrayList<Shapes> shapeList;
	private Color ColorState = Color.BLACK;
	private String ShapeState = LINE;
	public static final String LINE ="line";
	public static final String RECTANGLE ="rectangle";
	public static final String OVAL ="oval";
	public static final String ERASER ="Eraser";
	public static final String PENCIL = "pencil";
	private Shapes currentShape;
	private boolean dragged;
	private int startX,startY;
	
	public void init(){
		
	    shapeList = new ArrayList<>();
		initColorButtons();
		initShapeButtons();
		
		undoButton = new Button("Undo");
        undoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
			if(!shapeList.isEmpty()){
				shapeList.remove(shapeList.size()-1);
				repaint();
			}
        }});
		
		clearButton = new Button("Clear");
        clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
			if(!shapeList.isEmpty()){
				shapeList.clear();
			    repaint();
			}
        }});
		
		checkBox = new Checkbox("Solid",false);
		checkBox.addItemListener( new ItemListener (){
			public void itemStateChanged(ItemEvent e){
				checkBox.getState();
		}});
		
		eraserButton = new Button("Eraser");
        eraserButton.addActionListener(new ActionListener()   {
            public void actionPerformed(ActionEvent e) {
               ShapeState = ERASER;
        }});
		
		pencileButton = new Button("Free Pencil");
        pencileButton.addActionListener(new ActionListener()   {
            public void actionPerformed(ActionEvent e) {
               ShapeState = PENCIL;
        }});
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
				if(LINE.equals(ShapeState)){
					currentShape = new Line(); 
				    currentShape.setShapeColor(ColorState);
					currentShape.setSolid(checkBox.getState());
                    currentShape.setParameter1(e.getX());
                    currentShape.setParameter2(e.getY());
				}else if(RECTANGLE.equals(ShapeState)){
					currentShape = new RectangleShape();
					currentShape.setShapeColor(ColorState);
					currentShape.setSolid(checkBox.getState());
					currentShape.setParameter1(e.getX()); 
                    currentShape.setParameter2(e.getY()); 
				    startX = e.getX();
                    startY = e.getY();
				} else if(OVAL.equals(ShapeState)){
					currentShape = new Oval();
					currentShape.setShapeColor(ColorState);
					currentShape.setSolid(checkBox.getState());
					currentShape.setParameter1(e.getX());
					currentShape.setParameter2(e.getY());
					startX = e.getX();
                    startY = e.getY();
				}else if (PENCIL.equals(ShapeState)) {
                   currentShape = new FreePencil();
                   currentShape.setShapeColor(ColorState);
                  ((FreePencil) currentShape).addPoint(e.getX(), e.getY());
                }
            }
			
            public void mouseReleased(MouseEvent e) {
				if(LINE.equals(ShapeState) && dragged){
					currentShape.setParameter3(e.getX());
                    currentShape.setParameter4(e.getY());
                    shapeList.add(currentShape); 
				}else if ( (RECTANGLE.equals(ShapeState)  || OVAL.equals(ShapeState) )&& dragged){
					SetParameters(currentShape,e.getX(),e.getY());
					shapeList.add(currentShape);
				}else if(PENCIL.equals(ShapeState) && currentShape != null) {
                     shapeList.add(currentShape);
				}
				currentShape = null;
				dragged = false; 
				repaint();
            }

            public void mouseDragged(MouseEvent e) {
				dragged=true;
                if (LINE.equals(ShapeState) && currentShape != null) {
                    currentShape.setParameter3(e.getX());
                    currentShape.setParameter4(e.getY());
                } else if ( (RECTANGLE.equals(ShapeState) || OVAL.equals(ShapeState) ) && currentShape!=null){
					SetParameters(currentShape,e.getX(),e.getY());
				}else if(ERASER.equals(ShapeState)){
					currentShape = new Eraser();
					currentShape.setShapeColor(currentShape.getColor());
					currentShape.setParameter1(e.getX());
					currentShape.setParameter2(e.getY());
					currentShape.setParameter3(15);
					currentShape.setParameter4(15);
					shapeList.add(currentShape);
					currentShape=null;
				}else if (PENCIL.equals(ShapeState) && currentShape != null) {
                    ((FreePencil) currentShape).addPoint(e.getX(), e.getY());
                }
				repaint();
			}
        };
		
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
		
        add(undoButton);
		add(clearButton);
		add(LineButton);
		add(RectButton);
		add(OvalButton);
		add(pencileButton);
		add(BlackButton);
		add(RedButton);
		add(GreenButton);
		add(BlueButton);
		add(checkBox);
		add(eraserButton);
	}
	
	public void paint(Graphics g) {
       for (Shapes shape : shapeList) {
			shape.draw(g);
        }
		
       if (currentShape != null && dragged) {
          currentShape.draw(g);
        }
    }
	
	public void initColorButtons() {
		
		RedButton = new Button("Red");
        RedButton.setBackground(Color.RED);
        RedButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ColorState = Color.RED;
        }});
		
		GreenButton = new Button("Green");
        GreenButton.setBackground(Color.GREEN);
        GreenButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ColorState = Color.GREEN;
        }});
		
		 BlueButton = new Button("Blue");
         BlueButton.setBackground(Color.BLUE);
         BlueButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ColorState = Color.BLUE;
        }});
		
		 BlackButton = new Button("Blue");
         BlackButton.setBackground(Color.BLACK);
         BlackButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ColorState = Color.BLACK;
        }});
	} 
 
    public void initShapeButtons() {
		
        LineButton = new Button("Line");
		LineButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
		    ShapeState = LINE;
	    }});
		
		RectButton = new Button("Rectangle");
		RectButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
		    ShapeState = RECTANGLE;
	    }});
		
		OvalButton = new Button("Oval");
		OvalButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
		    ShapeState = OVAL;
	    }});
    }
	
	public void SetParameters(Shapes s,int endX,int endY){
	   int x = Math.min(startX, endX);
       int y = Math.min(startY, endY);
       int width = Math.abs(endX - startX);
       int height = Math.abs(endY - startY);

       s.setParameter1(x);
       s.setParameter2(y);
       s.setParameter3(width);
       s.setParameter4(height);
		
	}
}
