package com.joshua.a51bike.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadData {
       byte carState;// 0x01 锁定 02 运行状态 03 第一次进入锁定状态  04 异常状态
       byte carBattery; // 电动车电池容量
       byte backupCarBattery; // 后备电池容量
       byte alertInfo;// 报警信息  01 后备电池欠压报警 02 电频移除报警 03 振动报警
       byte lockState;// 锁状态 01 蓝牙控制进入锁状态 02 退出蓝牙开锁状态 03 后台控制进入锁状态 04 退出后台控制开锁状态
       byte []gps=new byte[6]; //gps数据时间  bcd码
       byte []weidu=new byte[8];// bcd码
       byte weidubanqiu; //纬度半球  ascii码
       byte []jindu=new byte[8]; // 经度 bcd
       byte jindubanqiu; // 经度半球 ascii
       byte carModel;// 车辆模式

	public int getCarBattery2() {
		return carBattery2;
	}

	public void setCarBattery2(int carBattery2) {
		this.carBattery2 = carBattery2;
	}

	public int carBattery2; // 电动车电池容量

	public double getWei() {
		return wei;
	}

	public void setWei(double wei) {
		this.wei = wei;
	}

	public double getJin() {
		return jin;
	}

	public void setJin(double jin) {
		this.jin = jin;
	}

	public long getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(long gpsTime) {
		this.gpsTime = gpsTime;
	}

	public double wei; // 韦
	public double jin; // 经
       
    public long gpsTime;//时间戳
       
       
       public void info() {
    	   System.out.println("################");
           StringBuffer buffer=new StringBuffer();
           switch (carState) {
		          case 0x01:
			           buffer.append("车状态 锁定\n");
			      break;
		          case 0x02:
			           buffer.append("车状态 运行\n");
			      break;
		          case 0x03:
			           buffer.append("车状态 第一次进入锁定状态\n");
			      break;
		          case 0x04:
			           buffer.append("车状态 异常\n");
			      break;

		          default:
			      break;
		   }
           String str=Integer.toHexString(carBattery);
//           System.out.println(Integer.valueOf(str,16).toString()); 
           buffer.append("电池容量  "+Integer.valueOf(str,16).toString()+"%\n");
           str=Integer.toHexString(backupCarBattery);
//           System.out.println(Integer.valueOf(str,16).toString()); 
           buffer.append("后备电池容量  "+Integer.valueOf(str,16).toString()+"%\n");
           switch (alertInfo) {
	          case 0x01:
		           buffer.append("报警信息 后备电池欠压报警\n");
		      break;
	          case 0x02:
		           buffer.append("报警信息  电频移除报警 \n");
		      break;
	          case 0x03:
		           buffer.append("报警信息  振动报警\n");
		      break;
	          default:
		      break;
           }
           switch (lockState) {
	          case 0x01:
		           buffer.append("锁状态 蓝牙控制进入锁状态 \n");
		      break;
	          case 0x02:
		           buffer.append("锁状态   退出蓝牙开锁状态 \n");
		      break;
	          case 0x03:
		           buffer.append("锁状态   后台控制进入锁状态 \n");
		      break;
	          case 0x04:
		           buffer.append("锁状态   退出后台控制开锁状态 \n");
		      break;
	          default:
		      break;
        }   
           System.out.println(buffer);
	       System.out.println("#######################");
//	       StringBuffer
    	  
    }
   public void parseGPSTime(byte gps[]) {
	      byte temp[]=new byte[]{0x17,0x03,0x29,0x07,0x21,0x33};    
//	      temp.toString()      
   }
   public void parseGPSTime()  {
	      byte temp[]=new byte[]{0x17,0x03,0x29,0x07,0x21,0x33};    
          
          String year=Integer.toHexString(temp[0]);
          String month=Integer.toHexString(temp[1]);
          String day=Integer.toHexString(temp[2]);
          String hour=Integer.toHexString(temp[3]);
          String minitue=Integer.toHexString(temp[4]);
          String second=Integer.toHexString(temp[5]);
          SimpleDateFormat format=new  SimpleDateFormat("yy-MM-dd HH:mm:ss");
          
          String time=year+"-"+month+"-"+day+" "+hour+":"+minitue+":"+second;
          try {
			Date date=format.parse(time);
			System.out.println("time "+date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		  
          //(119.306534,32.121130)
          
   }
   public void parseWeidu() {
	      // ddmm.mmmmm
	      byte[] weidu=new byte[]{0x32,0x12,0x11,0x30,0x00};
	      String du=Integer.toHexString(weidu[0]);
	      String fen=Integer.toHexString(weidu[1]);
	      System.out.println("##### 维度");
	      System.out.println("度 "+du);
	      System.out.println("分 "+fen);
	      String half=Integer.toHexString(weidu[4]);
	      
	      String miao=Integer.toHexString(weidu[2])+Integer.toHexString(weidu[3])+half.charAt(0);
	      
//	      int i=(weidu[2]<<8)&(weidu[3]&0xffff);
//	      Integer m=Integer.parseInt("2399",16);
	      Double d=Double.parseDouble("0."+miao)*60;
	      
	      System.out.println("秒 "+d);
	      System.out.println("wei "+du+"."+fen+"."+d);
	      
	      // ddmmm.mmmmm
	      // 经度
	      System.out.println("###### "+"经度");
	      byte[] jindu=new byte[]{(byte) 0x11,(byte) 0x93,(byte) 0x06,(byte) 0x53,(byte) 0x43};
	      du=Integer.toHexString(jindu[0]&0xff);
	      String tem=Integer.toHexString(jindu[1]&0xff);
	      if (tem.length()==1)
	    	  tem="0"+tem;
	      du=du+tem.charAt(0);
//	      System.out.println("度 "+du);
//	      fen=tem.charAt(1);
	      String temp=Integer.toHexString(jindu[2]&0xff);
	      if(temp.length()==1)
	    	  temp="0"+temp;
	      fen=tem.charAt(1)+""+temp.charAt(0);
	      
	      System.out.println("度 "+du);
	      System.out.println("分  "+fen);
	      miao=temp.charAt(1)+Integer.toHexString(jindu[3])+Integer.toHexString(jindu[4]);
	      Double j=d.parseDouble("0."+miao)*60;
	      System.out.println("秒 "+j);
	      System.out.println("jin "+du+"."+fen+"."+j);
	      
	      // (32.12678,119.3039205)
	      // (119.3039205,32.12678)
   }
   public void name() {
	      String str="12.11300";
	      Double d=Double.parseDouble(str);
	      System.out.println(d/60);
	      // 0.20188333333333333
	      str="30.65343";
	      d=Double.parseDouble(str);
	      System.out.println(d/60);
	      //0.5108905
	      //(32.20188333333333333,119.5108905)
	      
	      
	      
	      
  }
   
   public void jinwei() {
//	      byte[] weidu=new byte[]{0x32,0x12,0x11,0x30,0x00};
	      String du=Integer.toHexString(weidu[0]&0xff);
          String fen=Integer.toHexString(weidu[1]&0xff);
          String tem=Integer.toHexString(weidu[4]&0xff);
          String miao=Integer.toHexString(weidu[2]&0xff)+Integer.toHexString(weidu[3]&0xff)+tem.charAt(0);
          Double d=Double.parseDouble(fen+"."+miao)/60;
          d=Double.parseDouble(du)+d;
          
          System.out.println("维度  "+d);
          
//          byte[] jindu=new byte[]{(byte) 0x11,(byte) 0x93,(byte) 0x06,(byte) 0x53,(byte) 0x43};
          du=Integer.toHexString(jindu[0]&0xff);
          tem=Integer.toHexString(jindu[1]&0xff);
          if (tem.length()==1)
        	  tem="0"+tem;
          du=du+tem.charAt(0);
//          fen=tem.charAt(1)+Integer.toHexString(weidu[2]&0xff);
          String tem1=Integer.toHexString(jindu[2]&0xff);
          if (tem1.length()==1)
        	  tem1="0"+tem1;
          fen=tem.charAt(1)+""+tem1.charAt(0);
          miao=tem1.charAt(1)+Integer.toHexString(jindu[3]&0xff)+Integer.toHexString(jindu[4]&0xff);
          d=Double.parseDouble(fen+"."+miao)/60;
          d=Double.parseDouble(du)+d;
          System.out.println("经度 "+d);
          
          
   
   
   }
   
   
   
   
       
       
}
