package com.wjl.nio.c1;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wjl.nio.utils.ByteBufferUtil.debugAll;

public class TestByteBuffer {
	
	Logger log = LoggerFactory.getLogger(TestByteBuffer.class);
	
	public static final String FILE_DATA_TXT = "D:/Program Files/exploitation/work/sts/gitee/Netty/netty/nio-demo/src/main/resources/data.txt";

	// 模板
	@Test
	public void test() {
		// 
	}
	
	@Test
	public void fileChannl() {
		// 输入输出流
		try(FileChannel channel = new FileInputStream(FILE_DATA_TXT).getChannel()){
			// 准备缓冲区
			// 注意 缓冲区设定的字节数是设定能够[装下]多少字节[byte]的
			ByteBuffer bf = ByteBuffer.allocate(10);
			// 向buf写入数据
			// 从channel读 向buf写
			while(true) {
				int i = channel.read(bf);
				log.debug("当前读取到{}个字节",i);
				if(i ==- 1) {
					break;
				}
				// 切换至 读模式
				bf.flip();
				// 是否还有 还有就继续读
				while (bf.hasRemaining()) {
					byte b = bf.get();
					log.debug("实际读取到的字节：{}",(char)b);
				}
				
				// 读取完之后切换为 写 模式否则循环无法写入数据
				bf.clear();
			}
		}catch(Exception e) {
			log.debug(e.getMessage());
		}
	}
	
	@Test
	public void testByteBufferReadWrite() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put((byte)0x61); // a
		buffer.put((byte)0x61); // a
		debugAll(buffer);
		buffer.flip();
		debugAll(buffer);
		log.debug("读取到{}",buffer.get());
		debugAll(buffer);
		buffer.compact();
		log.debug("执行了compact~~");
		buffer.put((byte)0X63);
		debugAll(buffer);
	}

	@Test
	public void allocate(){
		/**
		 *  java.nio.HeapByteBuffer  java堆内存
		 *  	- 读写效率低 【gc】
		 *  java.nio.DirectByteBuffer  直接内存
		 *  	- 读写效率高 零拷贝 不受垃圾回收
		 */
		ByteBuffer allocate = ByteBuffer.allocate(10);
		ByteBuffer allocateDirect = ByteBuffer.allocateDirect(10);
		log.debug(allocate.getClass().getName());
		log.debug(allocateDirect.getClass().getName());
	}

	@Test
	public void rewind() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(new byte[]{'a','b','c','d'});
		buffer.flip();
		buffer.get(new byte[2]);
		debugAll(buffer);
		buffer.mark();
		buffer.get(new byte[2]);
		debugAll(buffer);
		//buffer.rewind();
		buffer.reset();
		debugAll(buffer);
		buffer.get(new byte[2]);
		debugAll(buffer);
	}

	@Test
	public void byteBufferString() {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.put("Hello".getBytes());
		debugAll(buffer);

		buffer.put(StandardCharsets.UTF_8.encode("Hello"));
		debugAll(buffer);
	}

	/**
	 *  分散读取
	 */
	@Test
	public void byteBuffers() throws IOException {
		/*try(FileChannel channel = new FileInputStream(FILE_DATA_TXT).getChannel()){
			ByteBuffer bf1 = ByteBuffer.allocate(4);
			ByteBuffer bf2 = ByteBuffer.allocate(4);
			ByteBuffer bf3 = ByteBuffer.allocate(4);
			channel.read(new ByteBuffer[]{bf1,bf2,bf3});
			bf1.flip();
			bf2.flip();
			bf3.flip();
			debugAll(bf1);
			debugAll(bf2);
			debugAll(bf3);
		}catch (Exception e){
			e.getStackTrace();
		}*/
		ByteBuffer b1 = StandardCharsets.UTF_8.encode("HELLO");
		ByteBuffer b2 = StandardCharsets.UTF_8.encode("王");
		ByteBuffer b3 = StandardCharsets.UTF_8.encode("AAAA");
		// StandardCharsets.UTF_8.encode创建只读的buffer
		log.error("读取{}",b1.get(2));
		RandomAccessFile file = new RandomAccessFile(FILE_DATA_TXT + "1.txt", "rw");
		FileChannel channel = file.getChannel();
		channel.write(new ByteBuffer[]{b1,b2,b3});
		channel.close();
		file.close();
	}
	@Test
	public void byteBuffers2() throws IOException {
//		// ByteBuffer allocate = ByteBuffer.allocate(16);
//		ByteBuffer allocate =StandardCharsets.UTF_8.encode("123");
//		FileInputStream fileInputStream = new FileInputStream(FILE_DATA_TXT);
//		FileOutputStream fileOutputStream = new FileOutputStream(FILE_DATA_TXT + "1.txt");
//		FileChannel inchannel = fileInputStream.getChannel();
//		FileChannel outchannel1 = fileOutputStream.getChannel();
//
//		inchannel.read(allocate);
//		allocate.flip();
//		outchannel1.write(allocate);
//
//
//		inchannel.close();
//		outchannel1.close();
//		fileInputStream.close();
//		fileOutputStream.flush();
//		fileOutputStream.close();
		ByteBuffer encode = StandardCharsets.UTF_8.encode("123");
		debugAll(encode);
		//debugAll(encode);
	}

	@Test
	public void stickyBag(){
		ByteBuffer source = ByteBuffer.allocate(32);
		//                     11            24
		source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
		split(source);

		source.put("w are you?\nhaha!\n".getBytes());
		split(source);
	}
	// 发送信息
	private static void split(ByteBuffer source) {
		source.flip();
		// limit 读的上限
		for (int i = 0; i < source.limit(); i++) {
			// get(index)不会导致读指针移动 get才会
			// 找到完整消息
			if (source.get(i) == '\n') {
				// 换行符所在位置加1减去索引起始位置
				int lenth = i+1-source.position();
				ByteBuffer buffer = ByteBuffer.allocate(lenth);
				// 从消息读 写到buffer
				for (int j = 0; j < lenth; j++) {
					byte b = source.get();
					buffer.put(b);
				}
				debugAll(buffer);
			}
		}
		// 消息不能重头写 导致整个buf消息丢失 所以把字节向前移动
		source.compact();
	}

	public static void main(String[] args) {
		System.out.println((byte)'\n');
	}
}
