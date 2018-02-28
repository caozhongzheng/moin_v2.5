package com.moinapp.wuliao.modules.sticker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StickerImgUtils {

	private static final byte[] MOIN_STICKER_PSW_BS = {-25, -128, -56, -34, -23, -67, -55, 34, 34, 126, 70, 125, 108, 56, -112, -57};//"pi_3%^p_89^&(8^"
	private static final int SECTION_LEN = MOIN_STICKER_PSW_BS.length;
	private static final String MOI_FILE_MARK = "MOIN";
	
	public static byte[] encode(String json, byte[] img){
		byte[] jsonbs = json.getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(jsonbs.length +img.length+MOI_FILE_MARK.getBytes().length+4);
		//head
		byteBuffer.put("MOIN".getBytes());
		byteBuffer.putInt(jsonbs.length);
		
		//json
		byteBuffer.put(jsonbs);
		
		byteBuffer.put(img);
		
		byteBuffer.flip();
		
		int len = byteBuffer.limit();
		byte[] src = byteBuffer.array();
		
		byte[] dst = new byte[len];
		
		for(int i = 0 ; i < len ; i+=SECTION_LEN){
			
			for(int j = 0 ; j < (len -SECTION_LEN > i ? SECTION_LEN : len - i) ; j++){
				
				dst[i+j] = (byte) (src[i+j] ^ StickerImgUtils.MOIN_STICKER_PSW_BS[j]);
				
			}
			
		}
		return dst;
	}
	
	public static StickerImg decode(byte[] moi){
		int len = moi.length;
		
		ByteBuffer dst = ByteBuffer.allocate(len);
		
		for(int i = 0 ; i < len ; i+=SECTION_LEN){
			
			for(int j = 0 ; j < (len -SECTION_LEN > i ? SECTION_LEN : len - i) ; j++){
				
				dst.put((byte) (moi[i+j] ^ StickerImgUtils.MOIN_STICKER_PSW_BS[j]));
				
			}
			
		}
		
		dst.flip();
		
		byte[] mark = new byte[4];
		dst.get(mark);
		//check mark;
		
		StickerImg si = new StickerImg();
		
		int jsonLen = dst.getInt();
		byte[] json = new byte[jsonLen];
		dst.get(json);
		
		si.setJson(new String(json));
		
		byte[] img = new byte[len - jsonLen - 8];
		dst.get(img);
		
		si.setImg(img);
		
		return si;
	}
	
	public static final class StickerImg{
		String json;
		byte[] img;
		byte[] moi;
		public String getJson() {
			return json;
		}
		public void setJson(String json) {
			this.json = json;
		}
		public byte[] getImg() {
			return img;
		}
		public void setImg(byte[] img) {
			this.img = img;
		}
		public byte[] getMoi() {
			return moi;
		}
		public void setMoi(byte[] moi) {
			this.moi = moi;
		}
		
		
	}
	
	
	private static void sampleEncode(){
		String json = "{id:\"dssss\",name:\"name\"}";
		String imgFileName = "/Users/mylk/Documents/debug/cainixihuan.png";
		String dstFileName = "/Users/mylk/Documents/debug/cainixihuan.mio";
		long cmt = System.currentTimeMillis();
		System.out.println("sampleEncode start.");
		try{
			byte[] img = readFile(imgFileName);
			
			byte[] moi = StickerImgUtils.encode(json, img);
			
			writeFile(dstFileName, moi);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		System.out.println("sampleEncode completed. cost:"+(System.currentTimeMillis()-cmt));
	}
	
	private static void sampleDecode(){
		String moiFileName = "/Users/mylk/Documents/debug/cainixihuan.mio";
		String decodeImgFileName = "/Users/mylk/Documents/debug/cainixihuande.png";
		long cmt = System.currentTimeMillis();
		System.out.println("sampleDecode start.");
		try{
			byte[] moi = readFile(moiFileName);
			
			StickerImg si = StickerImgUtils.decode(moi);
			
			System.out.println("decode completed. cost:"+(System.currentTimeMillis()-cmt));
			
			System.out.println(si.getJson());
			writeFile(decodeImgFileName, si.getImg());
		}catch(IOException ex){
			ex.printStackTrace();
		}
		System.out.println("sampleDecode completed. cost:"+(System.currentTimeMillis()-cmt));
		
	}
	
	public static byte[] readFile(String fileName) throws IOException{
		FileChannel channel = null;
		FileInputStream fs = null;
		
		try{
			fs = new FileInputStream(fileName);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
			}
			
			return byteBuffer.array();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void writeFile(String fileName, byte[] content) throws IOException{
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
			fos.write(content);
		}catch(IOException e){
			throw e;
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
