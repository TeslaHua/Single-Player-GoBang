/**
* 名称：五子棋小游戏
* 功能：可悔棋，重新开始，赢会有音乐提示
*/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.border.*;
import java.net.URL;
import java.awt.geom.Ellipse2D;
import java.awt.Point;

/**
* 名称：GoBang主类
* 功能：可悔棋，重新开始，赢会有音乐提示
*/

public class GoBang extends JFrame
{
	private JPanel panel = new JPanel();
	private ChessPanel chesspanel = new ChessPanel();
	private JButton jbtRestart = new JButton("重新开始");
	private JButton jbtback = new JButton("悔棋");
	private JLabel messageLabel = new JLabel("游戏中 !");
	private final int Cols = 18;           //棋盘行数
	private final int Rows = 18;           //棋盘列数
	private final int Margin = 45;         //窗口边距
	private final int Grid = 30;           //网格距离
	private Chess[] chessList = new Chess[Cols*Rows];  //棋子对象的数组

	public GoBang()
	{
		setLayout(new BorderLayout(5,5));
		panel.setLayout(new FlowLayout());
		messageLabel.setFont(new Font("Serif",Font.BOLD,15));
		messageLabel.setForeground(Color.BLUE);
		messageLabel.setBorder(new TitledBorder("Game message."));
		messageLabel.setBorder(new LineBorder(Color.RED,2));

		panel.add(messageLabel);
		panel.add(jbtRestart);
		panel.add(jbtback);

		add(chesspanel,BorderLayout.CENTER);
		add(panel,BorderLayout.SOUTH);

		//玩家重新再玩一局游戏
		jbtRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				chesspanel.index = 0;
				chesspanel.chessColor = " ";
				messageLabel.setText("游戏中 !");
				repaint();
			}
		});
		//玩家要悔棋
		jbtback.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(chesspanel.index>0)  //防止玩家还没下棋就悔棋
				{
					chesspanel.index--;
					chesspanel.chessColor = (chesspanel.chessColor == "WHITE" ? "BLACK":"WHITE");
					repaint();
				}
			}
		});
	}

	/**内部类ChessPanel是棋盘类
	*功能： 画棋盘，画棋子，判断输赢
	*/
	class ChessPanel extends JPanel
	{
		private URL Imageurl = getClass().getResource("chesspanel.jpg");
		private Image img = Toolkit.getDefaultToolkit().getImage(Imageurl);

		private URL Audiourl = getClass().getResource("Won.wav");    //一个URL定位胜利时的音乐
		private AudioClip audioClip = Applet.newAudioClip(Audiourl); //为此音乐创建一个音乐剪辑

		private int index = 0;
		private String chessColor = " ";
		public ChessPanel()
		{
			this.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e)
				{
					//在棋盘内部的条件
					if(Margin<=(int)(e.getPoint().getX())&&(int)(e.getPoint().getX())<=getWidth()-Margin&&
						Margin<=(int)(e.getPoint().getY())&&(int)(e.getPoint().getY())<=getHeight()-Margin)
					{
						if(chessColor == " ")
							chessColor = "BLACK";
						int col =(int)((e.getPoint().getX()-Margin)/Grid); 
						int row =(int)((e.getPoint().getY()-Margin)/Grid);
						col = (((e.getPoint().getX()-Margin)/Grid)>col+0.5 ? col+1:col);
						row = (((e.getPoint().getY()-Margin)/Grid)>row+0.5 ? row+1:row);
						//判断棋子是否已经被下过
						if(FindPieces(col,row))
							return ;
						chessList[index] = new Chess(chessColor);
						chessList[index].setLocation(col*Grid+Margin,row*Grid+Margin);
						System.out.println(index+"  "+chessList[index].getX()+"  "+chessList[index].getY()+"  "+chessList[index].getchessColor());
						index++;
						repaint();
						//判断玩家下的当前棋是否赢
						if(IsWon(chessList,index-1))
						{
							audioClip.play();  //播放胜利音乐
							if(chessList[index-1].getchessColor()=="BLACK") 
								JOptionPane.showMessageDialog(null, "恭喜黑子赢了 !");
							else
								JOptionPane.showMessageDialog(null, "恭喜白子赢了 !");
						}
						else if(index==Cols*Rows+1)
							messageLabel.setText("平局，请重新开始!");
					}	
				}
			});
		}
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			int WinWidth = getWidth();
			int WinHeight = getHeight();
			int IWidth = img.getWidth(this);
			int IHeight = img.getHeight(this);
			int xindex = (WinWidth - IWidth)/2;
			int yindex = (WinHeight - IHeight)/2;
			//画棋盘
			g.drawImage(img,xindex,yindex,null);
			//画竖线
			for(int i=0;i<Cols;i++)
				g.drawLine(Margin+(Grid*i),Margin,Margin+(Grid*i),getWidth()-Margin);
			//画横线
			for(int i=0;i<Rows;i++)
				g.drawLine(Margin,Margin+(Grid*i),getHeight()-Margin,Margin+(Grid*i));
			//画棋子
			Graphics2D g2d = (Graphics2D)g;   //坐标系转换到2D
			//使其具有颜色渐变的功能，用户可以指定两种或多种渐变颜色，此绘制将在颜色与颜色之间提供一个插值。 
			//参数分别是，渐变圆的圆心所在横纵坐标，渐变半径，colors是在渐变中使用的颜色数组。第一种颜色用于焦点处，最后一种颜色环绕在圆周上
			for(int i = 0;i <index;i++)
			{
				if(chessList[i].getchessColor() == "BLACK")
				{	
					RadialGradientPaint paint = new RadialGradientPaint((int)chessList[i].getX()+5,(int)chessList[i].getY()-5,15,new float[]{0f,1f},new Color[]{Color.WHITE,Color.BLACK});
					//此 Paint 接口定义如何为 Graphics2D 操作生成颜色模式。将实现 Paint 接口的类添加到 Graphics2D 上下文中，以便定义 draw 和 fill 方法所使用的颜色模式。 因为将这些对象作为 setPaint 方法的属性来设置时，或者 Graphics2D 对象本身被复制时，Graphics2D 并不复制这些对象
					g2d.setPaint(paint);  
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //抗锯齿
					g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);//Alpha插值
					Ellipse2D Oval = new Ellipse2D.Double((int)chessList[i].getX()-15,(int)chessList[i].getY()-15,chessList[index-1].getDiameter(),chessList[index-1].getDiameter());  //Ellipse2D类描述窗体矩形定义的椭圆
					g2d.fill(Oval);  //填充椭圆
					chessColor = "WHITE";
				}
				else if(chessList[i].getchessColor() == "WHITE")
				{
					RadialGradientPaint paint = new RadialGradientPaint((int)chessList[i].getX()+5,(int)chessList[i].getY()-5,60,new float[]{0f,1f},new Color[]{Color.WHITE,Color.BLACK});
					//此 Paint 接口定义如何为 Graphics2D 操作生成颜色模式。将实现 Paint 接口的类添加到 Graphics2D 上下文中，以便定义 draw 和 fill 方法所使用的颜色模式。 因为将这些对象作为 setPaint 方法的属性来设置时，或者 Graphics2D 对象本身被复制时，Graphics2D 并不复制这些对象
					g2d.setPaint(paint);  
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //抗锯齿
					g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);//Alpha插值
					Ellipse2D Oval = new Ellipse2D.Double((int)chessList[i].getX()-15,(int)chessList[i].getY()-15,chessList[index-1].getDiameter(),chessList[index-1].getDiameter());  //Ellipse2D类描述窗体矩形定义的椭圆
					g2d.fill(Oval);   
					chessColor = "BLACK";
				}
				g2d.setColor(Color.BLUE);
				g2d.draw3DRect((int)chessList[index-1].getX()-15,(int)chessList[index-1].getY()-15,chessList[index-1].getDiameter(),chessList[index-1].getDiameter(),true);
			}
		}

		//判断棋盘处是否被重复下棋
		public boolean FindPieces(int col,int row)
		{
			int i = 0;
			while(i<index)
			{
				if((chessList[i].getX()-Margin)/Grid ==col&&(chessList[i].getY()-Margin)/Grid==row)
					return true;
				i++;		
			}
			return false;
		}
	}
	/**内部类Chess是棋子类
	*功能： 棋子颜色，坐标，直径
	*/
	class Chess extends Point
	{
		private String chessColor ;            //棋子颜色
		public static final int Diameter = 30;    //直径  
		
		//默认构造函数
		public Chess(String chessColor){
			this.chessColor = chessColor;
		}
		public String getchessColor()
		{
			return chessColor;
		}
		public int getDiameter()
		{
			return Diameter;
		}
	}

	//判断是否有玩家赢
	public boolean IsWon(Chess[] chessList,int index)
	{
		int count = 1;
		//开始往左索引
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[j].getY()==chessList[index].getY()&&chessList[j].getX()==chessList[index].getX()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往右索引
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[j].getY()==chessList[index].getY()&&chessList[index].getX()==chessList[j].getX()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往上索引
		count=1;
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[j].getX()==chessList[index].getX()&&chessList[j].getY()==chessList[index].getY()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往下索引
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[j].getX()==chessList[index].getX()&&chessList[index].getY()==chessList[j].getY()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往左上索引
		count=1;
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[j].getX()==chessList[index].getX()-i*Grid&&chessList[j].getY()==chessList[index].getY()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往右下索引
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[index].getX()==chessList[j].getX()-i*Grid&&chessList[index].getY()==chessList[j].getY()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往右上索引
		count=1;
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[index].getX()==chessList[j].getX()-i*Grid&&chessList[j].getY()==chessList[index].getY()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		//开始往左下索引
		for(int i=1; i<5;i++)  
		{
			int j=index-1;
			for(;j>=0 ; j--)
				if(chessList[j].getX()==chessList[index].getX()-i*Grid&&chessList[index].getY()==chessList[j].getY()-i*Grid
				&&chessList[j].getchessColor()==chessList[index].getchessColor())
				{
					count++;
					break;
				}
			if(j==-1)
				break;
		}
		if(count==5)
			return true;
		return false;
	}
	public static void main(String[] args)
	{
		GoBang myframe = new GoBang();
		myframe.setTitle("五子棋游戏");
		myframe.setLocationRelativeTo(null);
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setSize(616,681);
		myframe.setVisible(true);
	}
}


