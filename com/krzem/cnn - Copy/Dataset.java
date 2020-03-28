package com.krzem.cnn - Copy;



import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;



public class Dataset{
	public static final int IM_SIZE=128;
	private List<int[][][]> data;
	private List<String> ll;



	public Dataset(){
		this.data=new ArrayList<int[][][]>();
		this.ll=new ArrayList<String>();
	}



	public int size(){
		return this.data.size();
	}



	public int[][][] getI(int i){
		return this.data.get(i);
	}



	public String getL(int i){
		return this.ll.get(i);
	}



	public void shuffle(){
		List<Integer> idx_l=new ArrayList<Integer>();
		for (int i=0;i<this.data.size();i++){
			idx_l.add(i);
		}
		Collections.shuffle(idx_l);
		List<int[][][]> ndata=new ArrayList<int[][][]>();
		List<String> nll=new ArrayList<String>();
		for (int idx:idx_l){
			ndata.add(this.data.get(idx));
			nll.add(this.ll.get(idx));
		}
		this.data=ndata;
		this.ll=nll;
	}



	public static Dataset load(String dir,int m){
		Dataset dt=new Dataset();
		for (File f:new File(dir).listFiles()){
			if (!f.isFile()||!f.getName().endsWith(".jpg")){
				continue;
			}
			dt._add(f,m);
		}
		return dt;
	}



	public static BufferedImage get_img(String fp){
		try{
			return (BufferedImage)ImageIO.read(new File(fp));
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	public static int[][][] convert(BufferedImage i){
		int[][][] img_dt=new int[4][IM_SIZE][IM_SIZE];
		int[][] r=new int[IM_SIZE][IM_SIZE];
		int[][] g=new int[IM_SIZE][IM_SIZE];
		int[][] b=new int[IM_SIZE][IM_SIZE];
		int[][] gray=new int[IM_SIZE][IM_SIZE];
		for (int x=0;x<IM_SIZE;x++){
			for (int y=0;y<IM_SIZE;y++){
				Color c=new Color(i.getRGB(x,y));
				r[x][y]=c.getRed();
				g[x][y]=c.getGreen();
				b[x][y]=c.getBlue();
				gray[x][y]=(c.getRed()+c.getGreen()+c.getBlue())/3;
			}
		}
		img_dt[0]=r;
		img_dt[1]=g;
		img_dt[2]=b;
		img_dt[3]=gray;
		return img_dt;
	}



	public static int[][][] downsample(int[][][] i,int m){
		if (i[0].length%m!=0||i[0][0].length%m!=0){
			System.out.println("Downsampling failed.");
			return i;
		}
		int[][][] o=new int[4][i[0].length/m][i[0][0].length/m];
		o[0]=Dataset._downsample(i[0],m);
		o[1]=Dataset._downsample(i[1],m);
		o[2]=Dataset._downsample(i[2],m);
		o[3]=Dataset._downsample(i[3],m);
		return o;
	}



	private void _add(File f,int m){
		BufferedImage i=Dataset.get_img(f.getAbsolutePath());
		if (i.getWidth()!=IM_SIZE||i.getHeight()!=IM_SIZE){
			System.out.println("Skipping: "+f.getName()+" (Wrong Size)");
			return;
		}
		String l=f.getName().substring(0,f.getName().lastIndexOf("_"));
		this.data.add((m>1?Dataset.downsample(Dataset.convert(i),m):Dataset.convert(i)));
		this.ll.add(l);
	}



	private static int[][] _downsample(int[][] img,int m){
		int[][] o=new int[img.length/m][img[0].length/m];
		for (int i=0;i<img.length/m;i++){
			for (int j=0;j<img.length/m;j++){
				int sum=0;
				for (int k=i*m;k<(i+1)*m;k++){
					for (int l=j*m;l<(j+1)*m;l++){
						sum+=img[k][l];
					}
				}
				o[i][j]=sum/(m*m);
			}
		}
		return o;
	}
}