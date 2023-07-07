<p align="center"><a href="https://oddfar.com/" target="_blank" rel="noopener noreferrer"><img width="180" src="https://note.oddfar.com/img/web.png" alt="logo"></a></p>

<p align="center">
  <a href="https://github.com/oddfar/campus-imaotai/stargazers"><img src="https://img.shields.io/github/stars/oddfar/campus-imaotai.svg"></a>
	<a href="https://github.com/oddfar/campus-imaotai/blob/master/LICENSE"><img src="https://img.shields.io/github/license/oddfar/campus-imaotai"></a>
</p>



<p align="center"> i茅台app自动预约，每日自动预约，支持docker一键部署</p>

<h2 align="center">Campus-imaotai</h2>

  [笔记仓库](https://github.com/oddfar/notes)  |  [我的博客](https://oddfar.com)  

## 项目介绍

i茅台app，每日自动预约茅台，可批量添加用户，可选本市出货量最大的门店，或预约你的位置附近门店

## 使用教程

### Docker部署

在 [release](https://github.com/oddfar/campus-imaotai/releases) 页面下载压缩包并解压。

- 运行容器: `docker-compose up -d`

  打开浏览器，输入：([http://localhost:80](http://localhost/))，若能正确展示页面，则表明环境搭建成功。

使用端口，mysql：3306，redis：6379，nginx：80，campus-service：8160

若端口被占用，修改`docker-compose.yml`里的端口配置，例：`3306:3306` 改成 `3307:3306`

### 配置

**1、添加用户**

i茅台->用户管理->添加账号（若你有token，可直接点击“直接添加”）

**2、修改配置**

添加完后，选中所手机号修改配置，添加维度、经度等配置

- 修改预约项目

  查看`预约项目列表`，商品预约code，用@间隔，例如：`10213@10214`、`10213`

- 修改经纬度、省份、城市

  地址查询：https://restapi.amap.com/v3/geocode/geo?key=0a7f4baae0a5e37e6f90e4dc88e3a10d&output=json&address=清华大学

  返回的key中：province省份、city城市、 "location": "116.326759,40.003304"左边是经度，右边是纬度

- 类型

  1：预约本市出货量最大的门店，2：预约你的位置附近门店

## 贡献代码

若您有好的想法，发现一些 **BUG** 并修复了，欢迎提交 **Pull Request** 参与开源贡献

发起 pull request 请求，提交到 master 分支，等待作者合并



## 演示图

| i茅台预约                                                    |                                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image-20230707144241399](/Users/ningzhiyuan/Java/MyProject/新-campus/oddfar-imaotai/README.assets/image-20230707144241399.png) | ![image-20230707144404638](/Users/ningzhiyuan/Java/MyProject/新-campus/oddfar-imaotai/README.assets/image-20230707144404638.png) |
|                                                              |                                                              |
| ![image-20230707144703842](/Users/ningzhiyuan/Java/MyProject/新-campus/oddfar-imaotai/README.assets/image-20230707144703842.png) | ![image-20230707145525709](/Users/ningzhiyuan/Java/MyProject/新-campus/oddfar-imaotai/README.assets/image-20230707145525709.png) |







## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

特别感谢 [JetBrains](https://www.jetbrains.com/?from=campus) 为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=campus) 等 IDE 的授权  
[<img src=".github/jetbrains-variant.png" width="200"/>](https://www.jetbrains.com/?from=campus)

