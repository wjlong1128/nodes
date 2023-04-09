# Git  

![图标](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221125354.png)

[Git官网](https://git-scm.com/)  
[安装教程](https://www.cnblogs.com/xueweisuoyong/p/11914045.html)  `总结：无脑下一步`  
安装完成之后打开`Git Bash Here`输入`git --version`查看当前Git版本

官网介绍
>Git 是一个 免费的开源软件 分布式版本控制系统旨在处理从小到大的一切 非常大的项目，速度和效率。  
Git 易于学习 并且具有 占用空间小，性能快如闪电 。 它优于 SCM 工具，如 Subversion、CVS、Perforce 和 ClearCase 具有 等功能 便宜的本地分支 ， 方便的 集结区 ，以及 多个工作流程 。

## Git代码  

### Git常用命令

|命令代码|作用|  
|:--:|:--:|  
|git config--global user.name 用户名|设置用户签名|  
|git config--gloabl user.email 邮箱|设置用户签名|  
|git init|初始化本地库|  
|git status|查看本地库状态|  
|git add 文件名|添加到暂存区|  
|git commit -m "日志信息" 文件名|提交到本地库|  
|git reflog|查看历史记录|  
|git reset --hard 版本号|版本穿梭|  

查看用户名信息在电脑用户下面的`.gitconfig`文件里面  
![user](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221135950.png)  
**注意**：只是个签名 和账号有区别  
git初始化本地库在对应文件夹里面执行`git init`  
![init](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221140558.png)  
**注意：** `.git目录是默认隐藏的` **别乱动**  
```git
$ git status //查看当前状态

On branch master //当前分支
No commits yet  //没有提交过东西
nothing to commit (create/copy files and use "git add" to track)
```

有文件未上传执行此命令，当有文件却未上传时报红，意思是`此文件未被追踪`
执行`git add 文件`之后再次`git status`  
![status](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221142306.png)  
意思是已经成功添加到暂存区  

执行`git rm --cached 文件名`之后显示`rm 文件名`表示将暂存区的此文件移除成功！ `工作区`也就是本地还存在
![git rm --cached](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221142617.png)  

提交到本地库`git commit -m "版本号" 文件名`
![git commit -m](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221143527.png)  

`git reflog`查看提交版本  
![gitreflog](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221144125.png)  

`git log`查看详细提交信息  
![git log](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221144227.png)  

版本穿梭`git reset --hard 之前提交的版本号`  
![reset --hard](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221150856.png)  

查看当前使用的使用的是什么分支  
![master](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221151302.png)  



查看master分支指向哪一个版本号  
![master2](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221151142.png)  
![master3](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221151556.png)  

### 分支操作  

|命令|作用|  
|:--|:--|  
|git branch 分支名|创建分支|  
|git branch-v|查看分支|  
|git checkout 分支名|切换分支|  
|git merge 分支名|把指定的分支合并到当前分支上|  

`git branch -v` 查看当前分支
![git branch -v](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221155151.png)  

`git branch 分支名`创建一个分支  
不显示任何

`git checkout 分支名`切换分支  
![gitcheckout](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221155702.png)  

`git merge 分支`合并另一条分支  
![git merge 分支](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/20211221160641.png)  

#### 冲突合并

**原因：** 两个分支对同一个文件的同一个位置有两套不同的修改。Git无法替我们决定使用哪一个。必须人为决定 

[相关链接资料](https://www.cnblogs.com/shuimuzhushui/p/9022549.html)
> 例子： 多个分支都修改同一个位置并且提交 Git选择困难



## GitHub  

[官网](https://github.com[)

### 常用命令

`git remote -v 查看当前远程库别名`

`git remote add 别名 地址  `     [别名最好和远程库（本地）一致]

> 例如
>
> $ git remote add git-demo https://github.com/Hsrwjl/wjlgihub

![image-20211223102702292](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223102702292.png)

此时 后面是远程库链接 前面是别名

**注意！这个才是远程库地址**

![image-20211223103011461](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223103011461.png)



#### 将本地仓库与远程库建立链接

语法：

推送： `gti push 别名/远程地址 分支名`

拉取 ： `git pull 别名/地址  分支`



#### 克隆代码

`git clone 仓库地址`不需要登录 默认别名为orgin



#### ssh

![image-20211223113008117](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223113008117.png)

***敲三次回车***

## idea 集成

### 集成git

在电脑用户的文件夹里建立一个`git.ignore` 文件

该配置如下

```txt
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

hs_err_pid*

.classpath
.project
.settings
target
.idea
*.iml
```

然后在.gitconfig文件中引入

```txt
[core]
	excludesfile=C:/Users/还是人物经历/git.ignore
```

```git
$ ssh-keygen -t rsa -C wangqingshan4198146@gmail.com
```





idea中

![image-20211223122908825](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223122908825.png)

![image-20211223123020052](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223123020052.png)

黄色表示被忽略的文件 git不会追踪

![image-20211223123804269](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223123804269.png)



合并分支

![image-20211223125456817](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223125456817.png)

两个分支修改同样的地方之后提交 之后的 合并引起的冲突【自己修改  Ps：选左还是选右？】

![image-20211223130336589](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223130336589.png)

![image-20211223130627364](https://gitee.com/hsrwjl/phonePictureBed/raw/master/img/image-20211223130627364.png)

### 集成Github

开发新代码之前请记得先拉取，避免不必要的麻烦，这是一种规范准则



## Gitee  

一样一样

## GitLab

后期Linux在来看吧
