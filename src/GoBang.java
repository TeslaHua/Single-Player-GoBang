/**
* ���ƣ�������С��Ϸ
* ���ܣ��ɻ��壬���¿�ʼ��Ӯ����������ʾ
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
* ���ƣ�GoBang����
* ���ܣ��ɻ��壬���¿�ʼ��Ӯ����������ʾ
*/

public class GoBang extends JFrame
{
	private JPanel panel = new JPanel();
	private ChessPanel chesspanel = new ChessPanel();
	private JButton jbtRestart = new JButton("���¿�ʼ");
	private JButton jbtback = new JButton("����");
	private JLabel messageLabel = new JLabel("��Ϸ�� !");
	private final int Cols = 18;           //��������
	private final int Rows = 18;           //��������
	private final int Margin = 45;         //���ڱ߾�
	private final int Grid = 30;           //�������
	private Chess[] chessList = new Chess[Cols*Rows];  //���Ӷ��������

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

		//�����������һ����Ϸ
		jbtRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				chesspanel.index = 0;
				chesspanel.chessColor = " ";
				messageLabel.setText("��Ϸ�� !");
				repaint();
			}
		});
		//���Ҫ����
		jbtback.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(chesspanel.index>0)  //��ֹ��һ�û����ͻ���
				{
					chesspanel.index--;
					chesspanel.chessColor = (chesspanel.chessColor == "WHITE" ? "BLACK":"WHITE");
					repaint();
				}
			}
		});
	}

	/**�ڲ���ChessPanel��������
	*���ܣ� �����̣������ӣ��ж���Ӯ
	*/
	class ChessPanel extends JPanel
	{
		private URL Imageurl = getClass().getResource("chesspanel.jpg");
		private Image img = Toolkit.getDefaultToolkit().getImage(Imageurl);

		private URL Audiourl = getClass().getResource("Won.wav");    //һ��URL��λʤ��ʱ������
		private AudioClip audioClip = Applet.newAudioClip(Audiourl); //Ϊ�����ִ���һ�����ּ���

		private int index = 0;
		private String chessColor = " ";
		public ChessPanel()
		{
			this.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e)
				{
					//�������ڲ�������
					if(Margin<=(int)(e.getPoint().getX())&&(int)(e.getPoint().getX())<=getWidth()-Margin&&
						Margin<=(int)(e.getPoint().getY())&&(int)(e.getPoint().getY())<=getHeight()-Margin)
					{
						if(chessColor == " ")
							chessColor = "BLACK";
						int col =(int)((e.getPoint().getX()-Margin)/Grid); 
						int row =(int)((e.getPoint().getY()-Margin)/Grid);
						col = (((e.getPoint().getX()-Margin)/Grid)>col+0.5 ? col+1:col);
						row = (((e.getPoint().getY()-Margin)/Grid)>row+0.5 ? row+1:row);
						//�ж������Ƿ��Ѿ����¹�
						if(FindPieces(col,row))
							return ;
						chessList[index] = new Chess(chessColor);
						chessList[index].setLocation(col*Grid+Margin,row*Grid+Margin);
						System.out.println(index+"  "+chessList[index].getX()+"  "+chessList[index].getY()+"  "+chessList[index].getchessColor());
						index++;
						repaint();
						//�ж�����µĵ�ǰ���Ƿ�Ӯ
						if(IsWon(chessList,index-1))
						{
							audioClip.play();  //����ʤ������
							if(chessList[index-1].getchessColor()=="BLACK") 
								JOptionPane.showMessageDialog(null, "��ϲ����Ӯ�� !");
							else
								JOptionPane.showMessageDialog(null, "��ϲ����Ӯ�� !");
						}
						else if(index==Cols*Rows+1)
							messageLabel.setText("ƽ�֣������¿�ʼ!");
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
			//������
			g.drawImage(img,xindex,yindex,null);
			//������
			for(int i=0;i<Cols;i++)
				g.drawLine(Margin+(Grid*i),Margin,Margin+(Grid*i),getWidth()-Margin);
			//������
			for(int i=0;i<Rows;i++)
				g.drawLine(Margin,Margin+(Grid*i),getHeight()-Margin,Margin+(Grid*i));
			//������
			Graphics2D g2d = (Graphics2D)g;   //����ϵת����2D
			//ʹ�������ɫ����Ĺ��ܣ��û�����ָ�����ֻ���ֽ�����ɫ���˻��ƽ�����ɫ����ɫ֮���ṩһ����ֵ�� 
			//�����ֱ��ǣ�����Բ��Բ�����ں������꣬����뾶��colors���ڽ�����ʹ�õ���ɫ���顣��һ����ɫ���ڽ��㴦�����һ����ɫ������Բ����
			for(int i = 0;i <index;i++)
			{
				if(chessList[i].getchessColor() == "BLACK")
				{	
					RadialGradientPaint paint = new RadialGradientPaint((int)chessList[i].getX()+5,(int)chessList[i].getY()-5,15,new float[]{0f,1f},new Color[]{Color.WHITE,Color.BLACK});
					//�� Paint �ӿڶ������Ϊ Graphics2D ����������ɫģʽ����ʵ�� Paint �ӿڵ�����ӵ� Graphics2D �������У��Ա㶨�� draw �� fill ������ʹ�õ���ɫģʽ�� ��Ϊ����Щ������Ϊ setPaint ����������������ʱ������ Graphics2D ����������ʱ��Graphics2D ����������Щ����
					g2d.setPaint(paint);  
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //�����
					g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);//Alpha��ֵ
					Ellipse2D Oval = new Ellipse2D.Double((int)chessList[i].getX()-15,(int)chessList[i].getY()-15,chessList[index-1].getDiameter(),chessList[index-1].getDiameter());  //Ellipse2D������������ζ������Բ
					g2d.fill(Oval);  //�����Բ
					chessColor = "WHITE";
				}
				else if(chessList[i].getchessColor() == "WHITE")
				{
					RadialGradientPaint paint = new RadialGradientPaint((int)chessList[i].getX()+5,(int)chessList[i].getY()-5,60,new float[]{0f,1f},new Color[]{Color.WHITE,Color.BLACK});
					//�� Paint �ӿڶ������Ϊ Graphics2D ����������ɫģʽ����ʵ�� Paint �ӿڵ�����ӵ� Graphics2D �������У��Ա㶨�� draw �� fill ������ʹ�õ���ɫģʽ�� ��Ϊ����Щ������Ϊ setPaint ����������������ʱ������ Graphics2D ����������ʱ��Graphics2D ����������Щ����
					g2d.setPaint(paint);  
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //�����
					g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);//Alpha��ֵ
					Ellipse2D Oval = new Ellipse2D.Double((int)chessList[i].getX()-15,(int)chessList[i].getY()-15,chessList[index-1].getDiameter(),chessList[index-1].getDiameter());  //Ellipse2D������������ζ������Բ
					g2d.fill(Oval);   
					chessColor = "BLACK";
				}
				g2d.setColor(Color.BLUE);
				g2d.draw3DRect((int)chessList[index-1].getX()-15,(int)chessList[index-1].getY()-15,chessList[index-1].getDiameter(),chessList[index-1].getDiameter(),true);
			}
		}

		//�ж����̴��Ƿ��ظ�����
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
	/**�ڲ���Chess��������
	*���ܣ� ������ɫ�����ֱ꣬��
	*/
	class Chess extends Point
	{
		private String chessColor ;            //������ɫ
		public static final int Diameter = 30;    //ֱ��  
		
		//Ĭ�Ϲ��캯��
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

	//�ж��Ƿ������Ӯ
	public boolean IsWon(Chess[] chessList,int index)
	{
		int count = 1;
		//��ʼ��������
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
		//��ʼ��������
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
		//��ʼ��������
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
		//��ʼ��������
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
		//��ʼ����������
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
		//��ʼ����������
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
		//��ʼ����������
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
		//��ʼ����������
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
		myframe.setTitle("��������Ϸ");
		myframe.setLocationRelativeTo(null);
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setSize(616,681);
		myframe.setVisible(true);
	}
}


