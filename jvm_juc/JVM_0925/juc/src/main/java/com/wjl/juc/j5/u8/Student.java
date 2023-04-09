package com.wjl.juc.j5.u8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 21:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    volatile int id;
    volatile String name;
}
