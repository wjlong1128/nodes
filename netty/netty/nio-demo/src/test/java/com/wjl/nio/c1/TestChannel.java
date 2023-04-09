package com.wjl.nio.c1;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class TestChannel {

    Logger log = LoggerFactory.getLogger(TestChannel.class);

    /**
     *  文件复制1
     */
    @Test
    public void testFileChannel(){
        try(
                FileChannel from = new FileInputStream(TestByteBuffer.FILE_DATA_TXT).getChannel();
                FileChannel to = new FileOutputStream(TestByteBuffer.FILE_DATA_TXT+"1.txt").getChannel()
        ){
            // 0拷贝 最多2G
            // 突破2G
            long size = from.size();
            for (long left = size;left>0;) {
                log.debug("起点{}",size-left);
                left -= from.transferTo((size-left),left,to);
                log.debug("结束{}",left);
            }
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    @Test
    public void testPath() throws IOException {
        Path path = Paths.get("D:/Program Files/exploitation/work/sts/gitee/Netty/netty/nio-demo/");
        log.debug(path.normalize().toString()); //D:\Program Files\exploitation\work\sts\gitee\Netty\netty\nio-demo\src\main\resources\data.txt
        // 设计模式 访问者
        Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
            // 进入文件夹之前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("进入{}",dir.normalize().toString().substring(50));
                return super.preVisitDirectory(dir, attrs);
            }
            // 遍历到文件之后
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("对应的文件{}",file.normalize().toString().substring(50));
                return super.visitFile(file, attrs);
            }
            // 遍历文件失败
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return super.visitFileFailed(file, exc);
            }
            // 从文件夹出来之后
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                log.debug("退出{}",dir.normalize().toString().substring(50));
                return super.postVisitDirectory(dir, exc);
            }
        });

    }

    @Test
    public void countClass() throws IOException {
        Path path = Paths.get("D:/Program Files/exploitation/work/sts/gitee/Netty/netty/nio-demo/");
        final AtomicInteger integer = new AtomicInteger();
        Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.toString().endsWith(".class")){
                    integer.incrementAndGet();
                    log.debug("当前文件是{}",file.normalize().toString().substring(50));
                }
                return super.visitFile(file, attrs);
            }
        });
        log.debug("一共{}个class文件",integer);
    }
}



