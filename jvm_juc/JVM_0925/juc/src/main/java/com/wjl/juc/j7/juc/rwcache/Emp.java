package com.wjl.juc.j7.juc.rwcache;

import java.math.BigDecimal;

/**
 * @version 1.0
 * @Author Wang Jianlong
 * Created on 2022/10/6 19:15
 */
public class Emp {
    private int empno;
    private String ename;
    private String job;
    private BigDecimal sal;

    public void  a(){

    }
    public int getEmpno() {
        return empno;
    }

    public void setEmpno(int empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public BigDecimal getSal() {
        return sal;
    }

    public void setSal(BigDecimal sal) {
        this.sal = sal;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "empno=" + empno +
                ", ename='" + ename + '\'' +
                ", job='" + job + '\'' +
                ", sal=" + sal +
                '}';
    }
}
