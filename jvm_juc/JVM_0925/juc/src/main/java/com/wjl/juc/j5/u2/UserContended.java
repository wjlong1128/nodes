package com.wjl.juc.j5.u2;

import sun.misc.Contended;


/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/4 19:08
 */

public class UserContended {
    @Contended("wjl-padding")
    private volatile long WJLVALUE = 100L;

}
