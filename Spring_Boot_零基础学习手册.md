# Java Spring Boot 零基础完整学习手册

## ——以校园博客论坛系统为案例

---

```
╔═══════════════════════════════════════════════════════╗
║                                                       ║
║        Java Spring Boot 零基础完整学习手册            ║
║                                                       ║
║        ——从 HelloWorld 到完整后端项目的奇妙之旅       ║
║                                                       ║
║                                                       ║
║         案例项目：校园博客论坛系统 v1.34              ║
║         作者：刘畅                                    ║
║         发布日期：2026 年 4 月                        ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

---

## 关于本书

这是一本**真正面向零基础**的 Spring Boot 学习手册。它不会假设你已经懂 Java，也不会跳过任何环节。我们会一起从最基础的 `Hello World` 开始，一步一步走到能独立看懂、修改、甚至从零搭建一个完整的后端项目。

**本书的特点：**

- ✅ **完全零基础友好**：每个新概念都会用生活类比解释一次
- ✅ **真实项目驱动**：所有讲解都基于真实可运行的"校园博客论坛系统"
- ✅ **代码来源真实**：每一段示例代码都来自项目的真实文件，可以打开 IDE 一一对照
- ✅ **覆盖全栈后端**：从 Java 语法 → Maven → Spring Boot → MyBatis Plus → Spring Security → JWT → 项目部署
- ✅ **超过 5 万字**：足够详细，但不啰嗦

**学完本书你将获得：**

1. 能独立读懂任何 Spring Boot 项目的源码
2. 能用 Spring Boot + MyBatis Plus 从零搭建一个 RESTful 后端
3. 理解用户认证、JWT、密码加密、XSS 防护等安全要点
4. 会处理常见的并发问题（点赞重复、阅读量竞态等）
5. 能独立完成项目打包、部署、上线全流程

---

## 适合人群

- **大学生 / 校招生**：找后端开发的实习或工作
- **转行学编程**：从 0 开始想入门后端开发
- **前端转后端**：会 JavaScript 但想学后端
- **培训班学员**：想要一份系统化的入门资料
- **代码爱好者**：想看懂别人的 GitHub 项目

⚠️ **不适合**：已经精通 Spring Boot、想看深入源码分析的高级开发者。本书定位是"入门到能上手"，不是"源码精读"。

---

## 学习建议

### 1. 边读边敲代码

**这是最重要的建议**。看再多教程不如自己亲手敲一遍。每一段示例代码都请你打开 IDE，亲自敲进去，亲自跑一次。理解永远来自亲手实践。

### 2. 不要跳跃

虽然你可能急切地想看"项目实战"，但请耐心从"Java 入门"开始。每一章都是后面的基础。跳过基础，后面的代码会让你抓狂。

### 3. 善用 IDE 的跳转

读到 `SysUserController` 时，请打开项目的 `SysUserController.java`，按住 `Ctrl` 点击各种类名，跳到定义。这种"探索式阅读"比看书快十倍。

### 4. 做笔记

遇到不理解的概念，不要硬背，先记下来，往后读几章自然就懂了。读完一章再回头看，会有 "原来如此" 的快感。

### 5. 不要害怕报错

报错是程序员最好的老师。看到红色的 `Exception` 不要慌张，而要兴奋——这是你学习的机会。复制错误信息到搜索引擎，99% 的问题都有答案。

### 6. 加入社区

学习后端开发不要孤军奋战。加入一些 Java / Spring Boot 学习群，和同样在学习的人一起讨论。

---

## 案例项目简介

本书使用的案例项目是一个**真实的、可运行的校园博客论坛系统**：

- **GitHub**：https://github.com/Xinghe-0203/Campus_Blog
- **版本**：v1.34（最后更新 2026-04-27）
- **代码量**：127 个 Java 文件
- **数据表**：18 张
- **Controller**：13 个
- **API 接口**：67 个 HTTP 端点

**包含的功能模块：**

| 模块 | 功能 |
|------|------|
| 👤 用户系统 | 注册、登录、JWT 认证、修改密码、用户搜索 |
| 📝 文章系统 | 发布、编辑、删除、详情、列表、高级搜索、草稿自动保存 |
| 💬 互动系统 | 评论（嵌套回复）、点赞、收藏 |
| 👥 社交系统 | 关注、粉丝、关注流 |
| 🔔 通知系统 | 点赞通知、评论通知、关注通知（事件驱动） |
| 🌐 校友圈 | 动态发布、点赞、评论、转发、可见性控制 |
| 📷 媒体上传 | 图片/视频上传，文件大小限制 500MB |
| 🔥 热门趋势 | 热门文章、热门标签 |
| 🚨 举报系统 | 用户举报、管理员处理 |

**为什么选择这个项目作为案例？**

1. **功能完整**：覆盖了真实业务系统的方方面面，不是玩具项目
2. **代码规范**：遵循阿里 Java 开发规范，分层清晰
3. **持续迭代**：从 v1.0 → v1.34 经过 30 次迭代，包含大量真实的 Bug 修复
4. **安全加固**：经过多轮安全审计，体现工业级实践
5. **学完即用**：完全可以二次开发成毕业设计或商业项目

---

## 全书结构

本书分为 **4 大部分共 19 章 + 附录**，循序渐进：

### 🌱 第一部分：入门基础（约 13000 字）

> 从 0 开始的 Java 与 Spring Boot 启蒙

- 写在前面：致零基础读者
- **第 1 章** Java 语言入门
- **第 2 章** 开发环境搭建
- **第 3 章** Maven 与项目结构
- **第 4 章** Spring Boot 第一课

### 🌿 第二部分：框架核心（约 13000 字）

> 理解 Spring Boot 项目"内功心法"

- **第 5 章** MyBatis Plus 持久层框架
- **第 6 章** 分层架构详解
- **第 7 章** 统一响应与异常处理
- **第 8 章** Spring Security 入门
- **第 9 章** JWT 认证实战

### 🌳 第三部分：项目实战（约 16000 字）

> 真实业务场景下的代码与设计

- **第 10 章** 用户模块实战
- **第 11 章** 文章模块实战
- **第 12 章** 互动模块：评论 / 点赞 / 收藏
- **第 13 章** 关注与通知
- **第 14 章** 校友圈与媒体
- **第 15 章** 热门趋势 + 举报系统

### 🌲 第四部分：进阶与部署（约 10000 字）

> 让代码真正"上线为王"

- **第 16 章** 安全加固实战
- **第 17 章** 性能优化技巧
- **第 18 章** 测试与调试
- **第 19 章** 部署上线
- **附录 A** 常见问题 FAQ
- **附录 B** 项目术语表
- **后记** 你的成长之路

---

## 如何下载本项目代码

```bash
# 1. 克隆项目
git clone https://github.com/Xinghe-0203/Campus_Blog.git

# 2. 进入项目目录
cd Campus_Blog

# 3. 复制环境变量模板（重要！）
cp .env.example .env

# 4. 编辑 .env 文件，填入你的数据库密码等

# 5. 构建项目
mvn clean package

# 6. 运行项目
mvn spring-boot:run
```

详细的环境搭建、IDE 配置、数据库初始化步骤，都在第 2 章中详细讲解。

---

## 致谢

感谢你选择这本书。希望读完这本书的你，能从一个完全的零基础小白，蜕变成一个能独立写出 Spring Boot 项目的后端开发者。

如果你在阅读中遇到任何问题，或者发现书中错误，欢迎在 GitHub 项目中提 Issue。

> **学习编程没有捷径，但有最适合你的路径。**
>
> 这本书，就是为零基础的你，定制的那条路径。

让我们开始吧！🚀

---

</content>

# 《Java Spring Boot 零基础完整学习手册》

## 第一部分 入门基础

> 项目案例：校园博客论坛系统（Campus Blog Forum）
> 配套代码：`D:\MyCode\edu_project`
> 适合人群：零基础完全没接触过 Java 和后端开发的小白

---

# 写在前面

嗨，朋友 👋

如果你正在打开这本书，说明你大概率是这样的情况：听说过"Java 后端开发"这个词，可能在大学开过 Java 课但是云里雾里，或者完全没碰过编程，只是因为想做一个属于自己的网站项目而走到这里。无论你是哪一种，恭喜你，你来对地方了。

## 这本书是给谁看的

这本书是写给**完全零基础**的同学的。我假设你：

- 没写过一行 Java 代码
- 不知道什么是 IDE、什么是 Maven
- 没听说过 Spring Boot
- 但是会开电脑、会用浏览器、会复制粘贴 😄

如果你已经是有经验的开发者，这本书的前几章对你来说可能会显得啰嗦，可以快速翻过去，直接从后面 Spring Boot 的部分开始读。

## 学完后你能掌握什么

读完整本书（包括后续部分），并且**亲手把每一个例子敲一遍**之后，你将能够：

1. 独立看懂中等规模的 Spring Boot 项目源码
2. 自己从零搭建一个 Spring Boot 后端工程
3. 编写 RESTful API（也就是手机 App、网页前端调用的"接口"）
4. 操作 MySQL 数据库，写出增删改查功能
5. 实现用户登录、权限控制、JWT 认证
6. 看懂我们这个真实的"校园博客论坛系统"项目，并且能在它的基础上继续添加自己的功能

## 学习路径建议

⚠️ **请务必遵守这条建议：边读边敲代码！**

我知道很多人喜欢把书"看完再说"，看完之后觉得自己懂了，结果一上手就抓瞎。学编程**没有捷径**，你必须把代码亲手敲一遍——不是复制粘贴，是**一个字符一个字符地敲**。

为什么？因为你的手指在敲键盘的时候，会犯各种错误：拼错单词、忘记分号、括号不匹配……这些错误就是你成长的养料。每一个 bug 都会教你一个知识点。

建议节奏：

1️⃣ 每天学 1 ~ 2 章，不要贪多
2️⃣ 每个例子都敲一遍并跑起来
3️⃣ 出错了先自己查（先看报错信息），实在不会再问 AI 或同学
4️⃣ 学完一章用自己的话复述一遍

## 本项目简介

我们的案例项目叫 **校园博客论坛系统（Campus Blog Forum）**。它是一个真实可运行、能部署上线的项目，包含以下功能：

- 用户注册、登录、JWT 认证
- 发帖、评论、嵌套回复
- 点赞、收藏、关注
- 通知系统
- 校友圈（类似微博）
- 媒体上传（图片、视频）
- 热度统计、举报管理
- 等等……

数据库一共 **18 张表**，技术栈是 **Spring Boot 3.0.12 + MyBatis Plus 3.5.5 + Spring Security + JWT + MySQL**，JDK 用 **21+**。是不是听上去很厉害？别怕，等你读完这本书，回过头看会觉得"也就那么回事"。

## 学习心态

最后说一句最重要的话：

> **技术不难，难的是耐心。**

你会遇到环境装不上、Maven 下载不动、Lombok 不生效、数据库连不上、莫名其妙的红线……这些都是**所有程序员的日常**。不要怀疑自己笨，没人天生就会，所有"大佬"都是从看到红色报错就慌张开始的。

放轻松，慢慢来。准备好了？我们开始吧 🚀

---

# 第 1 章 Java 语言入门

在动手搭建项目之前，我们必须先认识 Java 这门语言。这一章我尽量用大白话和生活类比来解释，不会让你看公式和定义。

## 1.1 Java 是什么、能做什么

Java 是一门 **编程语言（Programming Language）**。所谓编程语言，就是人类用来"指挥"电脑做事的一种特殊语言。中文是用来跟人说话的，Java 是用来跟电脑说话的。

🌰 一个生活类比：

- 你想让一个外国朋友帮你买杯奶茶，你得用他能听懂的语言（比如英语）告诉他怎么走、要点什么。
- 你想让电脑帮你处理数据、显示网页，你也得用电脑能听懂的语言（比如 Java）告诉它步骤。

Java 由 Sun 公司在 1995 年发布，现在归 Oracle 所有。它最大的特点是 **"一次编写，到处运行（Write Once, Run Anywhere）"** —— 你写的同一段 Java 代码，可以在 Windows、Mac、Linux、安卓手机上都跑起来。这是怎么做到的？因为 Java 不是直接给电脑硬件看的，它是给一个叫 **JVM（Java Virtual Machine，Java 虚拟机）** 的"翻译官"看的，每个操作系统上都有自己版本的 JVM。

Java 能做什么？非常多：

| 应用场景 | 举例 |
| --- | --- |
| 后端服务器 | 淘宝、京东、12306 后端大量使用 Java |
| 安卓 App | 早期安卓应用主要用 Java 写（现在 Kotlin 也很流行）|
| 大数据 | Hadoop、Spark、Flink 都是 Java/Scala |
| 桌面应用 | IDEA 本身就是 Java 写的 |
| 我们的项目 | 校园博客论坛后端 ✅ |

我们这本书重点讲的是 **后端服务器开发**，也就是给网页和手机 App 提供数据的"幕后英雄"。

## 1.2 第一个 Java 程序 HelloWorld

按照编程界的传统，学任何新语言第一个程序都叫 "Hello World"（你好，世界）。我们也不能免俗：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

跑起来后屏幕会显示：

```
Hello, World!
```

看起来简单，其实里面信息量很大。我们一行一行拆解：

| 代码 | 含义 |
| --- | --- |
| `public class HelloWorld` | 定义一个名为 HelloWorld 的"类"，public 表示"公开的" |
| `{ ... }` | 大括号包起来的部分是这个类的内容 |
| `public static void main(String[] args)` | 一个特殊的方法，叫 main，是程序的"入口" |
| `System.out.println(...)` | 调用系统的"打印"功能，把括号里的内容显示在控制台 |
| `"Hello, World!"` | 一个字符串（一段文字） |
| `;` | 每条语句结束都要加分号，跟英文句号差不多 |

⚠️ **几个零基础最容易忽略的细节：**

1. Java **严格区分大小写**：`Main` 和 `main` 是两个完全不同的东西
2. 文件名必须叫 `HelloWorld.java`，必须和 `public class` 后面的名字**完全一致**
3. 每条语句都必须以 `;` 结束，少一个就会编译失败
4. 大括号 `{}` 必须配对，少一个也会失败

为什么必须有 `main` 方法？因为 JVM 启动时只认它，类似于一栋楼必须有大门口。所有 Java 应用程序都从 `main` 开始执行。`String[] args` 是命令行参数（一般用不到），先不用管。

`System.out.println` 中：
- `System` 是 Java 自带的一个系统工具类
- `out` 是它里面的"输出"对象
- `println` 是"打印一行（print line）"的意思，会自动换行

如果你只想打印不换行，用 `System.out.print` 就行。

## 1.3 变量与数据类型

**变量（Variable）** 是程序里存数据的"盒子"。盒子上贴个标签（变量名），盒子里放东西（值）。

```java
int age = 20;
String name = "刘畅";
boolean isStudent = true;
double price = 19.9;
long bigNumber = 99999999999L;
```

逐行解释：

- `int age = 20;` —— 我准备一个名叫 `age` 的盒子，规定只能放整数（int），里面放了 20。
- `String name = "刘畅";` —— 一个名叫 `name` 的盒子，放字符串，内容是 "刘畅"。
- `boolean isStudent = true;` —— 布尔盒子，只能装 `true` 或 `false`（是或否）。
- `double price = 19.9;` —— 装小数的盒子。
- `long bigNumber = 99999999999L;` —— 装超大整数的盒子，注意末尾的 `L`，告诉编译器这是 long 类型。

Java 的常用基本数据类型一览：

| 类型 | 中文名 | 占用字节 | 范围/示例 |
| --- | --- | --- | --- |
| `byte` | 字节 | 1 | -128 ~ 127 |
| `short` | 短整数 | 2 | -32768 ~ 32767 |
| `int` | 整数 | 4 | 约 ±21 亿 |
| `long` | 长整数 | 8 | 非常大，需加 L |
| `float` | 浮点数 | 4 | 小数，需加 F |
| `double` | 双精度浮点 | 8 | 默认的小数类型 |
| `boolean` | 布尔 | 1 | true/false |
| `char` | 单个字符 | 2 | 'A'、'中' |

⚠️ 注意 `String` 不是基本类型，它是**类（Class）**，所以首字母大写。我们项目里几乎所有的"文字"都用 String。

类比一下：

- `int` 像一个小号文件柜（只能放整数文件夹）
- `String` 像一个大号档案盒（能放各种长度的文字资料）
- 给变量赋值 = 把东西塞进盒子
- 读取变量 = 看盒子里有什么

## 1.4 控制流：if/else、for、while、switch

程序如果只是从上往下执行，能做的事情就太少了。**控制流（Control Flow）** 让程序可以根据条件判断、循环执行。

### 1.4.1 if / else（如果……否则……）

```java
int score = 85;
if (score >= 90) {
    System.out.println("优秀");
} else if (score >= 60) {
    System.out.println("及格");
} else {
    System.out.println("不及格");
}
```

读起来跟中文几乎一样：如果分数 ≥ 90，打印优秀；否则如果 ≥ 60，打印及格；否则打印不及格。

### 1.4.2 for 循环（重复 N 次）

```java
for (int i = 1; i <= 5; i++) {
    System.out.println("第 " + i + " 次打印");
}
```

`for` 三段式：

1. `int i = 1` —— 初始化，从 1 开始
2. `i <= 5` —— 条件，只要满足就继续循环
3. `i++` —— 每轮结束加 1（等同于 `i = i + 1`）

输出：

```
第 1 次打印
第 2 次打印
第 3 次打印
第 4 次打印
第 5 次打印
```

### 1.4.3 while 循环（条件满足就一直循环）

```java
int count = 0;
while (count < 3) {
    System.out.println("当前 count = " + count);
    count++;
}
```

适合 **不知道要循环多少次** 的场景，比如"读到文件结尾就停"。

### 1.4.4 switch（多分支选择）

```java
int day = 3;
switch (day) {
    case 1: System.out.println("周一"); break;
    case 2: System.out.println("周二"); break;
    case 3: System.out.println("周三"); break;
    default: System.out.println("其他");
}
```

`switch` 适合分支很多的情况。⚠️ 别忘了 `break;`，不然会"穿透"到下一个 case。

## 1.5 类和对象（重点！）

这是 Java 的核心概念，也是零基础最难的一关。请反复读这一节。

### 类是模子，对象是铸造出来的零件 🏭

想象一个工厂：

- **类（Class）** 就像一个"铸造模子"。它定义了：要做什么形状、有几个洞、用什么材料。
- **对象（Object）** 就像用模子铸造出来的"实物零件"。一个模子可以铸造无数个零件。

举个例子，我们项目里有个 `SysUser`（系统用户）类，它定义了"用户"长什么样。简化版：

```java
public class SysUser {
    // 字段（属性）：每个用户都有这些信息
    private Long id;
    private String username;
    private String password;
    private Integer age;

    // 方法（行为）：用户能做的事
    public void sayHello() {
        System.out.println("大家好，我是 " + username);
    }

    // Getter / Setter（读写字段）
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
```

代码解读表：

| 行号片段 | 作用 |
| --- | --- |
| `public class SysUser` | 定义一个名为 SysUser 的类（模子） |
| `private Long id;` | 模子里有一个 id 属性，private 表示外部不能直接访问 |
| `private String username;` | 用户名属性 |
| `public void sayHello()` | 一个行为：打招呼 |
| `getUsername()` / `setUsername()` | 暴露给外部读写 username 的入口 |

接着我们用这个模子"铸造"两个对象：

```java
public class Test {
    public static void main(String[] args) {
        // new 关键字 = 用模子铸造一个新对象
        SysUser user1 = new SysUser();
        user1.setUsername("张三");

        SysUser user2 = new SysUser();
        user2.setUsername("李四");

        user1.sayHello();  // 输出：大家好，我是 张三
        user2.sayHello();  // 输出：大家好，我是 李四
    }
}
```

逐行解释：

- `new SysUser()` —— 用 SysUser 这个模子铸造一个新对象
- `user1` 和 `user2` 是两个不同的对象，互相独立
- 改 user1 的名字，不会影响 user2

🎯 **核心要点：**

> 类是"设计图"，对象是"按设计图盖出来的房子"。设计图只有一份，房子可以盖很多座。

我们项目里 `SysUser`、`BlogPost`、`BlogComment` 等所有 entity（实体类）都是这种模式，分别对应数据库的一张表。一行数据 = 一个对象。

### private、public 是什么？

它们叫 **访问修饰符（Access Modifier）**：

| 修饰符 | 含义 |
| --- | --- |
| `public` | 公开，谁都能用 |
| `private` | 私有，只有本类内部能用 |
| `protected` | 保护，本类和子类能用 |
| 不写 | 默认，同一个包内可用 |

通常字段写 `private`，方法写 `public`，这是"封装"的最佳实践。

## 1.6 方法：参数、返回值、重载

**方法（Method）** 就是一段可以重复使用的代码块，类似数学里的函数。

```java
public int add(int a, int b) {
    return a + b;
}
```

拆解：

| 部分 | 含义 |
| --- | --- |
| `public` | 谁都可以调用 |
| `int` | 返回值类型，这里返回一个整数 |
| `add` | 方法名 |
| `(int a, int b)` | 两个参数：a 和 b |
| `return a + b;` | 返回 a + b 的结果 |

调用：

```java
int sum = add(3, 5);  // sum = 8
```

### 没有返回值用 void

```java
public void printHello() {
    System.out.println("Hello");
}
```

`void` 表示"没有东西要返回"，类似"我帮你做这件事，但不给你东西"。

### 方法重载（Overload）

同名方法、参数不同 = 重载。

```java
public int add(int a, int b) { return a + b; }
public double add(double a, double b) { return a + b; }
public int add(int a, int b, int c) { return a + b + c; }
```

编译器会根据你传的参数自动选择最匹配的版本。

## 1.7 集合：List 和 Map

**集合（Collection）** 就是装多个数据的容器。最常用的两种：

### List —— 有序列表（像火车车厢，一节挨一节）

```java
import java.util.ArrayList;
import java.util.List;

List<String> users = new ArrayList<>();
users.add("张三");
users.add("李四");
users.add("王五");

System.out.println(users.get(0));  // 张三
System.out.println(users.size());  // 3
```

`List<String>` 表示"装 String 的列表"。`<String>` 这种语法叫 **泛型（Generics）**，限定列表里只能放 String。

我们项目里查询博客文章列表，就会得到一个 `List<BlogPost>`。

### Map —— 键值对（像字典，查"苹果"得到"apple"）

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Object> userInfo = new HashMap<>();
userInfo.put("username", "张三");
userInfo.put("age", 20);
userInfo.put("isStudent", true);

System.out.println(userInfo.get("username"));  // 张三
```

`Map<String, Object>` 表示"键是 String，值是任意类型"的字典。

后面写接口返回数据时，经常会用 Map 临时组装一些字段。

## 1.8 异常处理 try-catch

程序运行时可能会出错，比如：除以 0、读取一个不存在的文件、网络断开。这些情况叫 **异常（Exception）**。

🌰 类比：你正在烧开水，突然停电了。如果不处理，整个程序会"崩溃退出"。处理它的方式就是 `try-catch`：

```java
try {
    int result = 10 / 0;  // 这行会抛异常
    System.out.println("永远不会执行到这里");
} catch (ArithmeticException e) {
    System.out.println("出错了：" + e.getMessage());
} finally {
    System.out.println("不管出不出错，这里都会执行");
}
```

| 块 | 作用 |
| --- | --- |
| `try` | 把可能出错的代码放在这里 |
| `catch` | 抓住异常，做处理 |
| `finally` | 无论是否异常都会执行，常用于关闭资源 |

我们项目里有个 **全局异常处理器** `GlobalExceptionHandler`，所有 Controller 抛出的异常都会被它统一捕获并返回友好的错误信息。这样我们 Controller 里只管写正常逻辑，出错的事交给它就行。

## 1.9 注解（Annotation）是什么

**注解（Annotation）** 是一种"标签"，放在类、方法、字段头上，告诉编译器或框架"我有特殊用途"。

最常见的内置注解 `@Override`：

```java
@Override
public String toString() {
    return "我是一个用户对象";
}
```

`@Override` 表示"我重写了父类的方法"。如果你拼错了方法名，编译器会报错提醒你。

Spring Boot 大量使用注解，看一眼我们项目的启动类（来自 `EduProjectApplication.java`）：

```java
@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class
})
@Import(SecurityConfig.class)
@EnableScheduling
public class EduProjectApplication {
    ...
}
```

这里就有 `@SpringBootApplication`、`@Import`、`@EnableScheduling` 三个注解，每个都对应一种"功能开关"。第 4 章会详细讲。

🎯 现在你只要记住一句话：**注解 = 框架用的标签，告诉框架"我是个特殊角色"**。

## 1.10 包和导入 import

当代码量多了，几百个 Java 文件挤在一起会乱套，所以 Java 用 **包（Package）** 来分组，类似文件夹。

```java
package com.example.edu_project.entity;
```

这一行说"我这个文件属于 `com.example.edu_project.entity` 包"。对应到磁盘上的目录就是：

```
src/main/java/com/example/edu_project/entity/
```

要使用别的包里的类，得先 `import`：

```java
import java.util.List;
import java.util.ArrayList;
import com.example.edu_project.entity.SysUser;
```

⚠️ 包名习惯用**全小写**，并且采用"反向域名"风格（com.公司.项目.模块），保证全球唯一。我们项目所有代码都在 `com.example.edu_project` 这个根包下。

---

学完第 1 章，你已经掌握了 Java 语言的基本骨架。接下来我们要把"工具"装到电脑上，让代码能跑起来。

---

# 第 2 章 开发环境搭建

工欲善其事，必先利其器。这一章我们一起把开发环境装好。请按顺序操作，不要跳步。

## 2.1 安装 JDK 21

**JDK（Java Development Kit）** 是 Java 开发工具包，包含编译器、JVM 等。我们项目要求 JDK 21（在 `pom.xml` 里看到 `<java.version>21</java.version>`）。

### 1️⃣ 下载

推荐去 Oracle 官网或者 Adoptium 下载：

- Oracle: https://www.oracle.com/java/technologies/downloads/
- Adoptium（推荐，免费开源）: https://adoptium.net/

选择 **JDK 21 LTS** 版本，对应你的操作系统（Windows x64 选 .msi 安装包）。

### 2️⃣ 安装

Windows 下双击 `.msi`，一路下一步即可。默认会装到 `C:\Program Files\Java\jdk-21\`。

### 3️⃣ 配置 JAVA_HOME 环境变量

这一步很关键。Maven、IDEA 都靠它找 JDK。

Windows 11 操作步骤：

1. 右键"此电脑" → 属性 → 高级系统设置 → 环境变量
2. 在"系统变量"里新建：
   - 变量名：`JAVA_HOME`
   - 变量值：`C:\Program Files\Java\jdk-21`（你的实际安装路径）
3. 找到 `Path` 变量，编辑，新增一行：`%JAVA_HOME%\bin`
4. 一路确定保存

### 4️⃣ 验证

打开 **新的** cmd 或 PowerShell（必须新打开，旧窗口的环境变量不会刷新），输入：

```bash
java -version
javac -version
```

应该看到类似：

```
java version "21.0.2" 2024-01-16 LTS
javac 21.0.2
```

⚠️ 如果出现"不是内部或外部命令"，说明 Path 没配对，回去检查。

## 2.2 安装 IntelliJ IDEA

**IDE（Integrated Development Environment，集成开发环境）** 是写代码的"超级记事本"，自带智能提示、自动补全、调试器等。Java 后端开发首选 IntelliJ IDEA。

下载地址：https://www.jetbrains.com/idea/download/

### 社区版 vs 专业版

| 版本 | 价格 | 是否支持 Spring | 推荐 |
| --- | --- | --- | --- |
| Community（社区版） | 免费 | 部分支持，不带 Spring 插件 | 学习初期可用 |
| Ultimate（专业版） | 收费（学生免费） | 完整支持 Spring、数据库面板等 | ✅ 强烈推荐 |

学生可以用 .edu 邮箱去 [JetBrains 学生计划](https://www.jetbrains.com/community/education/) 申请免费的专业版。

### 初次设置

1️⃣ 启动 IDEA，跳过欢迎页面
2️⃣ 选择主题（Darcula 黑色护眼）
3️⃣ 在 Settings → Build → Build Tools → Maven 里检查 Maven 配置（后面再调）
4️⃣ 在 File → Project Structure → Project SDK 里选 JDK 21

## 2.3 安装 Lombok 插件

**Lombok** 是一个神奇的工具，能自动帮你生成 getter / setter / 构造函数等"模板代码"。我们项目大量使用它（`pom.xml` 里能看到 `lombok` 1.18.40 依赖）。

IDEA 在新版本里已经内置 Lombok 插件，无需手动安装。但你需要确保它启用了：

1. File → Settings → Plugins
2. 搜索 "Lombok"，确认状态是 Enabled

## 2.4 启用注解处理器（关键！）

⚠️ **这一步如果忘了，整个项目会爆红，所有 `@Data`、`@Getter` 都不生效！**

操作步骤：

1️⃣ File → Settings
2️⃣ 搜索 "Annotation Processors"
3️⃣ 找到 "Build, Execution, Deployment → Compiler → Annotation Processors"
4️⃣ 勾选 ✅ **Enable annotation processing**
5️⃣ 点 OK 保存

这一步告诉 IDEA："请在编译时运行 Lombok 的注解处理器，自动帮我生成代码。"

## 2.5 安装并配置 Maven

IDEA 自带了 Maven，可以先用着。如果你想自己装：

1. 下载 Maven：https://maven.apache.org/download.cgi
2. 解压到 `D:\apache-maven-3.9.x`
3. 配置环境变量 `MAVEN_HOME` = `D:\apache-maven-3.9.x`
4. 在 Path 里添加 `%MAVEN_HOME%\bin`
5. 验证：`mvn -version`

### 配置国内镜像（极重要）

Maven 默认从国外服务器下载依赖，速度极慢。我们必须配置阿里云镜像。

打开 `C:\Users\你的用户名\.m2\settings.xml`（没有就自己建一个），加入：

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

然后在 IDEA 的 Settings → Maven 里把 "User settings file" 指向这个 `settings.xml`，下载速度会从 KB/s 提升到 MB/s。

## 2.6 安装 MySQL 8 + 创建数据库

我们项目用的是 **MySQL 8.x**。

### 1️⃣ 下载安装

去 https://dev.mysql.com/downloads/installer/ 下 MySQL Installer for Windows，选 Community 版，按引导一步步装。

安装时会让你设 root 密码，**务必记住这个密码**。

### 2️⃣ 验证

打开命令行：

```bash
mysql -u root -p
```

输入密码后能进入 `mysql>` 提示符就说明成功了。

### 3️⃣ 创建项目数据库

从我们的 `.env.example` 文件看到，项目数据库名应该叫 `campus_blog`：

```sql
CREATE DATABASE campus_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'campus_blog'@'%' IDENTIFIED BY 'your_password_here';
GRANT ALL PRIVILEGES ON campus_blog.* TO 'campus_blog'@'%';
FLUSH PRIVILEGES;
```

逐行解释：

| 语句 | 作用 |
| --- | --- |
| `CREATE DATABASE campus_blog ...` | 创建一个名叫 campus_blog 的数据库，字符集 utf8mb4（支持 emoji） |
| `CREATE USER 'campus_blog' ...` | 创建一个专用账号，避免直接用 root |
| `GRANT ALL PRIVILEGES ...` | 把这个数据库的所有权限给这个账号 |
| `FLUSH PRIVILEGES` | 刷新权限使生效 |

## 2.7 安装数据库可视化工具

光用命令行操作 MySQL 太累，我们要装个可视化工具：

| 工具 | 价格 | 推荐 |
| --- | --- | --- |
| Navicat | 收费 | ⭐⭐⭐⭐⭐ |
| DataGrip（JetBrains） | 收费（学生免费） | ⭐⭐⭐⭐⭐ |
| DBeaver | 免费开源 | ⭐⭐⭐⭐ |
| IDEA 自带 Database 面板（专业版） | 内置 | ⭐⭐⭐⭐⭐ 推荐！ |

如果你用 IDEA 专业版，直接用右侧的 "Database" 面板：

1. 点 + → Data Source → MySQL
2. 填 Host、Port (3306)、User、Password、Database (campus_blog)
3. 点 Test Connection
4. 第一次会让你下载 driver，点 Download

成功后你能在面板里看到所有表、字段，能直接写 SQL 查询。

## 2.8 验证整套环境

我们用一个最小项目把流程跑通。打开 IDEA：

1️⃣ New Project → 选 Maven，JDK 选 21
2️⃣ GroupId: `com.test`, ArtifactId: `hello-test`
3️⃣ 创建后等 Maven 下载完依赖
4️⃣ 在 `src/main/java` 下新建 `com.test.HelloApp` 类：

```java
package com.test;

public class HelloApp {
    public static void main(String[] args) {
        System.out.println("环境装好啦！🎉");
    }
}
```

5️⃣ 右键 → Run 'HelloApp.main()'
6️⃣ 看到底部控制台输出 "环境装好啦！🎉" 就成功了

## 2.9 常见环境问题排查

零基础最容易踩的坑：

| 问题 | 原因 | 解决方法 |
| --- | --- | --- |
| `java -version` 找不到命令 | JAVA_HOME 或 Path 没配对 | 重新检查环境变量，**重新打开** cmd |
| IDEA 显示 "Cannot resolve symbol" | Maven 没下载完依赖 | 右键 pom.xml → Maven → Reload Project |
| `@Data` 等 Lombok 注解爆红 | 注解处理器没启用 | 见 2.4 节 |
| Maven 下载特别慢 | 没配阿里云镜像 | 见 2.5 节 |
| 连不上 MySQL | 密码错 / 端口被占 / 防火墙 | 检查 .env 里的密码、端口；用可视化工具先测连通 |
| 项目启动报 "端口已占用" | 8825 端口被别的程序占了 | 在 .env 里改 SERVER_PORT 或杀掉占端口的进程 |
| 启动后说 "找不到 DB_HOST" | 没建 .env 文件 | `cp .env.example .env`，再修改值 |

---

环境装好了，恭喜你迈过最痛苦的一关 💪 接下来我们认识 Maven 和项目结构。

---

# 第 3 章 Maven 与项目结构

## 3.1 Maven 是什么

**Maven** 是一个 **项目构建和依赖管理工具**。这句话翻译成人话：

- **依赖管理**：你想用别人写好的库（比如 Spring Boot、MySQL 驱动），不用自己去网上找 jar 包，只要在 pom.xml 里写一行配置，Maven 自动帮你下载。
- **项目构建**：编译、打包、运行测试，一行命令搞定。

🌰 类比：

| 你认识的 | 类似的工具 |
| --- | --- |
| 安卓开发 | Gradle |
| 前端开发 | npm / yarn / pnpm |
| Python 开发 | pip / poetry |
| Java 开发 | **Maven** / Gradle |

它们都在做同一件事：管理项目用到的"积木块"。

## 3.2 pom.xml 文件结构详解

`pom.xml` 是 Maven 项目的"身份证"，POM = Project Object Model（项目对象模型）。我们看一下我们项目的实际 pom.xml（节选）：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.0.12</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>edu_project</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>edu_project</name>
    <description>校园博客论坛系统 - 后端</description>

    <properties>
        <java.version>21</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        ...
    </properties>

    <dependencies>
        ...
    </dependencies>
</project>
```

骨架解读：

| 标签 | 含义 |
| --- | --- |
| `<modelVersion>` | POM 的版本，固定 4.0.0 |
| `<parent>` | 父工程，继承其依赖管理 |
| `<groupId>` | 组织 ID，类似公司域名反写 |
| `<artifactId>` | 项目 ID（artifact = 工件） |
| `<version>` | 项目版本号 |
| `<properties>` | 属性配置，可被下面引用 |
| `<dependencies>` | 项目依赖列表 |
| `<build>` | 构建相关配置（插件） |

## 3.3 依赖管理：groupId / artifactId / version

每个 Java 库都用三个坐标唯一标识，叫 **GAV（GroupId-ArtifactId-Version）**：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

这三行说："我要 baomidou 公司开发的、名为 mybatis-plus-boot-starter 的、3.5.5 版本的库。"

Maven 拿到这个坐标后，会去仓库（阿里云镜像）找对应的 jar 包，下载到你电脑的 `~/.m2/repository/` 目录里。

🌰 类比：GAV 就像快递地址：

- groupId = 城市
- artifactId = 街道+门牌号
- version = 具体楼层

## 3.4 父工程 spring-boot-starter-parent

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.0.12</version>
</parent>
```

这一段表示我们继承了 Spring Boot 的父工程。继承的好处是：

1. **版本统一**：父工程已经规定好了几百个常用库的版本，我们写依赖时可以**省略 version**，直接用父工程的版本。
2. **配置统一**：编码、JDK 版本、插件版本都已经默认好了。

类比一下：父工程像"一份大礼包套餐"，里面所有东西都已经搭配好。你只要从套餐里"挑你想要的"，不用自己一个个调味。

## 3.5 starter 启动器

Spring Boot 有一个超棒的发明叫 **starter（启动器）**。

看我们项目里的：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

这个 `spring-boot-starter-web` 实际上是个 **"全家桶"**：

- Spring MVC
- Tomcat（内置 Web 服务器）
- Jackson（JSON 处理）
- 几十个常用 Web 依赖

只引一个 starter，就把做 Web 开发的所有东西都备齐了，太爽了。

我们项目用到的主要 starter：

| starter | 作用 |
| --- | --- |
| spring-boot-starter-web | Web 开发（MVC、Tomcat） |
| spring-boot-starter-security | 安全认证 |
| spring-boot-starter-validation | 参数校验 |
| spring-boot-starter-test | 单元测试 |
| mybatis-plus-boot-starter | MyBatis Plus 数据库 ORM |
| knife4j-openapi3-jakarta-spring-boot-starter | API 文档 UI |

## 3.6 标准的 Maven 项目目录结构

```
edu_project/
├── pom.xml                  ← Maven 配置
├── src/
│   ├── main/
│   │   ├── java/            ← Java 源代码
│   │   │   └── com/example/edu_project/
│   │   └── resources/       ← 配置文件、静态资源
│   │       ├── application.yml
│   │       └── mapper/      ← MyBatis XML
│   └── test/
│       └── java/            ← 测试代码
└── target/                  ← 编译输出（自动生成，不要手动改）
```

牢记 3 大区域：

| 路径 | 用途 |
| --- | --- |
| `src/main/java` | 业务代码 |
| `src/main/resources` | 配置文件（yml、xml、静态资源）|
| `src/test/java` | 单元测试代码 |

## 3.7 解读本项目的目录结构

打开 `D:\MyCode\edu_project\src\main\java\com\example\edu_project\` 你会看到：

```
edu_project/
├── controller/   ← 控制器层（接收 HTTP 请求）
├── service/      ← 业务逻辑层
│   └── impl/
├── mapper/       ← 数据库访问层
├── entity/       ← 实体类（对应数据库表）
├── dto/          ← Data Transfer Object（接收前端传来的数据）
├── vo/           ← View Object（返回给前端的数据）
├── config/       ← 配置类
├── common/       ← 公共类（Result、异常等）
├── filter/       ← 过滤器
├── utils/        ← 工具类
└── EduProjectApplication.java  ← 启动入口
```

这是经典的 **分层架构（Layered Architecture）**。请求流向：

```
浏览器 → Controller → Service → Mapper → MySQL
```

各层职责一句话总结：

| 层 | 职责 | 例子 |
| --- | --- | --- |
| Controller | 接 HTTP 请求、返回响应 | 用户访问 /api/posts |
| Service | 写业务逻辑 | "发帖时积分 +5" |
| Mapper | 读写数据库 | SELECT * FROM blog_post |
| Entity | 表结构对应的 Java 类 | BlogPost.java |
| DTO | 前端请求参数 | LoginDTO（用户名+密码）|
| VO | 给前端的数据 | UserVO（不含密码）|

## 3.8 常用 Maven 命令

在项目根目录打开终端，可以用这些命令：

| 命令 | 作用 |
| --- | --- |
| `mvn clean` | 清理 target 目录 |
| `mvn compile` | 编译代码 |
| `mvn test` | 运行单元测试 |
| `mvn package` | 打包成 jar |
| `mvn clean package` | 先清理再打包（最常用）|
| `mvn install` | 打包并放进本地仓库 |
| `mvn spring-boot:run` | 直接启动 Spring Boot 应用 |

我们项目实际上线时通常是：

```bash
mvn clean package -DskipTests
java -jar target/edu_project-0.0.1-SNAPSHOT.jar
```

第一行打成 jar（跳过测试节省时间），第二行用 java 启动。

---

讲完了 Maven，你应该对"项目"有了整体感。最后一章我们正式见识 Spring Boot。

---

# 第 4 章 Spring Boot 第一课

## 4.1 Spring 框架家族介绍

我们经常听到的几个名字，关系是这样的：

```
Spring Framework （地基）
   └─ Spring Boot （脚手架，让搭楼变快）
        └─ Spring Cloud （微服务集群方案）
```

| 名字 | 一句话介绍 |
| --- | --- |
| Spring Framework | 老牌 IoC + AOP 框架，1.0 发布于 2004 年 |
| Spring Boot | 简化 Spring 配置的"懒人套餐"，2014 年发布，现在最常用 |
| Spring Cloud | 在 Spring Boot 基础上，提供分布式系统解决方案 |

我们这本书学的是 **Spring Boot**，因为它已经够用并且最受欢迎。

## 4.2 Spring Boot 的"约定大于配置"理念

老 Spring 时代，要做一个 Web 项目你得写几百行 XML 配置：哪个文件夹放代码、用什么数据库连接池、JSON 怎么序列化……非常痛苦。

Spring Boot 提出了 **"约定大于配置（Convention over Configuration）"**：

> 我们已经帮你想好了 99% 场景的最佳实践，你**啥都不写就能用**；只有特殊需求时再覆盖默认值。

举个例子：你引了 `spring-boot-starter-web`，Spring Boot 自动：

- 启动一个 Tomcat 服务器（端口 8080）
- 配置 JSON 序列化
- 配置静态资源目录
- 配置错误页面

你只要写 Controller 就行了，相比老 Spring 节省了 90% 的配置工作。

## 4.3 启动类 EduProjectApplication 详解

打开 `D:\MyCode\edu_project\src\main\java\com\example\edu_project\EduProjectApplication.java`：

```java
package com.example.edu_project;

import com.example.edu_project.config.DotenvConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.edu_project.config.SecurityConfig;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class
})
@Import(SecurityConfig.class)
@EnableScheduling
public class EduProjectApplication {

    private static final Logger log = LoggerFactory.getLogger(EduProjectApplication.class);

    public static void main(String[] args) {
        DotenvConfig.load();
        log.info("========================================");
        SpringApplication.run(EduProjectApplication.class, args);
        log.info("   校园博客论坛系统启动成功！");
        log.info("   API文档地址：http://localhost:8825/api/doc.html");
        log.info("========================================");
    }
}
```

逐段解读：

### `@SpringBootApplication`

这是 Spring Boot 的**核心注解**，它实际上是 3 个注解的组合：

| 子注解 | 作用 |
| --- | --- |
| `@SpringBootConfiguration` | 标记这是一个配置类 |
| `@EnableAutoConfiguration` | 启用自动配置（最神奇的部分！）|
| `@ComponentScan` | 自动扫描本包及子包下所有标有 @Controller、@Service 等注解的类 |

**自动配置** 的意思是：Spring Boot 会查看你引了哪些 starter，自动帮你配好相关功能。比如发现你引了 mysql-connector-j，就自动准备好数据源配置。

### `exclude = {...}`

排除两个默认的 Spring Security 自动配置类，因为我们项目自定义了 `SecurityConfig`，不想让默认配置干扰。

### `@Import(SecurityConfig.class)`

手动导入我们自己的安全配置类。

### `@EnableScheduling`

启用定时任务。我们项目里有 `JwtSchedulerConfig`，定期清理 JWT 黑名单。

### `main` 方法

```java
DotenvConfig.load();
SpringApplication.run(EduProjectApplication.class, args);
```

第一行：先加载 `.env` 文件里的环境变量（数据库密码、JWT 密钥等），这是项目自己写的工具。

第二行：启动 Spring Boot，Spring 会扫描所有组件、连接数据库、启动 Tomcat。

启动成功后日志会打印 API 文档地址。

## 4.4 application.yml 配置文件解读

`src/main/resources/application.yml` 是 Spring Boot 主配置文件。我们的实际配置（节选）：

```yaml
spring:
  application:
    name: campus-blog-forum

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      pool-name: CampusBlogHikariCP

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0

server:
  port: ${SERVER_PORT:8825}
  servlet:
    context-path: /api

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION}
```

关键点表格：

| 配置 | 含义 |
| --- | --- |
| `spring.application.name` | 应用名称 |
| `spring.datasource.url` | 数据库连接地址 |
| `${DB_HOST}` | 引用环境变量（来自 .env 文件） |
| `${SERVER_PORT:8825}` | 默认端口 8825，可被环境变量覆盖 |
| `server.servlet.context-path: /api` | 所有接口前缀加 /api |
| `mybatis-plus.global-config.db-config.logic-delete-field` | 逻辑删除字段名 |
| `jwt.secret` | JWT 签名密钥 |

⚠️ 注意 YAML 格式：

- 用 **空格** 缩进，绝对不能用 Tab！
- 冒号后面要有 **一个空格**
- 同一层级的字段缩进要一致

### 为什么用环境变量？

直接把数据库密码写在 yml 里推到 GitHub？那等于把家门钥匙挂在大门上！我们用 `${DB_PASSWORD}` 占位符，真实值放在本地 `.env` 文件里（已加进 `.gitignore`）。

`.env.example` 就是模板，让别人 `cp .env.example .env` 后填自己的值：

```bash
DB_HOST=IP
DB_PORT=3306
DB_NAME=campus_blog
DB_USERNAME=campus_blog
DB_PASSWORD=your_database_password_here
JWT_SECRET=your_jwt_secret_key_here_minimum_32_characters
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
SERVER_PORT=8825
```

⚠️ JWT_SECRET 必须 **至少 32 位**，否则 JJWT 会拒绝签名。

## 4.5 第一个 RestController 示例

让我们写一个简单接口感受 Spring Boot 的魅力：

```java
package com.example.edu_project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "! 欢迎来到校园博客论坛 🎉";
    }
}
```

代码解析：

| 注解/语法 | 作用 |
| --- | --- |
| `@RestController` | 标记这是个 REST 控制器，所有方法返回的内容会直接写到 HTTP 响应体（JSON/字符串）|
| `@GetMapping("/hello")` | 把 HTTP GET 请求 `/hello` 映射到这个方法 |
| `@RequestParam` | 从 URL 中取参数，比如 `?name=张三` |
| `defaultValue = "World"` | 没传 name 时默认用 "World" |

启动项目后，浏览器访问：

```
http://localhost:8825/api/hello
http://localhost:8825/api/hello?name=刘畅
```

注意路径是 `/api/hello` 而不是 `/hello`，因为我们在 yml 里设了 `context-path: /api`。

## 4.6 项目运行：mvn spring-boot:run vs java -jar

两种运行方式对比：

| 方式 | 命令 | 适合场景 |
| --- | --- | --- |
| 开发期运行 | `mvn spring-boot:run` 或 IDEA 直接 Run | 边改代码边跑 |
| 打包后运行 | `mvn clean package` → `java -jar target/edu_project-0.0.1-SNAPSHOT.jar` | 部署上线 |

打包后的 jar 文件叫 **fat jar（胖 jar）**，里面已经把 Tomcat 都打进去了，部署到任何装了 Java 21 的机器上都能跑：

```bash
java -jar edu_project-0.0.1-SNAPSHOT.jar
```

✨ 这就是 Spring Boot 最爽的特性：**不需要单独装 Tomcat**！

## 4.7 访问地址 & Knife4j 文档

启动成功后，你能访问以下地址：

| 地址 | 用途 |
| --- | --- |
| http://localhost:8825/api | 接口根地址 |
| http://localhost:8825/api/doc.html | **Knife4j 接口文档（重点！）** |
| http://localhost:8825/api/v3/api-docs | OpenAPI JSON 原始数据 |

### Knife4j 是什么

Knife4j 是基于 Swagger 的增强版 API 文档工具。打开 doc.html 你会看到一个超漂亮的网页：

- 左侧是按模块分组的接口列表（用户、文章、评论…）
- 中间是接口详情：URL、参数、响应
- 右侧可以**直接在线测试接口**，不需要 Postman

⚠️ 默认管理员账号是 `admin` / `admin123`，可以登录后试用所有需要鉴权的接口。

### 项目结构再回顾

至此你应该对项目有完整的画面感了：

```
浏览器访问 http://localhost:8825/api/posts
   ↓
Tomcat 接收请求
   ↓
Spring 路由到 PostController
   ↓
PostController 调 PostService
   ↓
PostService 调 PostMapper
   ↓
MyBatis Plus 生成 SQL，访问 MySQL
   ↓
查到数据 → 一路返回 → 序列化成 JSON
   ↓
前端拿到结果显示
```

这就是一个典型的 Spring Boot RESTful 应用的请求流程。

---

## 第一部分小结

🎉 恭喜你读完了第一部分！让我们回顾一下：

1️⃣ **第 1 章** 学了 Java 语言基础：变量、控制流、类与对象、集合、异常、注解、包
2️⃣ **第 2 章** 装好了开发环境：JDK 21、IDEA、Lombok、Maven、MySQL
3️⃣ **第 3 章** 认识了 Maven 和我们项目的目录结构
4️⃣ **第 4 章** 第一次接触 Spring Boot：启动类、yml 配置、写了一个 hello 接口

到这里你应该能：

- 看懂 Java 基本语法
- 把项目跑起来
- 写出最简单的 GET 接口
- 看懂项目的整体架构

接下来在第二部分，我们会深入 Controller、Service、Mapper 的写法，开始真正动手开发"用户注册登录"这样的实际功能。

请休息一下，喝杯水，做几个深呼吸，然后我们继续 💪

> 记住开发荣耻第一条：**以瞎清接口为耻，以认真查询为荣。**
> 写代码不是在炫技，是在认真解决问题。慢就是快。

下一部分见！

# 《Java Spring Boot 零基础完整学习手册》

## 第二部分 框架核心

> 案例项目：校园博客论坛系统（Campus Blog）
> 适合人群：完全没有 Java Web 经验的初学者
> 写作原则：每个概念都有比喻、每段代码都来自真实项目

---

# 第 5 章 MyBatis Plus 持久层框架

## 5.1 ORM 思想：类↔表，对象↔行

刚开始接触 Java Web 的朋友，最容易卡住的地方就是"数据库 SQL"和"Java 代码"之间的鸿沟。数据库里有一张表，比如 `sys_user`，它有 `id`、`username`、`password` 这些列。Java 程序里有一个类，比如 `SysUser`，它有 `id`、`username`、`password` 这些字段。它们看起来是一回事，但底层完全不同：一个是关系型数据库的二维表，一个是 JVM 内存里的对象。

ORM（Object-Relational Mapping，对象关系映射）就是把这两个世界连起来的"翻译官"。它的核心思想可以用一个比喻来理解：

> 把数据库表想象成一个 Excel 表格。
> 表的"表头"（列名）= Java 类里的"字段名"。
> 表的"每一行"= Java 里的"一个对象"。
> 你在 Java 里 `new SysUser()`，就好比在 Excel 里新增一行；你给对象的字段赋值，就好比往单元格里填数据；最后你保存对象到数据库，就好比把这一行写到 Excel 里。

有了 ORM，你就不用一直手写 `INSERT INTO sys_user (username, password) VALUES (?, ?)` 这种 SQL 了。框架会替你拼 SQL、执行、把结果集映射回 Java 对象。

ORM 的三组对应关系一定要牢记：

| 数据库世界 | Java 世界 |
|------------|-----------|
| 表（table） | 类（class） |
| 行（row） | 对象（object） |
| 列（column） | 字段（field） |

## 5.2 MyBatis 与 MyBatis Plus 的区别

MyBatis 是国内最流行的 ORM 框架之一，特点是"半自动"：你写 SQL，它帮你映射结果。但即便是简单的 CRUD（增删改查），也得写一堆 XML 或注解 SQL，重复且枯燥。

MyBatis Plus（简称 MP）是在 MyBatis 之上做的"增强工具"，特点是"全自动 + 可扩展"：

- 简单的 CRUD（按 ID 查、新增、更新、删除、分页）：完全不用写 SQL，框架自动生成。
- 复杂的业务 SQL：你照样可以用 MyBatis 的 `@Select`、XML 写自定义 SQL，两种方式无缝共存。

形象比喻：MyBatis 像一台手动挡汽车，所有挡位都得自己换；MyBatis Plus 则像自动挡，普通路况自动操作，遇到山路你可以切手动模式。

本项目的 `pom.xml` 里引入的就是 MyBatis Plus 3.5.5，整个项目几乎所有简单 CRUD 都没有写一行 SQL，全靠框架生成。

## 5.3 实体类的关键注解（基于真实代码）

实体类（Entity）是 ORM 里"表"的 Java 化身。我们以本项目的 `SysUser.java` 为例（路径：`src/main/java/com/example/edu_project/entity/SysUser.java`）：

```java
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String nickname;
    private String avatar;
    private String email;
    private String role;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Integer loginFailCount;

    private LocalDateTime lockUntil;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
```

这里出现的注解，是你今后每写一个实体类都会用到的"四大金刚"：

### `@TableName("sys_user")`

告诉 MyBatis Plus："这个 Java 类对应数据库里的 `sys_user` 表"。如果你把表名写成蛇形（小写下划线），类名写成帕斯卡（大写驼峰），MP 也能默认推断（`SysUser` → `sys_user`），但显式写出来更清晰、更安全。

### `@TableId(type = IdType.AUTO)`

声明这个字段是数据库的主键，并指定主键策略：

- `IdType.AUTO`：交给数据库自增（MySQL 的 `AUTO_INCREMENT`）。
- `IdType.ASSIGN_ID`：MP 用雪花算法生成全局唯一 ID（分布式场景）。
- `IdType.INPUT`：用户自己传入。

本项目 `SysUser` 用的是 `AUTO`，简单直接，依赖 MySQL 的自增能力。

### `@TableField`

控制字段的细节行为。最常用的两个用法：

1. **指定列名**：当 Java 字段名和数据库列名不一致时（比如 `userName` 对应 `user_name`），可以写 `@TableField("user_name")`。MP 默认会把驼峰转下划线，所以大部分时候不用写。
2. **自动填充**：`@TableField(fill = FieldFill.INSERT)` 表示插入时框架自动填值；`FieldFill.INSERT_UPDATE` 表示插入和更新时都自动填值。这跟 5.5 节的 `MyMetaObjectHandler` 配合工作。

### `@TableLogic`

逻辑删除标记。本项目里 `isDeleted` 字段加了这个注解后，调用 `mapper.deleteById(1L)` 时，MP 不会真的执行 `DELETE`，而是会执行 `UPDATE sys_user SET is_deleted = 1 WHERE id = 1`。从此以后所有查询会自动加上 `WHERE is_deleted = 0`，被"删除"的数据对业务层面隐形，但实际仍然存在于数据库。

⚠️ 这是企业级开发的标配。物理删除的数据找不回来，逻辑删除随时可以恢复。本项目最近一次安全升级（v1.28）就是把所有物理删除改成了逻辑删除。

### `@JsonIgnore`

虽然这是 Jackson 的注解（不是 MP 的），但很关键。`SysUser` 的 `password` 字段加了 `@JsonIgnore`，意思是"序列化为 JSON 时跳过这个字段"。这样即使你不小心把 `SysUser` 直接返回给前端，密文密码也不会泄露。安全第一。

### `@Data`（Lombok）

Lombok 的 `@Data` 自动生成 Getter、Setter、`toString`、`equals`、`hashCode`。如果没有它，一个有 14 个字段的 `SysUser` 类要写几百行样板代码。

我们再快速看一下 `BlogPost.java`，结构非常类似：

```java
@Data
@TableName("blog_post")
public class BlogPost implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
```

注意到 `viewCount`、`likeCount` 等都是普通字段，对应数据库的 `view_count`、`like_count`。MP 默认会把驼峰转下划线匹配。

## 5.4 BaseMapper 内置方法

写好实体类后，下一步就是 Mapper 接口。看本项目的 `SysUserMapper.java`：

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 自定义 SQL 方法...
}
```

`@Mapper` 告诉 MyBatis 这是一个 Mapper 接口，需要被扫描；`extends BaseMapper<SysUser>` 一句话就让你"白嫖"了所有 CRUD 方法。BaseMapper 里到底有哪些方法？常用的有：

| 方法 | 作用 |
|------|------|
| `insert(T entity)` | 新增一条记录 |
| `deleteById(Serializable id)` | 按主键删除（实际是逻辑删除） |
| `updateById(T entity)` | 按主键更新 |
| `selectById(Serializable id)` | 按主键查询 |
| `selectList(Wrapper<T>)` | 按条件查询多条 |
| `selectOne(Wrapper<T>)` | 按条件查询一条 |
| `selectCount(Wrapper<T>)` | 按条件统计行数 |
| `selectPage(IPage<T>, Wrapper<T>)` | 分页查询 |

⚠️ 看清楚：你只是写了一个继承 `BaseMapper` 的**空接口**，就拥有了上面所有方法！这就是 MP 让人爱不释手的地方。

## 5.5 自动填充机制

新增和更新数据时，每次都要手动 `setCreateTime(LocalDateTime.now())`、`setUpdateTime(LocalDateTime.now())` 吗？太累。MP 提供了"自动填充"机制，本项目里通过 `MyMetaObjectHandler.java` 实现：

```java
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
```

它的工作原理：

1. 任何实体类的字段加上 `@TableField(fill = FieldFill.INSERT)`，新增时框架会触发 `insertFill` 方法。
2. 任何字段加上 `@TableField(fill = FieldFill.INSERT_UPDATE)`，新增和更新时都会触发 `updateFill` 方法。
3. `strictInsertFill` 是"严格模式"，只在字段为 `null` 时才填，已经有值则不覆盖。
4. `LocalDateTime::now` 是 Java 的方法引用，等价于 `() -> LocalDateTime.now()`。

效果就是：你 service 层写 `userService.save(user)` 时根本不用管时间，MP 会自动给 `createTime` 和 `updateTime` 塞值。

## 5.6 逻辑删除：`@TableLogic` + `isDeleted`

前面已经提过，这里再深入一点。本项目所有业务表都有 `isDeleted` 字段（`0=正常，1=已删除`），加了 `@TableLogic`。它的好处：

- 误删可恢复：把 `is_deleted` 改回 `0` 即可。
- 历史可追溯：删除的数据仍在表里，可用于审计。
- 软删 + 关联表保护：避免外键级联删除带来的不可控影响。

`application.yml` 里通常会有全局配置：

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: isDeleted   # 全局逻辑删除字段
      logic-delete-value: 1           # 已删除值
      logic-not-delete-value: 0       # 未删除值
```

这样后续就算不在每个实体类上写 `@TableLogic`，只要字段名叫 `isDeleted`，也会自动生效。

## 5.7 分页插件（基于 `MybatisPlusConfig.java`）

MP 默认没开启分页，需要手动注册一个分页拦截器。本项目的配置类：

```java
@Configuration
@MapperScan("com.example.edu_project.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

两个关键点：

1. `@MapperScan("com.example.edu_project.mapper")`：告诉 Spring 扫描这个包下所有 Mapper 接口，自动生成代理实现。
2. `PaginationInnerInterceptor(DbType.MYSQL)`：为 MySQL 注入分页 SQL 改写能力。

启用后你就能这样写：

```java
Page<SysUser> page = new Page<>(1, 10); // 第 1 页，每页 10 条
IPage<SysUser> result = userMapper.selectPage(page, wrapper);
result.getRecords();    // 当前页数据
result.getTotal();      // 总记录数
result.getPages();      // 总页数
```

底层 SQL 框架会自动给你拼成：`SELECT ... FROM sys_user ... LIMIT 0, 10`，并额外执行 `SELECT count(*) ...` 求总数。

本项目 `SysUserServiceImpl.searchUsers` 方法就是这样用的：

```java
Page<SysUser> page = new Page<>(request.getPage(), request.getPageSize());
LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
    String keyword = request.getKeyword().trim();
    wrapper.and(w -> w.like(SysUser::getUsername, keyword)
            .or()
            .like(SysUser::getNickname, keyword));
}
wrapper.orderByDesc(SysUser::getCreateTime);
IPage<SysUser> userPage = this.page(page, wrapper);
```

## 5.8 LambdaQueryWrapper 条件构造器

`Wrapper` 是 MP 用来构造 WHERE 条件的工具，相当于"用 Java 写 SQL 的 WHERE 部分"。最推荐的版本是 `LambdaQueryWrapper`，因为它用方法引用 `SysUser::getUsername` 代替字符串 `"username"`，编译期能检查字段名拼写错误。

常见 API：

| 方法 | 等价 SQL |
|------|----------|
| `eq(SysUser::getUsername, "刘畅")` | `username = '刘畅'` |
| `ne(...)` | `<>` |
| `gt / ge / lt / le` | `> / >= / < / <=` |
| `like(...)` | `LIKE '%xxx%'` |
| `in(...)` | `IN (...)` |
| `between(...)` | `BETWEEN a AND b` |
| `orderByDesc(...)` | `ORDER BY xxx DESC` |
| `and(w -> ...)` | `AND (...)` 嵌套条件 |

在本项目登录接口里就有典型用法：

```java
LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(SysUser::getUsername, request.getUsername());
SysUser user = this.getOne(wrapper);
```

## 5.9 自定义 SQL：`@Update` 注解或 XML

简单 CRUD 用 BaseMapper 就够了，但有些场景需要"原子操作"，比如：

- 计数器自增：`UPDATE sys_user SET follower_count = follower_count + 1 WHERE id = ?`
- 复杂条件更新

本项目的 `SysUserMapper` 就有大量这样的注解 SQL：

```java
@Update("UPDATE sys_user SET login_fail_count = login_fail_count + 1, " +
        "lock_until = CASE WHEN login_fail_count + 1 >= #{maxFailCount} " +
        "THEN DATE_ADD(NOW(), INTERVAL #{lockMinutes} MINUTE) ELSE lock_until END " +
        "WHERE id = #{userId} AND (lock_until IS NULL OR lock_until <= NOW())")
int incrementLoginFailCount(@Param("userId") Long userId,
                             @Param("maxFailCount") int maxFailCount,
                             @Param("lockMinutes") int lockMinutes);

@Update("UPDATE sys_user SET follower_count = follower_count + 1 WHERE id = #{userId}")
int incrementFollowerCount(@Param("userId") Long userId);
```

`#{xxx}` 是 MyBatis 的占位符，会自动 PreparedStatement 化，防止 SQL 注入。`@Param` 给参数命名以便 SQL 里引用。

⚠️ 这些自增 SQL 在数据库层就保证了原子性，不会出现"100 个人同时点赞，结果计数器只 +1"的并发 Bug。这是**数据库层的并发安全**，比 Java 层的锁更可靠。

### 一句话总结
> MyBatis Plus 把"对象"和"表"自动绑定，简单 CRUD 一行 SQL 不用写，复杂 SQL 又能随时手写，是 Java Web 开发的标配。

---

# 第 6 章 分层架构详解

## 6.1 为什么要分层（饭店比喻）

想象你去饭店吃饭：

- **服务员（Controller）**：接你的菜单订单，告诉厨房要做什么，最后把菜端给你。他不会做菜，只负责沟通。
- **厨师（Service）**：真正炒菜的人。他会根据菜单决定先切菜还是先开火，把"做菜"的复杂逻辑封装起来。
- **采购员（Mapper）**：去仓库取原料、把原料带回厨房。他不关心怎么烹饪，只管"从数据库拿数据"。
- **食材（Entity）**：摆在仓库里的牛肉、青菜，对应数据库表里的一行。

如果让一个人同时干这三件事，会乱套：服务员一边记单一边切菜一边搬蔬菜。所以企业级项目都强制分层，每一层只做自己的事，便于维护、便于测试、便于团队协作。

Spring Boot 项目的标准分层：

```
浏览器 / 前端
    ↓ HTTP
Controller（控制器）  ← 接请求、返响应
    ↓ Java 调用
Service（业务层）     ← 业务规则、事务
    ↓ Java 调用
Mapper（持久层）      ← SQL、与数据库交互
    ↓ JDBC
MySQL
```

## 6.2 Controller 层（基于真实 `SysUserController.java`）

Controller 是"接待大厅"。看看本项目的注册接口长什么样：

```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse response = sysUserService.register(request);
        return Result.success(response);
    }
}
```

逐行解读：

- `@RestController`：等于 `@Controller + @ResponseBody`，意思是这个类的所有方法直接返回 JSON，不走视图模板。
- `@RequestMapping("/user")`：这个类下的所有接口都以 `/user` 开头。
- `@PostMapping("/register")`：完整路径就是 `POST /user/register`。
- `@RequestBody`：把请求体的 JSON 反序列化为 `UserRegisterRequest` 对象。
- `@Valid`：触发参数校验（详见 7.5 节）。
- `@Autowired`：把 `SysUserService` 这个 Bean 自动注入进来（详见 6.6 节）。
- `Result.success(response)`：把业务结果包成统一响应格式（详见第 7 章）。

⚠️ 控制器里不要写业务逻辑，只做三件事：**接参 → 调 service → 返结果**。

## 6.3 Service 层（接口 + 实现类）

Service 层是"厨房"，是业务规则的核心。本项目用 **接口 + 实现类** 的方式分离：

接口 `SysUserService.java`：

```java
public interface SysUserService extends IService<SysUser> {
    UserRegisterResponse register(UserRegisterRequest request);
    UserLoginResponse login(UserLoginRequest request);
    SysUser getUserById(Long id);
    void changePassword(Long userId, String oldPassword, String newPassword);
    IPage<UserVO> searchUsers(UserSearchRequest request);
}
```

实现类 `SysUserServiceImpl.java`：

```java
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterResponse register(UserRegisterRequest request) {
        // ... 一堆业务逻辑
    }
}
```

为什么要"接口 + 实现"？

1. **解耦**：Controller 依赖接口而不是具体实现，将来想换实现（比如对接其他数据源）只需新写一个实现类。
2. **AOP 友好**：Spring 的事务、日志、缓存代理需要接口才能用 JDK 动态代理。
3. **易测试**：写单元测试时可以方便地 Mock 接口。

注意几个关键点：

- `extends ServiceImpl<SysUserMapper, SysUser>`：MP 提供的通用 Service 实现，让你在 service 里直接用 `this.save()`、`this.getById()` 等方法，免去写 `userMapper.insert()` 的麻烦。
- `@Service`：告诉 Spring "我是一个 service Bean，请管理我"。
- `@Transactional(rollbackFor = Exception.class)`：所有写操作都加事务，遇到异常自动回滚，保证数据一致性。
- `@Transactional(readOnly = true)`：只读事务可以让数据库做优化（比如只读副本路由），本项目 `getUserById`、`searchUsers` 都加了。

## 6.4 Mapper 层

Mapper 层是"采购员"，只做一件事：**和数据库说话**。看本项目的 `SysUserMapper`：

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Update("UPDATE sys_user SET login_fail_count = login_fail_count + 1, ... ")
    int incrementLoginFailCount(@Param("userId") Long userId,
                                 @Param("maxFailCount") int maxFailCount,
                                 @Param("lockMinutes") int lockMinutes);
}
```

它就是一个接口，没有方法体。简单 CRUD 由 BaseMapper 提供，复杂 SQL 用注解或 XML。框架启动时会用动态代理生成实现类。

⚠️ Service 层可以调 Mapper，但 Controller 层禁止直接调 Mapper！否则就破坏了分层架构。

## 6.5 Entity / DTO / VO 区别

这三个东西看起来都是"装数据的盒子"，但用途完全不同。本项目里非常清晰：

| 类型 | 全称 | 用途 | 例子 |
|------|------|------|------|
| Entity | 实体类 | 对应数据库表，给 Mapper 用 | `SysUser` |
| DTO | Data Transfer Object | 接收前端请求参数 | `UserRegisterRequest` |
| VO | View Object | 给前端返回的展示对象 | `UserVO`、`UserLoginResponse` |

为什么要分？看一个例子。

数据库表 `sys_user` 有 `password`、`isDeleted`、`lockUntil` 这些敏感/无关字段。

- 注册时，前端只该传 `username`、`password`、`nickname`、`email`，所以本项目定义了 `UserRegisterRequest`：
  ```java
  @Data
  public class UserRegisterRequest {
      @NotBlank(message = "用户名不能为空")
      @Size(min = 3, max = 20)
      private String username;
      @NotBlank @Size(min = 8, max = 50)
      private String password;
      @Size(max = 30)
      private String nickname;
      @Email
      private String email;
  }
  ```
  这就是 **DTO**，只有前端会传的字段。

- 查询用户时，给前端返回的对象不能包含 `password`、`isDeleted`，所以另定义了 `UserVO`，只放可公开的字段（id、username、nickname、avatar 等）。

如果直接把 `SysUser` 实体类返回给前端：
1. 密码会泄露（虽然 `@JsonIgnore` 能挡一下）。
2. 数据库字段一变，前端契约就跟着崩。
3. 没法精细控制权限（比如本项目里"只有本人或管理员能看到 email、role"）。

⚠️ 一条铁律：**Controller 入参用 DTO，出参用 VO，Entity 只在 service / mapper 内部流转**。

## 6.6 IoC / DI 速通

Spring 最核心的概念是 **IoC（控制反转）** 和 **DI（依赖注入）**。听起来玄乎，其实一个比喻就懂：

> 老式做法：你想喝水，自己走到饮水机接水（自己 new 对象）。
> Spring 做法：你举手喊"我要水"，服务员自动送过来（容器自动注入）。

控制反转 = 把"创建对象"的权力从你手里反转给 Spring 容器。
依赖注入 = 容器把你需要的对象塞给你。

本项目里到处可见：

```java
@Service
public class SysUserServiceImpl ... {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
}
```

`@Autowired` 告诉 Spring："请把已经创建好的 `JwtUtils` Bean 塞给我"。Spring 启动时会扫描所有 `@Component / @Service / @Controller / @Repository / @Configuration`，把它们都注册进容器，然后按需注入。

常见 Bean 注解：

| 注解 | 用途 |
|------|------|
| `@Component` | 通用 Bean |
| `@Service` | 业务层 Bean |
| `@Repository` | 持久层 Bean（MP 用 `@Mapper`） |
| `@Controller` / `@RestController` | 控制器 Bean |
| `@Configuration` | 配置类 |
| `@Bean` | 在配置类的方法上声明一个 Bean |

⚠️ 推荐用**构造器注入**而不是字段注入（更利于测试和不可变性），但小型项目用 `@Autowired` 字段注入也很常见。

## 6.7 项目实际调用链：注册接口

最后我们走一遍"注册接口"的完整调用链，把所有层串起来：

```
前端
  ↓ POST /api/user/register  body: {username, password, nickname, email}
SysUserController.register(...)
  ↓ @Valid 触发参数校验
  ↓ 调用
SysUserService.register(request)
  ↓ 实现是
SysUserServiceImpl.register(...)
    1. 校验密码复杂度（至少 8 位、含 3 类字符）
    2. LambdaQueryWrapper 检查 username 是否已存在
    3. LambdaQueryWrapper 检查 email 是否已存在
    4. BCryptPasswordEncoder.encode(password) 加密密码
    5. this.save(user) → 调用 BaseMapper.insert
       ↓
SysUserMapper（BaseMapper）
    INSERT INTO sys_user (...) VALUES (...)
       ↓
MySQL
       ↓ 返回新增 id
回到 service：log.info("用户注册成功")
回到 controller：Result.success(new UserRegisterResponse(id, username))
       ↓ 序列化为 JSON
前端收到：{"code":200,"message":"操作成功","data":{"id":1,"username":"..."}}
```

整条链路非常清晰，每一层职责单一，这就是分层的力量。

### 一句话总结
> Controller 接请求、Service 写业务、Mapper 跑 SQL，Entity / DTO / VO 各司其职，靠 IoC 容器把它们粘起来。

---

# 第 7 章 统一响应与异常处理

## 7.1 为什么要统一响应

设想一下：登录接口返回 `{"token": "xxx"}`，注册接口返回 `{"id": 1, "ok": true}`，查询接口返回 `[{...}, {...}]`。前端每接一个接口都得猜成功失败如何判断、错误信息在哪。混乱无比。

所以企业级项目都规定：**所有接口都返回同一个外层结构**，里面有：

- `code`：业务状态码（200 成功、500 失败、401 未登录、403 无权限...）
- `message`：可读的提示
- `data`：业务数据（可能为 null）
- `timestamp`：时间戳（便于排查）

这样前端只需要写一套全局拦截器：`if (res.code === 200) ...else 弹错误`。本项目里就这么干。

## 7.2 Result 类详解（基于真实 `Result.java`）

```java
@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;
    private long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() { ... }
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    public static <T> Result<T> success(String message, T data) { ... }
    public static <T> Result<T> error() { ... }
    public static <T> Result<T> error(String message) { ... }
    public static <T> Result<T> error(Integer code, String message) { ... }
}
```

几个值得讲的设计点：

1. **泛型 `<T>`**：`Result<UserVO>`、`Result<IPage<BlogPost>>`、`Result<Void>`，让任何业务数据都能装进 `data`，且类型安全。
2. **私有构造方法 + 静态工厂**：避免外部直接 `new Result()`，强制走 `Result.success()` / `Result.error()`，命名更语义化。
3. **timestamp 自动赋值**：构造方法里 `this.timestamp = System.currentTimeMillis()`，不用每次手动设置。
4. **多个重载**：你可以 `success()`、`success(data)`、`success(msg, data)`、`error()`、`error(msg)`、`error(code, msg)`，按需选用。

序列化为 JSON 的样子：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { "id": 1, "username": "liuchang" },
  "timestamp": 1719999999999
}
```

## 7.3 BusinessException（基于真实代码）

业务异常专门用来表达"业务规则不允许"，比如"用户名已存在"、"密码错误"、"无权操作"。本项目的定义：

```java
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

继承 `RuntimeException`（运行时异常）的好处是：方法签名上不用 `throws`，调用方不用强制 try-catch，清爽。

`@Getter` 是 Lombok 注解，自动生成 `getCode()`。`message` 由父类管理，通过 `getMessage()` 取。

使用方式遍布全项目：

```java
if (user == null) {
    throw new BusinessException(401, "用户名或密码错误");
}
if (user.getStatus() == 0) {
    throw new BusinessException(403, "账号已被禁用");
}
if (categories < 3) {
    throw new BusinessException(400, "密码必须包含大小写字母、数字或特殊字符中的至少3种");
}
```

⚠️ **永远不要在 service / controller 里写 `try { ... } catch { return Result.error(...) }` 来处理业务错误**。统一抛 `BusinessException`，让全局异常处理器去转换。

## 7.4 GlobalExceptionHandler（`@RestControllerAdvice`）

异常抛出后被谁接住？答：`GlobalExceptionHandler`。这是一个全局拦截器，专门捕获所有控制器抛出的异常，转成统一的 `Result` 返回。

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：[{}]", e.getClass().getSimpleName());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("参数校验异常：{}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        return Result.error(409, "数据已存在，操作冲突");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "权限不足，拒绝访问");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        return Result.error(401, "认证失败，请先登录");
    }

    @ExceptionHandler(Throwable.class)
    public Result<Void> handleThrowable(Throwable e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error(500, "系统内部错误，请稍后重试");
    }
}
```

关键注解：

- `@RestControllerAdvice`：等于 `@ControllerAdvice + @ResponseBody`，对所有 `@RestController` 生效，且返回 JSON。
- `@ExceptionHandler(XxxException.class)`：声明这个方法处理哪种异常。Spring 会按"最具体优先"的原则匹配。

本项目处理的异常类型很全：

| 异常 | 含义 | 返回 code |
|------|------|-----------|
| `BusinessException` | 业务异常 | 500 / 自定义 |
| `MethodArgumentNotValidException` | `@Valid` 参数校验失败 | 400 |
| `BindException` | 表单绑定校验失败 | 400 |
| `ConstraintViolationException` | JPA 风格的校验失败 | 400 |
| `MissingServletRequestParameterException` | 缺少必填参数 | 400 |
| `HttpMessageNotReadableException` | 请求体格式错误 | 400 |
| `NoHandlerFoundException` | 404 资源不存在 | 404 |
| `DuplicateKeyException` | 数据库唯一约束冲突 | 409 |
| `AccessDeniedException` | Spring Security 鉴权失败 | 403 |
| `AuthenticationException` | Spring Security 认证失败 | 401 |
| `MaxUploadSizeExceededException` | 文件上传超限 | 400 |
| `Throwable`（兜底） | 任何未捕获的异常 | 500 |

⚠️ 注意 `Throwable` 兜底处理时只 `log.error` 而**不把堆栈泄露给前端**，避免暴露内部结构（这是 v1.34 安全增强的细节之一）。

## 7.5 参数校验注解

参数校验是 Web 应用的"第一道防线"。本项目用 Bean Validation（JSR-303）规范：

```java
public class UserRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 50, message = "密码长度必须在8-50个字符之间")
    private String password;

    @Size(max = 30, message = "昵称长度不能超过30个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;
}
```

常用注解：

| 注解 | 含义 |
|------|------|
| `@NotNull` | 不能为 null |
| `@NotEmpty` | 不能为 null 且 size > 0（用于 String / Collection） |
| `@NotBlank` | 字符串不能为 null / 空串 / 全空格 |
| `@Size(min=, max=)` | 长度限制 |
| `@Min` / `@Max` | 数值最小 / 最大 |
| `@Email` | 邮箱格式 |
| `@Pattern(regexp=...)` | 正则匹配 |

要让校验生效，**必须**在 controller 方法的参数前加 `@Valid`：

```java
@PostMapping("/register")
public Result<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
    return Result.success(sysUserService.register(request));
}
```

校验失败会抛 `MethodArgumentNotValidException`，被 `GlobalExceptionHandler` 转成：

```json
{ "code": 400, "message": "用户名长度必须在3-20个字符之间", "data": null }
```

GET 请求的查询参数（如 `UserSearchRequest`）也要加 `@Valid`：

```java
@GetMapping("/search")
public Result<IPage<UserVO>> searchUsers(@Valid UserSearchRequest request) {
    return Result.success(sysUserService.searchUsers(request));
}
```

## 7.6 完整请求处理流程图

我们把 7.1 ~ 7.5 串起来：

```
前端发起 POST /user/register
   ↓
DispatcherServlet（Spring MVC 入口）
   ↓ 路由到 SysUserController.register
   ↓ @RequestBody 解析 JSON → UserRegisterRequest
   ↓ @Valid 触发 Bean Validation
   │     ├── 校验失败 → 抛 MethodArgumentNotValidException
   │     │              → GlobalExceptionHandler 接住
   │     │              → Result.error(400, "...") → 返回 JSON
   │     └── 校验通过 → 进入 service
   ↓
SysUserService.register
   │     ├── 业务规则不通过 → throw new BusinessException(400, "...")
   │     │              → GlobalExceptionHandler 接住
   │     │              → Result.error(400, "...") → 返回 JSON
   │     └── 通过 → return UserRegisterResponse
   ↓
Controller: return Result.success(response)
   ↓
Spring 序列化为 JSON 返回前端
```

无论成功失败，前端总是收到 `{ code, message, data, timestamp }` 这种结构。

### 一句话总结
> `Result` 统一返回格式，`BusinessException` 优雅抛业务错误，`GlobalExceptionHandler` 全局兜底，三件套让接口又干净又一致。

---

# 第 8 章 Spring Security 入门

## 8.1 认证 vs 授权

学 Spring Security 之前，先分清两个词：

- **认证（Authentication）**：你是谁？比如登录就是认证：你提交用户名密码，系统识别"哦，你是用户 ID 1 的刘畅"。
- **授权（Authorization）**：你能做什么？比如普通用户不能访问 `/admin/**`，管理员可以；这就是授权。

二者经常一起出现，但概念上必须分开。Spring Security 是 Java 世界处理这两件事的事实标准。

## 8.2 SecurityConfig 配置类详解（基于真实 `SecurityConfig.java`）

本项目的核心配置在 `SecurityConfig.java`：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins:http://localhost:8080,http://127.0.0.1:8080}")
    private String allowedOrigins;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { ... }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/user/register", "/user/login", "/user/refresh").permitAll()
                .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                .requestMatchers("/admin/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/post/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/comment/post/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/like/check/**", "/collect/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/tag/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/follow/check/**").permitAll()
                .requestMatchers("/follow/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/post/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/post/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/post/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

逐项解读：

### `@EnableWebSecurity`
启用 Spring Security 的 Web 安全支持。

### `csrf(csrf -> csrf.disable())`
关闭 CSRF（跨站请求伪造）防护。**为什么关？** 因为我们用的是 JWT 无状态认证，不依赖 Cookie/Session，CSRF 攻击的载体（自动发送的 Cookie）不存在，所以可以关闭。但如果你用 Session 认证，**绝不能关 CSRF**。

### `cors(...)`
开启跨域支持。本项目通过 `@Value("${cors.allowed-origins:...}")` 从配置读取允许的来源，例如 `http://localhost:8080`。前后端分离开发时（前端跑在 8080、后端跑在 8825），必须正确配置 CORS，否则浏览器会拦截请求。

### `sessionCreationPolicy(STATELESS)`
不创建 HTTP Session。无状态意味着每个请求都得自己带身份凭证（也就是 JWT）。

### `authorizeHttpRequests(...)`
最重要的部分：**URL 鉴权规则**。规则按从上到下顺序匹配，先匹配的先生效：

| 规则 | 含义 |
|------|------|
| `OPTIONS /**` permitAll | 跨域预检请求一律放行 |
| `/user/register, /user/login, /user/refresh` permitAll | 注册、登录、刷新 token 不需要登录 |
| Swagger 相关路径 permitAll | API 文档对公网开放 |
| `/admin/**` hasRole("admin") | 管理员路径必须 admin 角色 |
| `GET /post/**` permitAll | 文章列表/详情匿名可看 |
| `POST/PUT/DELETE /post/**` authenticated | 发布/修改/删除文章必须登录 |
| `anyRequest().authenticated()` | 其他所有请求都要登录 |

### `addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`
把自定义的 JWT 过滤器插到 Spring Security 的过滤链中（详见第 9 章）。位置很关键：放在 `UsernamePasswordAuthenticationFilter` 之前，先用 JWT 完成认证。

## 8.3 BCrypt 密码加密（强度 12）

```java
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

- **BCrypt** 是当前业界推荐的密码哈希算法。它有两个特征：
  1. **加盐（salt）**：每次哈希都加随机盐值，相同密码每次哈希结果不同，防止彩虹表攻击。
  2. **慢哈希**：故意设计得慢，使暴力破解代价极高。
- **强度（cost factor）= 12** 表示进行 2^12 = 4096 轮哈希。值越大越安全但越耗时。本项目选 12，既安全又能在普通服务器上几十毫秒内完成验证。

使用方式：

```java
// 注册时加密
user.setPassword(passwordEncoder.encode(request.getPassword()));

// 登录时校验
if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    throw new BusinessException(401, "用户名或密码错误");
}
```

⚠️ **绝对不能**用 MD5、SHA1、SHA256 直接存密码！它们速度太快，已经被破解到不安全的程度。

## 8.4 CSRF / CORS 简介

### CSRF（Cross-Site Request Forgery，跨站请求伪造）
攻击者诱导你访问一个伪装的网站，该网站偷偷向你已登录的银行系统发请求（利用浏览器自动带 Cookie）。防御靠 CSRF Token。
本项目用 JWT 无状态、不靠 Cookie，所以可以放心 `csrf().disable()`。

### CORS（Cross-Origin Resource Sharing，跨源资源共享）
浏览器的同源策略：脚本不能访问与当前页面"协议+域名+端口"不同的资源。前后端分离时，前端 `http://localhost:8080` 想访问后端 `http://localhost:8825`，端口不同就跨源了。CORS 就是后端通过响应头告诉浏览器："我允许这个来源"。

本项目的 CORS 配置（节选）：

```java
configuration.addAllowedOriginPattern(trimmed); // 允许的来源
configuration.addAllowedHeader("*");
configuration.addAllowedMethod("*");
configuration.setAllowCredentials(true);        // 允许带凭证（Cookie / Authorization）
configuration.setMaxAge(3600L);
```

⚠️ 安全提示：**绝不要 `addAllowedOrigin("*")` + `setAllowCredentials(true)` 同时存在**，那是非常严重的安全漏洞。本项目从环境变量读取明确的白名单。

## 8.5 一次登录请求完整流程

走一遍 `POST /user/login`：

```
1. 前端提交 {"username":"liuchang","password":"Abc12345"}
2. Spring Security 过滤链开始：
   - JwtAuthenticationFilter 检查 Authorization header（没有，跳过）
   - 路径 "/user/login" 匹配 permitAll，不需要认证，放行
3. 进入 SysUserController.login(...)
4. SysUserServiceImpl.login(...)
   - 用 LambdaQueryWrapper 按 username 查 user
   - 检查锁定状态、账号状态
   - passwordEncoder.matches(明文, 数据库密文) 对比
     - 失败 → handleLoginFailAtomic(userId) 原子+1，可能锁定
       throw new BusinessException(401, "用户名或密码错误")
     - 成功 → 重置失败计数，生成 token
5. jwtUtils.generateToken(userId, username, role)  → 普通 token
   jwtUtils.generateRefreshToken(...)              → 刷新 token
6. 返回 UserLoginResponse(id, username, nickname, avatar, token, refreshToken)
7. Result.success(response) → JSON 给前端
8. 前端把 token 存 localStorage，后续请求带在 header：
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 一句话总结
> Spring Security 把"哪些路径需要登录、哪些角色才能访问、密码怎么加密、跨域怎么配置"全打包好，本项目用它 + JWT 实现了无状态的安全防护。

---

# 第 9 章 JWT 认证实战

## 9.1 JWT 是什么

**JWT（JSON Web Token）** 是一种"自包含的、可验证的"令牌格式。它解决了一个问题：**如何在无状态的 HTTP 上识别用户身份**。

传统 Session 方案：服务器存 sessionId → user 的映射，前端的 Cookie 带 sessionId 来。
JWT 方案：服务器签发一个加密字符串（token），里面已经包含了用户 id、角色等信息，前端每次请求带在 `Authorization` 头里。

JWT 的特点：

- **无状态**：服务器不存 session，多实例部署时不用同步。
- **自包含**：token 解码后能直接读到 userId、role。
- **可验证**：服务器用密钥签名，任何篡改会导致签名验证失败。

⚠️ JWT 本身**不加密**，只签名！payload 是 Base64 编码的，谁拿到都能解码，所以**绝不要在 payload 里放密码、银行卡号等敏感信息**。

## 9.2 JWT 三段结构

一个典型的 JWT 字符串长这样：

```
eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoibGl1Y2hhbmciLCJyb2xlIjoidXNlciIsInN1YiI6ImxpdWNoYW5nIn0.5xj6q0...
```

被两个"."分成三段：

| 段 | 名字 | 内容 |
|----|------|------|
| 第 1 段 | Header（头部） | 算法 + 类型，如 `{"alg":"HS256","typ":"JWT"}` |
| 第 2 段 | Payload（载荷） | 业务数据，如 `{"userId":1,"username":"liuchang","role":"user","exp":1719999999}` |
| 第 3 段 | Signature（签名） | 用服务器密钥对前两段做 HMAC-SHA256 签名 |

签名公式：`HMACSHA256(base64(header) + "." + base64(payload), secret)`。

任何人想篡改 payload（比如把 `role` 改成 `admin`），都没办法重新算出正确的签名（因为没有 secret），服务器一验签就失败。

## 9.3 JwtUtils 工具类详解（基于真实代码）

本项目的 `JwtUtils.java` 封装了所有 JWT 操作。挑核心方法讲：

### 配置注入

```java
@Value("${jwt.secret}")
private String secret;

@Value("${jwt.expiration}")
private Long expiration;

@Value("${jwt.refresh-expiration}")
private Long refreshExpiration;
```

从配置文件（最终来自 `.env`）读取密钥和过期时间。**密钥必须至少 32 位**，否则 HmacSHA256 会拒绝。

### 生成 Token

```java
public String generateToken(Long userId, String username, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("username", username);
    claims.put("role", role);
    return createToken(claims, username, expiration);
}

private String createToken(Map<String, Object> claims, String subject, Long expirationMs) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
}
```

`claims` 就是 payload 里的自定义字段；`subject` 是标准字段（一般填用户名）；`issuedAt` 签发时间；`expiration` 过期时间。`signWith(key)` 用 HMAC-SHA256 签名。最后 `.compact()` 拼成字符串。

### 解析与校验

```java
public Claims parseToken(String token) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
}

public boolean isTokenExpired(String token) {
    try {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
        return true;
    } catch (...) { ... }
}

public Long getUserIdFromToken(String token) {
    Claims claims = parseToken(token);
    return claims.get("userId", Long.class);
}
```

`parseSignedClaims` 内部会**自动验签**，签名错就抛异常。所以 `parseToken` 只要不抛异常，就说明 token 是真的、未被篡改。

### 从请求中提取 Token

```java
public String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring("Bearer ".length());
    }
    return null;
}
```

约定：前端把 token 放在 `Authorization: Bearer <token>` 头里。

## 9.4 JwtAuthenticationFilter 拦截器（OncePerRequestFilter）

每个请求都需要识别"你是谁"，这个工作由 `JwtAuthenticationFilter` 完成：

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtUtils.extractTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                try {
                    if (!jwtUtils.isTokenExpired(token) && !jwtUtils.isTokenRevoked(token)) {
                        Long userId = jwtUtils.getUserIdFromToken(token);
                        String username = jwtUtils.getUsernameFromToken(token);
                        String role = jwtUtils.getRoleFromToken(token);

                        UserContext userContext = new UserContext(userId, role);

                        List<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority(role != null ? "ROLE_" + role : "ROLE_USER")
                        );
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userContext, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    logger.warn("JWT Authentication failed: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warn("JWT Authentication failed");
        }
        filterChain.doFilter(request, response);
    }
}
```

要点：

1. 继承 `OncePerRequestFilter`：保证同一个请求只走一次（避免被多次过滤）。
2. 从请求头解析 token → 校验过期 → 校验黑名单 → 解析 userId/role。
3. 把用户身份塞进 `SecurityContextHolder`：之后业务代码任何地方都可以通过 `SecurityUtils.getCurrentUserId()` 拿到当前用户。
4. token 无效不抛异常，只是不设置认证信息，让后续 Spring Security 决定是否拒绝（401）。
5. 必须 `filterChain.doFilter(request, response)` 让请求继续往下走。

⚠️ 一定要先验签再查黑名单，否则攻击者可以用伪造 token 撑爆黑名单。

配套 `SecurityUtils.java` 提供了便捷方法：

```java
public static Long getCurrentUserId() { ... }       // 强制要求登录
public static Long getCurrentUserIdOrNull() { ... } // 可空，匿名访问也能用
public static boolean isCurrentUserAdmin() { ... }
```

业务代码（如 `SysUserController.getById`）就这样用：

```java
Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();
boolean isOwner = currentUserId != null && currentUserId.equals(id);
boolean isAdmin = SecurityUtils.isCurrentUserAdmin();
if (isOwner || isAdmin) {
    userVO.setEmail(user.getEmail());
    userVO.setRole(user.getRole());
}
```

## 9.5 Token 刷新机制（access + refresh）

JWT 有个两难：

- 过期时间长 → 一旦泄露危害大。
- 过期时间短 → 用户频繁要重登录，体验差。

业界通用方案：**双 token**。

- **AccessToken**（短期，比如 2 小时）：每次业务请求带它。
- **RefreshToken**（长期，比如 7 天）：只用于换新的 AccessToken。

本项目登录后同时返回两个 token：

```java
String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername(), user.getRole());
```

刷新接口 `POST /user/refresh` 的逻辑（节选自 `SysUserController.refreshToken`）：

```java
String refreshToken = authHeader.substring(7);

if (!jwtUtils.isRefreshToken(refreshToken)) {
    throw new BusinessException(401, "无效的刷新Token");
}
if (jwtUtils.isTokenExpired(refreshToken) || jwtUtils.isTokenRevoked(refreshToken)) {
    throw new BusinessException(401, "刷新Token已过期或已撤销");
}

Long userId = jwtUtils.getUserIdFromToken(refreshToken);
// 校验用户当前状态
SysUser currentUser = sysUserService.getUserById(userId);
if (currentUser == null || currentUser.getStatus() == 0 || locked) {
    throw new BusinessException(403, "...");
}

// 撤销旧的 refresh token（refresh token rotation）
jwtUtils.revokeToken(refreshToken);

String newToken = jwtUtils.generateToken(userId, username, role);
String newRefreshToken = jwtUtils.generateRefreshToken(userId, username, role);
```

⚠️ **Refresh Token Rotation（刷新令牌轮换）** 是非常重要的安全实践：每次刷新都把旧 refresh token 加入黑名单，新签发一个。这样即便老 refresh token 被偷走，攻击者也只能用一次，且会被合法用户触发的轮换"踢掉"。

## 9.6 Token 黑名单

JWT 一旦签发就无法主动让它失效（除非过期）。但用户主动登出、修改密码、被踢下线时，必须能撤销 token。本项目用"黑名单"实现：

```java
private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

public void revokeToken(String token) {
    if (token == null) return;
    try {
        parseToken(token); // 先验签
        if (!isTokenExpired(token)) {
            tokenBlacklist.add(token);
        }
    } catch (Exception e) {
        // 无效 token 不加入
    }
}

public boolean isTokenRevoked(String token) {
    return tokenBlacklist.contains(token);
}

public void cleanExpiredTokens() {
    tokenBlacklist.removeIf(token -> {
        try { return isTokenExpired(token); }
        catch (Exception e) { return true; }
    });
}
```

注意：

1. `ConcurrentHashMap.newKeySet()` 提供线程安全的 Set。
2. `revokeToken` **必须先验签**：避免攻击者用伪造 token 撑爆内存。
3. `cleanExpiredTokens()` 由 `JwtSchedulerConfig` 定时调度，避免黑名单无限膨胀。
4. ⚠️ 当前是内存方案，只支持单实例。生产环境多实例部署时，应换成 **Redis SET + TTL**，本项目源码注释里也明确写了这点 TODO。

## 9.7 安全注意事项（务必记住）

最后总结一下 JWT 实战必须遵守的安全准则：

1. **密钥（secret）至少 32 位且必须保密**。本项目通过 `.env` 文件管理，`.gitignore` 忽略，**永远不要提交到 Git**。
2. **必须校验签名**：自动由 `parseSignedClaims` 完成，不要自己写"只 base64 解码不验签"的代码。
3. **payload 不能存敏感信息**：密码、银行卡、身份证等绝对不放。
4. **设置合理的过期时间**：access 1~2 小时、refresh 7~30 天。
5. **必须有撤销机制**：黑名单 / Redis / 数据库 token 表。
6. **用 HTTPS 传输**：HTTP 下 token 在网络上裸奔，会被中间人截获。
7. **拒绝弱算法**：不要用 `alg: none`、不要用 RS256 时把公钥当对称密钥（历史上著名漏洞）。
8. **登录失败要锁账号**：本项目实现了 5 次失败锁 15 分钟，且用数据库原子 SQL 防并发绕过。
9. **修改密码、登出后撤销旧 token**：避免泄露的 token 继续可用。
10. **生产环境用 Redis 存黑名单**：内存方案多实例不一致。

### 一句话总结
> JWT 是"带签名的身份证"，本项目用 access + refresh 双 token、轮换、黑名单、登录锁、HTTPS、CORS 白名单等组合拳，把无状态认证做得既好用又安全。

---

# 第二部分小结

到这里，框架核心的四大支柱你都已经掌握：

1. **MyBatis Plus**：用 `@TableName / @TableId / @TableLogic / @TableField` 把类映射到表，BaseMapper 白嫖 CRUD，分页、自动填充、逻辑删除一键开启。
2. **分层架构**：Controller / Service / Mapper / Entity 各司其职，DTO 接前端、VO 给前端，IoC 容器把它们串起来。
3. **统一响应与异常**：`Result` 包返回值、`BusinessException` 抛业务错、`GlobalExceptionHandler` 全局兜底，前后端契约清晰。
4. **Spring Security + JWT**：BCrypt 加密密码、过滤器解析 token、双 token 刷新、黑名单撤销、CORS 白名单、登录锁定，构成一套企业级的安全防线。

接下来的第三部分将进入"业务实战"：从一个真实的"发布文章 / 点赞 / 评论 / 关注 / 通知 / 圈子"功能，把前面学的所有知识揉在一起，做出可运行的成品。

---

> 全章节配套真实代码均位于本项目仓库：
> - GitHub：https://github.com/Xinghe-0203/Campus_Blog
> - 关键文件：`Result.java`、`BusinessException.java`、`GlobalExceptionHandler.java`、`MybatisPlusConfig.java`、`MyMetaObjectHandler.java`、`SecurityConfig.java`、`JwtAuthenticationFilter.java`、`JwtUtils.java`、`SecurityUtils.java`、`SysUser.java`、`BlogPost.java`、`UserRegisterRequest.java`、`SysUserMapper.java`、`SysUserService.java`、`SysUserServiceImpl.java`、`SysUserController.java`。
# 《Java Spring Boot 零基础完整学习手册》

## 第三部分 项目实战：一行行读懂校园博客论坛

> 本部分基于真实项目 `edu_project`（v1.34）的源码，按照"需求 → 表 → DTO → Service → Controller"的顺序，把六大业务模块拆给零基础读者看。
> 阅读本部分前，请确保已经掌握第二部分中关于 Spring Boot、MyBatis Plus、Spring Security 的入门知识。

---

# 第 10 章 用户模块实战

用户模块是整个论坛的入口。所有"我"才能做的事——发文章、点赞、关注、收藏——都依赖一件事：**系统知道当前请求是谁发的**。本章我们就一行行读懂它。

## 10.1 用户表 sys_user 字段解读

校园博客的用户信息全部保存在 `sys_user` 表中。让我们先看看这张表都有哪些字段：

| 字段名 | 类型 | 含义 | 设计要点 |
| --- | --- | --- | --- |
| `id` | BIGINT | 主键，自增 | 全局唯一身份标识 |
| `username` | VARCHAR(50) | 登录用户名 | **唯一索引**，禁止重复 |
| `password` | VARCHAR(100) | 加密后的密码 | BCrypt 摘要，永不明文存储 |
| `nickname` | VARCHAR(50) | 昵称（公开展示） | 没填就用 username 兜底 |
| `email` | VARCHAR(100) | 邮箱 | **唯一索引**，可空 |
| `avatar` | VARCHAR(255) | 头像 URL | 默认空，前端可显示占位图 |
| `role` | VARCHAR(20) | 角色：`user` / `admin` | 用于 Spring Security 权限判断 |
| `status` | TINYINT | 账号状态：1 正常，0 禁用 | 管理员可一键封禁 |
| `login_fail_count` | INT | 连续登录失败次数 | 防爆破核心 |
| `lock_until` | DATETIME | 锁定截止时间 | 失败超 5 次锁 15 分钟 |
| `follower_count` | INT | 粉丝数 | 冗余字段，避免每次 COUNT |
| `following_count` | INT | 关注数 | 同上 |
| `create_time` / `update_time` | DATETIME | 创建/更新时间 | MyBatis Plus 自动填充 |
| `is_deleted` | TINYINT | 逻辑删除标记 | 1 删除，0 正常 |

💡 **设计决策**：把"粉丝数"这种统计值落地存储而不是每次 `SELECT COUNT(*)`。这叫"冗余字段"。代价是每次关注/取关需要做一次 `UPDATE`，但相对每次刷新主页都执行 COUNT 来说，性价比极高。

⚠️ **易错点**：`is_deleted` 字段不能直接 `WHERE is_deleted = 0`，MyBatis Plus 的 `@TableLogic` 注解会自动加这个条件，手写反而容易写双层。

## 10.2 SysUser 实体类（@TableLogic、@JsonIgnore 防密码泄露）

实体类是 Java 世界对数据库行的镜像：

```java
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    @JsonIgnore   // ⚠️ 关键：序列化为 JSON 时忽略密码字段
    private String password;

    private String nickname;
    private String email;
    private String avatar;
    private String role;
    private Integer status;

    private Integer loginFailCount;
    private LocalDateTime lockUntil;

    private Integer followerCount;
    private Integer followingCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic   // 逻辑删除标记
    private Integer isDeleted;
}
```

逐行解读：

- `@TableName("sys_user")`：告诉 MyBatis Plus，这个 Java 类对应数据库的 `sys_user` 表。
- `@TableId(type = IdType.AUTO)`：主键由数据库自增生成，新增时不要自己写 id。
- `@JsonIgnore`：Spring MVC 把对象序列化成 JSON 返回前端时，跳过该字段——**这是防止密码泄露给前端最简单也最有效的一道闸门**。
- `@TableField(fill = ...)`：配合 `MyMetaObjectHandler`，自动填充时间。INSERT 时填 `createTime`，UPDATE 时同时填 `updateTime`。
- `@TableLogic`：表示这是逻辑删除字段。调用 `removeById` 时不会真的执行 DELETE，而是 UPDATE 把 `is_deleted` 改为 1。

⚠️ **新手最爱犯的错**：直接把 `SysUser` 当响应返回。即便有 `@JsonIgnore`，未来某次需求改动忘了打这个注解，密码就裸奔到前端了。**所以项目里专门有一个 `UserVO`（视图对象）类用来返回**，从源头杜绝信息外泄。

## 10.3 注册接口：DTO 校验 → 唯一性检查 → BCrypt → 用户枚举防护

我们从 `SysUserController#register` 看起：

```java
@PostMapping("/register")
public Result<UserRegisterResponse> register(
        @Valid @RequestBody UserRegisterRequest request) {
    UserRegisterResponse response = sysUserService.register(request);
    return Result.success(response);
}
```

- `@RequestBody`：把请求体里的 JSON 反序列化成 Java 对象。
- `@Valid`：触发 Bean Validation。校验不通过会被 `GlobalExceptionHandler` 捕获并返回 400。

DTO（请求对象）类似：

```java
@Data
public class UserRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 50)
    private String password;

    @Size(max = 50)
    private String nickname;

    @Email
    private String email;
}
```

进入 Service 层就开始做"硬规则"：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public UserRegisterResponse register(UserRegisterRequest request) {
    // 1. 密码强度校验：至少 8 位 + 包含大小写/数字/特殊字符任意 3 类
    String password = request.getPassword();
    int categories = 0;
    if (password.matches(".*[A-Z].*")) categories++;
    if (password.matches(".*[a-z].*")) categories++;
    if (password.matches(".*\\d.*")) categories++;
    if (password.matches(".*[!@#$%^&*()_+...].*")) categories++;
    if (categories < 3) {
        throw new BusinessException(400,
            "密码必须包含大小写字母、数字或特殊字符中的至少3种");
    }

    // 2. 用户名唯一性 ——— 注意错误信息！
    if (this.count(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, request.getUsername())) > 0) {
        throw new BusinessException(400, "注册失败，请稍后重试");
    }

    // 3. 邮箱唯一性
    if (request.getEmail() != null && !request.getEmail().isEmpty()) {
        if (this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, request.getEmail())) > 0) {
            throw new BusinessException(400, "注册失败，请稍后重试");
        }
    }

    // 4. 创建用户对象
    SysUser user = new SysUser();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt
    user.setNickname(request.getNickname() != null
            ? request.getNickname() : request.getUsername());
    user.setEmail(request.getEmail());
    user.setRole("user");
    user.setStatus(1);
    user.setLoginFailCount(0);

    // 5. 入库（捕获并发场景下的唯一约束冲突）
    try {
        this.save(user);
    } catch (DuplicateKeyException e) {
        throw new BusinessException(400, "注册失败，请稍后重试");
    }

    return new UserRegisterResponse(user.getId(), user.getUsername());
}
```

关键点逐一展开：

💡 **为什么错误信息如此模糊？**
注意第 2/3 步的错误都是"注册失败，请稍后重试"，**不是**"用户名已存在""邮箱已注册"。这叫**用户枚举防护（User Enumeration Protection）**：如果系统老老实实告诉攻击者"邮箱已注册"，攻击者就能批量猜出哪些邮箱在你站点上有账号，再去对应的邮件账号尝试爆破。

💡 **为什么先 count 检查，后又要 try DuplicateKeyException？**
两道防线缺一不可：
1. 单线程时 count 检查就够了，但**两个请求同时注册同一个用户名时**，它们都能通过 count 检查，然后两个都往数据库插。
2. 数据库的唯一索引是终极防线，必然抛 `DuplicateKeyException`。我们捕获并返回友好提示。

💡 **BCrypt 的强度 12 是什么？**
`BCryptPasswordEncoder` 默认 strength 是 10，本项目设为 12。每加 1 计算时间翻倍——即便数据库被拖库，攻击者每尝试一个密码也要 200 多毫秒，1 亿次组合需要约 240 天，足以让用户在察觉后改密码。

⚠️ **`@Transactional(rollbackFor = Exception.class)`** 必不可少。Spring 默认只对 `RuntimeException` 回滚，但保险起见把所有 `Exception` 都纳入回滚条件，避免某些 Checked Exception 让"半成品账号"留在表里。

## 10.4 登录接口：账户锁定检查 → 密码验证 → JWT + refreshToken 返回

登录是一段非常微妙的代码：你要把"用户体验好"和"防爆破"两个需求拧在一起。

```java
@Override
@Transactional(rollbackFor = Exception.class)
public UserLoginResponse login(UserLoginRequest request) {
    // 1. 取出用户
    SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, request.getUsername()));
    if (user == null) {
        throw new BusinessException(401, "用户名或密码错误");
    }

    // 2. 锁定检查（先于密码校验！）
    if (user.getLockUntil() != null
            && user.getLockUntil().isAfter(LocalDateTime.now())) {
        throw new BusinessException(403, "登录失败次数过多，请稍后再试");
    }

    // 3. 状态检查
    if (user.getStatus() == 0) {
        throw new BusinessException(403, "账号已被禁用");
    }

    // 4. 密码校验
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        // 4.1 失败时原子地累加失败次数
        handleLoginFailAtomic(user.getId());

        // 4.2 重新查询拿到最新锁定状态
        SysUser updated = this.getById(user.getId());
        if (updated.getLockUntil() != null
                && updated.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(403, "登录失败次数过多，请稍后再试");
        }
        throw new BusinessException(401, "用户名或密码错误");
    }

    // 5. 登录成功，重置计数
    if (user.getLoginFailCount() == null || user.getLoginFailCount() > 0) {
        user.setLoginFailCount(0);
        user.setLockUntil(null);
        this.updateById(user);
    }

    // 6. 生成双 Token
    String token = jwtUtils.generateToken(
            user.getId(), user.getUsername(), user.getRole());
    String refreshToken = jwtUtils.generateRefreshToken(
            user.getId(), user.getUsername(), user.getRole());

    return new UserLoginResponse(user.getId(), user.getUsername(),
            user.getNickname(), user.getAvatar(), token, refreshToken);
}
```

逐项拆解：

💡 **顺序很关键**：先看锁，再看密码。
如果先校验密码、再判断是否锁定，攻击者每次仍然会消耗一次 `passwordEncoder.matches`（约 250ms），但更糟的是会让"已锁定但仍可继续提交密码"——锁定就形同虚设。本项目顺序保证：账号一旦锁定，后续请求会被快速拦截（无 BCrypt 计算），保护数据库压力。

💡 **错误信息"用户名或密码错误"**——同样是用户枚举防护：不告诉攻击者究竟是用户名错还是密码错。

💡 **refreshToken 是什么？**
- `token`（accessToken）：1 小时有效期，每次请求带上。
- `refreshToken`：7 天有效期，**只**给 `/user/refresh` 端点用。当 accessToken 过期时，前端拿 refreshToken 去换一对新的 token。这样既能控制 accessToken 短生命周期（被盗也很快失效），又不需要让用户每小时重新登录。

## 10.5 登录失败锁定（5 次失败锁 15 分钟）

`handleLoginFailAtomic` 调用了 mapper 中的一段自定义 SQL：

```xml
<update id="incrementLoginFailCount">
    UPDATE sys_user
    SET login_fail_count = login_fail_count + 1,
        lock_until = CASE
            WHEN login_fail_count + 1 >= #{maxFailCount}
            THEN DATE_ADD(NOW(), INTERVAL #{lockMinutes} MINUTE)
            ELSE lock_until
        END
    WHERE id = #{userId}
</update>
```

为什么用一条 SQL 完成所有事？

⚠️ **错误示范**：
```java
SysUser u = userMapper.selectById(id);
u.setLoginFailCount(u.getLoginFailCount() + 1);
if (u.getLoginFailCount() >= 5) {
    u.setLockUntil(LocalDateTime.now().plusMinutes(15));
}
userMapper.updateById(u);
```

这段代码看似没问题，但在并发下：两个失败请求同时读到 `loginFailCount = 4`，都加 1 写回 5，最终值仍是 5——计数偏少了 1。我们用一条 SQL 让数据库自己 + 1 + 判断，一原子搞定，杜绝竞态。

💡 **细节**：被锁定后，`lockUntil > now()` 就直接拦截，连密码校验都不做——这就是前面强调"锁定检查先于密码校验"的原因。

## 10.6 修改密码 + 用户搜索

修改密码和注册流程几乎对称，多了一步"验证旧密码"：

```java
public void changePassword(Long userId, String oldPassword, String newPassword) {
    SysUser user = this.getById(userId);
    if (user == null) throw new BusinessException(404, "用户不存在");

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        throw new BusinessException(400, "旧密码不正确");
    }

    // 复杂度校验同注册（略）

    user.setPassword(passwordEncoder.encode(newPassword));
    this.updateById(user);
}
```

⚠️ 修改密码后理论上应该把已签发的 token 加入黑名单，强制其它设备重新登录。本项目的 JWT 黑名单机制可以做到这点（在第二部分讲过），生产中务必启用。

用户搜索就是一个简单的分页 + 模糊查询：

```java
public IPage<UserVO> searchUsers(UserSearchRequest request) {
    Page<SysUser> page = new Page<>(request.getPage(), request.getPageSize());
    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(request.getKeyword())) {
        String kw = request.getKeyword().trim();
        wrapper.and(w -> w.like(SysUser::getUsername, kw)
                .or().like(SysUser::getNickname, kw));
    }
    wrapper.orderByDesc(SysUser::getCreateTime);

    return this.page(page, wrapper).convert(this::convertToUserVO);
}
```

💡 这里 `convertToUserVO` 会清掉密码字段，只留下展示用的属性，再次贯彻"永远不返回 SysUser"的纪律。

---

# 第 11 章 文章模块实战

## 11.1 blog_post 表结构

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| user_id | BIGINT | 作者ID（FK） |
| title | VARCHAR(200) | 标题 |
| summary | VARCHAR(500) | 摘要（手动或自动截取） |
| content | TEXT | 正文（最长 50000 字符） |
| category | VARCHAR(50) | 分类（默认"默认分类"） |
| view_count | INT | 阅读数 |
| like_count | INT | 点赞数 |
| comment_count | INT | 评论数 |
| collect_count | INT | 收藏数 |
| status | TINYINT | 1 已发布 / 0 草稿 / 2 下架 |
| create_time / update_time | DATETIME | 时间戳 |
| is_deleted | TINYINT | 逻辑删除 |

💡 四个 `count` 字段都是冗余字段。原则同 `sys_user` 的粉丝数：写入次数远小于读取次数，把统计落地能换来巨大性能优势。

⚠️ 这些 count 不能用 Java 层代码 `++`、`--`，**必须**通过 `UPDATE blog_post SET like_count = like_count + 1 WHERE id = ?` 由数据库做加法，避免并发丢失更新。

## 11.2 发布文章（DTO + 标签关联 + XSS 过滤）

`BlogPostController#createPost`：

```java
@PostMapping
public Result<Long> createPost(@Valid @RequestBody PostCreateRequest request) {
    Long userId = SecurityUtils.getCurrentUserIdOrNull();
    if (userId == null) throw new BusinessException(401, "请先登录");
    Long postId = blogPostService.createPost(request, userId);
    return Result.success(postId);
}
```

DTO `PostCreateRequest` 包含：title、summary、content、category、tagIds（List<Long>）。

Service 内部最关键的两步：

1. **XSS 过滤**：

```java
String sanitizedTitle   = htmlSanitizer.sanitizeRichText(request.getTitle());
String sanitizedSummary = htmlSanitizer.sanitizeRichText(request.getSummary());
String sanitizedContent = htmlSanitizer.sanitizeRichText(request.getContent());
```

`HtmlSanitizer` 内部用 Jsoup 的白名单：富文本允许 `<p>`/`<strong>`/`<a>` 等安全标签，剥离 `<script>`/`onerror=` 等危险属性。category 用更严格的 `sanitizePlainText` 完全剥离 HTML。

2. **标签关联**：

```java
this.save(post);   // 先保存文章拿到 id
if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
    savePostTags(post.getId(), request.getTagIds());
}
```

`blog_post_tag` 是中间表，主键 (postId, tagId)。`savePostTags` 内部循环 insert。

⚠️ **必须先保存文章拿到自增 id**，再写中间表。新手常犯的错：在 save 之前就拿 id 用，结果 id 还是 null。

💡 **校验标签 ID 有效性**：项目调用 `validateTagIds` 检查传入的 tagId 都真实存在，避免脏数据。

## 11.3 文章列表（分页 + 多条件 LambdaQueryWrapper）

```java
public IPage<PostListResponse> getPostList(PostQueryRequest request) {
    Page<BlogPost> page = new Page<>(request.getPage(), request.getPageSize());

    LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(BlogPost::getStatus, 1)         // 已发布
           .ne(BlogPost::getIsDeleted, 1);     // 排除已删除

    if (StringUtils.hasText(request.getKeyword())) {
        wrapper.and(w -> w.like(BlogPost::getTitle, request.getKeyword())
                .or().like(BlogPost::getContent, request.getKeyword()));
    }
    if (StringUtils.hasText(request.getCategory())) {
        wrapper.eq(BlogPost::getCategory, request.getCategory());
    }
    if (request.getUserId() != null) {
        wrapper.eq(BlogPost::getUserId, request.getUserId());
    }
    if (request.getTagId() != null) {
        // 通过中间表反查
        List<Long> ids = blogPostTagMapper.selectList(...).stream()
                .map(BlogPostTag::getPostId).toList();
        if (ids.isEmpty()) return new Page<>(...0...);
        wrapper.in(BlogPost::getId, ids);
    }
    wrapper.orderByDesc(BlogPost::getCreateTime);

    return this.page(page, wrapper).convert(this::toListVO);
}
```

💡 **`LambdaQueryWrapper` 的 `.and(w -> ...)`**：括号里的 `or()` 不会逃出去影响外层条件——这一点新手很容易踩坑。比如不写 `.and(...)` 包裹 OR 子句，就会变成 `WHERE status=1 AND title LIKE 'x' OR content LIKE 'x'`，关键字段全部被 OR "短路"过滤。

⚠️ 标签筛选这里有 **N+1 隐患**：先查中间表，再 `IN` 主表。如果某标签下文章过多，可能产生上千个 ID 的 IN 子句，未来需要换成 JOIN SQL 优化。

## 11.4 文章详情 + 阅读量防刷

详情接口很普通——根据 ID 查表，拼装作者信息和标签。**重头戏是阅读量防刷**。

`incrementViewCount` 在 Controller 层先做了一道过滤：

```java
@PutMapping("/{id}/view")
public Result<Void> incrementViewCount(@PathVariable Long id, HttpServletRequest req) {
    String userKey = getUserIdentifier(req) + "-" + id;
    long now = System.currentTimeMillis();
    AtomicLong lastViewTime = viewCountCache.computeIfAbsent(userKey, k -> new AtomicLong(0));

    while (true) {
        long lastTime = lastViewTime.get();
        if (now - lastTime < VIEW_COUNT_INTERVAL_MS) {
            return Result.success(null);   // 1 分钟内不重复计数
        }
        if (lastViewTime.compareAndSet(lastTime, now)) break;  // CAS 成功
        // 失败，重读 lastTime 再次循环
    }
    blogPostService.incrementViewCount(id);
    return Result.success(null);
}
```

逐步解释：

- **用户标识 `getUserIdentifier`**：登录用户用 `user-{id}`；未登录用 `guest-{IP}-{UA hash}`，这就是常说的"指纹"。比单纯 IP 更难伪造，但比强账号体系成本更低。
- **缓存使用 LRU**：`new LinkedHashMap<>(16, 0.75f, true)` + `removeEldestEntry` 实现简单 LRU，最多 10000 条，避免内存泄漏。
- **CAS（Compare-And-Set）**：`compareAndSet(lastTime, now)` 在并发场景下保证只有一个线程能成功更新时间戳，其它线程读到新值后会自动判断"在 1 分钟内"而 return。这就解决了所谓的 **TOCTOU**（Time-Of-Check-To-Time-Of-Use）竞态。

💡 真正的 incrementViewCount 落表也是一条原子 SQL：`UPDATE blog_post SET view_count = view_count + 1 WHERE id = ?`。

## 11.5 编辑/删除（仅作者或管理员）

```java
if (!post.getUserId().equals(userId) && !SecurityUtils.isCurrentUserAdmin()) {
    throw new BusinessException(403, "无权修改此文章");
}
```

💡 这一行权限检查，是项目里至少出现过 20 次的固定模式：**资源所有者** 或 **管理员** 可操作，其他人 403。

删除采用**逻辑删除**——`removeById` 内部由 `@TableLogic` 转化为 UPDATE。同时手动清理 `blog_post_tag` 中间表关联（标签是共享资源，关系不应保留）。

⚠️ 评论/点赞/收藏数据**不级联删**，保留可用于审计，展示时通过 `is_deleted` / `status` 过滤即可。

## 11.6 草稿自动保存

`blog_draft` 表与 `blog_post` 字段类似，多了 `user_id`，少了统计字段。

```java
public Long saveDraft(Long userId, SaveDraftRequest req) {
    BlogDraft draft = new BlogDraft();
    draft.setUserId(userId);
    draft.setTitle(htmlSanitizer.sanitizeRichText(req.getTitle()));
    draft.setContent(htmlSanitizer.sanitizeRichText(req.getContent()));
    // ...
    blogDraftMapper.insert(draft);
    return draft.getId();
}

public SaveDraftRequest getLatestDraft(Long userId) {
    return blogDraftMapper.selectOne(new LambdaQueryWrapper<BlogDraft>()
            .eq(BlogDraft::getUserId, userId)
            .orderByDesc(BlogDraft::getUpdateTime)
            .last("LIMIT 1"));
}
```

💡 前端可以做**节流（debounce）30 秒触发一次**自动保存，加上手动保存按钮，体验和"知乎写文章"接近。

## 11.7 高级搜索 + 搜索建议

`advancedSearch` 比基础列表多了：分类组合、时间区间、按 view/like/createTime 排序。

```java
if (req.getStartDate() != null) {
    wrapper.ge(BlogPost::getCreateTime, req.getStartDate());
}
if (req.getEndDate() != null) {
    wrapper.le(BlogPost::getCreateTime, req.getEndDate());
}
switch (req.getSortBy()) {
    case "viewCount" -> wrapper.orderByDesc(BlogPost::getViewCount);
    case "likeCount" -> wrapper.orderByDesc(BlogPost::getLikeCount);
    default -> wrapper.orderByDesc(BlogPost::getCreateTime);
}
```

`getSearchSuggestions` 给搜索框做自动补全：

```java
public List<String> getSearchSuggestions(String keyword) {
    if (!StringUtils.hasText(keyword)) return List.of();
    return blogPostMapper.selectList(new LambdaQueryWrapper<BlogPost>()
            .eq(BlogPost::getStatus, 1)
            .likeRight(BlogPost::getTitle, keyword)  // 前缀匹配
            .orderByDesc(BlogPost::getViewCount)
            .last("LIMIT 10"))
        .stream().map(BlogPost::getTitle).distinct().toList();
}
```

💡 `likeRight` 等价于 `LIKE 'keyword%'`，能命中索引；如果用 `like`，前缀通配符会让 MySQL 全表扫描。

---

# 第 12 章 互动模块（评论 / 点赞 / 收藏）

互动模块是论坛的"血液"。三个动作各有难点：评论考验**树形结构**，点赞考验**并发写**，收藏与点赞高度相似。

## 12.1 评论：嵌套回复、树形组装、级联删除

`blog_comment` 字段：id、postId、userId、parentId、content、createTime、isDeleted。

新建评论时校验"父评论必须属于同一篇文章"是关键：

```java
if (request.getParentId() != null) {
    BlogComment parent = this.getById(request.getParentId());
    if (parent == null) throw new BusinessException(404, "父评论不存在");
    if (!request.getPostId().equals(parent.getPostId())) {
        throw new BusinessException(400, "父评论不属于该文章");
    }
}
```

💡 **为什么校验？** 否则攻击者可以通过 `parentId` 把回复"移植"到别的文章下，制造混乱。

⚠️ **评论用 `sanitizePlainText`**——比文章正文更严格，**剥离所有 HTML**。因为评论场景用户没必要发富文本。这是 XSS 的额外加固。

随后 `incrementCommentCount(postId)` 用 SQL 原子加 1。

事件机制同步抛出 `CommentCreatedEvent`，留给通知模块去消费（见 13 章）。

**树形组装**：

```java
List<BlogComment> comments = this.list(...);  // 拍平的评论列表
Map<Long, CommentVO> voMap = comments.stream()
    .map(this::toVO)
    .collect(Collectors.toMap(CommentVO::getId, c -> c));

List<CommentVO> roots = new ArrayList<>();
for (CommentVO vo : voMap.values()) {
    if (vo.getParentId() == null) roots.add(vo);
    else {
        CommentVO parent = voMap.get(vo.getParentId());
        if (parent != null) parent.getReplies().add(vo);
    }
}
return roots;
```

💡 这是一遍循环就构建树的经典写法——先把所有节点 put 进 Map，再二次循环挂到 parent 的 children 上。**O(n) 不重复查询**，远快于"递归+SQL 多次访问"。

**级联删除**：

```java
public void deleteComment(Long commentId, Long userId) {
    BlogComment c = this.getById(commentId);
    // 权限检查同前
    List<Long> idsToDelete = new ArrayList<>();
    idsToDelete.add(commentId);
    collectChildCommentIds(commentId, idsToDelete, 1);  // 递归收集
    this.removeByIds(idsToDelete);   // 批量逻辑删除
}
```

⚠️ `collectChildCommentIds` 第三个参数是**递归深度**，限制嵌套层级（如 5 层）防止恶意构造的"深度炸弹"撑爆 JVM。

## 12.2 点赞：唯一约束 + 逻辑删除 + 细粒度锁 + DuplicateKeyException

`blog_like` 表的复合唯一索引 `(user_id, post_id)`：保证一人对一文章只能点一次赞。

但加了 `is_deleted` 字段后，会出现一个矛盾：
- A 点赞 → 插入记录 (1, 100, isDeleted=0)
- A 取消 → 逻辑删除变成 (1, 100, isDeleted=1)
- A 又点赞 → 想再插 (1, 100, isDeleted=0) **却被唯一索引挡住**！

项目的解法：**唯一索引带上 isDeleted 列**：`UNIQUE (user_id, post_id, is_deleted)`，这样删除后的记录与新记录的 (is_deleted) 不同，就不会冲突。或者另一种实现：**不再插新行，把已删除行 isDeleted 改回 0**。本项目走前者方案。

核心实现 `toggleLike`：

```java
String lockKey = userId + "-" + postId;
synchronized (getLock(lockKey)) {                // 细粒度锁
    BlogLike existing = this.getOne(...);
    if (existing != null) {
        // 取消点赞（逻辑删除）
        ((BlogLikeMapper) baseMapper).logicalDeleteById(existing.getId());
        blogPostService.decrementLikeCount(postId);
        result.setAction("unlike");
    } else {
        try {
            this.save(newLike);
            blogPostService.incrementLikeCount(postId);
            eventPublisher.publishEvent(new LikeCreatedEvent(...));
            result.setAction("like");
        } catch (DuplicateKeyException e) {
            // 并发：另一线程已经插入
            BlogLike concurrent = this.getOne(...);
            if (concurrent != null) {
                logicalDelete(concurrent.getId());
                decrementLikeCount(postId);
                result.setAction("unlike");
            }
        }
    }
}
```

💡 **细粒度锁**：用 `userId-postId` 作为 key，从 `ConcurrentHashMap` 取一把 Object 锁。同一用户对同一文章的并发请求会被串行化，但不影响 100 万其它用户。比 `synchronized(this)` 那种粗粒度锁高效百倍。

⚠️ **细粒度锁的内存泄漏问题**：每次点赞都 put 一个新对象进 map，永远不删——百万用户后 OOM。本项目通过 `LockEntry.createTime` + `MAX_LOCKS_SIZE` 做 LRU + 过期清理。

💡 **DuplicateKeyException 是最后防线**：即使前面 `getOne` 没查到，仍可能有另一线程刚插完。数据库唯一索引会兜住，并由代码处理为"取消点赞"或"重试"。

## 12.3 收藏：类似点赞 + "我的收藏"列表

`blog_collect` 表结构与 `blog_like` 几乎一模一样，逻辑也复用同样的"细粒度锁 + 唯一索引 + DuplicateKey 兜底"模式。差异在多了一个"我的收藏"接口：

```java
public IPage<PostListResponse> myCollects(Long userId, int page, int size) {
    Page<BlogCollect> p = new Page<>(page, size);
    IPage<BlogCollect> collects = blogCollectMapper.selectPage(p,
        new LambdaQueryWrapper<BlogCollect>()
            .eq(BlogCollect::getUserId, userId)
            .orderByDesc(BlogCollect::getCreateTime));

    List<Long> postIds = collects.getRecords().stream()
            .map(BlogCollect::getPostId).toList();
    if (postIds.isEmpty()) return new Page<>(page, size, 0);

    List<BlogPost> posts = blogPostMapper.selectBatchIds(postIds);
    // 转 VO 略
}
```

💡 **批量查询 selectBatchIds**——避免 N+1。一定要忍住"在循环里 selectById"的冲动。

---

# 第 13 章 关注与通知

## 13.1 关注关系表 blog_follow

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| follower_id | 谁关注（粉丝） |
| following_id | 被谁关注（博主） |
| create_time | 关注时间 |
| is_deleted | 逻辑删除 |

唯一索引 `(follower_id, following_id, is_deleted)`，含义同点赞。

## 13.2 关注/取关接口（不能关自己 + 双向计数）

`FollowServiceImpl#follow` 与 `toggleLike` 高度相似：

```java
if (targetUserId.equals(currentUserId)) {
    throw new BusinessException(400, "不能关注自己");
}
SysUser target = sysUserMapper.selectById(targetUserId);
if (target == null) throw new BusinessException(404, "用户不存在");

synchronized (getLock(currentUserId + "-" + targetUserId)) {
    BlogFollow exists = this.getOne(...);
    if (exists != null) {
        result.setAction("already_following");
    } else {
        try {
            this.save(newFollow);
            sysUserMapper.incrementFollowerCount(targetUserId);   // 博主粉丝+1
            sysUserMapper.incrementFollowingCount(currentUserId); // 我关注+1
            eventPublisher.publishEvent(new FollowCreatedEvent(currentUserId, targetUserId));
        } catch (DuplicateKeyException e) {
            // 并发兜底
        }
    }
}
```

💡 **双向计数**：粉丝数和关注数各自维护在 `sys_user` 表，原子 SQL 加减。

⚠️ **必须先校验"目标用户存在"再插表**——否则插了脏数据，统计永远偏。

取关流程几乎对称：检查记录在 → 逻辑删除 → 双向减 1。

## 13.3 粉丝/关注列表（分页）

```java
public IPage<UserVO> getFollowersPage(Long userId, int page, int size) {
    Page<BlogFollow> p = new Page<>(page, size);
    IPage<BlogFollow> followPage = this.page(p,
        new LambdaQueryWrapper<BlogFollow>()
            .eq(BlogFollow::getFollowingId, userId)
            .orderByDesc(BlogFollow::getCreateTime));

    List<Long> followerIds = followPage.getRecords().stream()
            .map(BlogFollow::getFollowerId).toList();
    if (followerIds.isEmpty()) return new Page<>(page, size, 0);

    List<SysUser> users = sysUserMapper.selectBatchIds(followerIds);
    // 拼 VO 返回
}
```

💡 同样是 **selectBatchIds** 避免 N+1。

## 13.4 通知系统：blog_notification

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| type | 通知类型（comment/like/follow/system） |
| title | 标题 |
| content | 内容 |
| from_user_id | 触发者（可能为 null：系统通知） |
| to_user_id | 接收者 |
| target_type | 目标类型（post/comment） |
| target_id | 目标 ID |
| is_read | 0 未读 / 1 已读 |
| create_time | |

`NotificationServiceImpl#sendNotification` 写得很谨慎：

```java
public void sendNotification(String type, String title, String content,
        Long fromUserId, Long toUserId, String targetType, Long targetId) {
    // 1. 不通知自己（自己评论自己的文章不会产生通知）
    if (fromUserId != null && fromUserId.equals(toUserId)) return;

    // 2. 检查接收者是否还存在
    if (toUserId != null) {
        SysUser target = sysUserMapper.selectById(toUserId);
        if (target == null) return;
    }

    BlogNotification n = new BlogNotification();
    n.setType(type);
    n.setTitle(title);
    n.setContent(content);
    n.setFromUserId(fromUserId);
    n.setToUserId(toUserId);
    n.setTargetType(targetType);
    n.setTargetId(targetId);
    n.setIsRead(0);
    this.save(n);
}
```

💡 "自己不通知自己"看似小事，实际是用户体验细节：你给自己文章点了赞、自己回复自己评论时，通知中心不会刷出来一条无意义记录。

## 13.5 事件机制：CommentCreatedEvent / LikeCreatedEvent / FollowCreatedEvent

事件类只是简单的数据载体：

```java
@Getter
public class LikeCreatedEvent {
    private final Long fromUserId;
    private final Long toUserId;
    private final Long postId;
    private final String postTitle;
    public LikeCreatedEvent(Long fromUserId, Long toUserId, Long postId, String title) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.postId = postId;
        this.postTitle = title;
    }
}
```

我们在三个地方 `eventPublisher.publishEvent(...)`：评论创建、点赞、关注。

💡 **为什么要事件机制？**
- **解耦**：业务逻辑（点赞）不需要知道有"通知中心"存在。未来加"短信提醒"也只需多一个监听器。
- **可异步**：监听器可注解为异步执行，避免拖慢主流程。

## 13.6 @TransactionalEventListener 异步发送通知（事务提交后）

```java
@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(CommentCreatedEvent e) {
        notificationService.sendNotification(
            "comment",
            "您的文章收到了新评论",
            e.getContent(),
            e.getFromUserId(),
            e.getToUserId(),
            "post", e.getPostId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLikeCreated(LikeCreatedEvent e) {
        notificationService.sendNotification(
            "like", "您的文章收到了新的点赞",
            "点赞了你的文章《" + e.getPostTitle() + "》",
            e.getFromUserId(), e.getToUserId(),
            "post", e.getPostId());
    }
}
```

逐项解释：

- `@TransactionalEventListener(phase = AFTER_COMMIT)`：等当前事务**真的提交**之后才触发，避免"事务回滚但通知已发"的脏数据。
- `@Async`：把发通知逻辑放到独立线程池执行，不阻塞主请求。
- 需要在配置类加 `@EnableAsync` 启用。

⚠️ **为何不用 Phase.BEFORE_COMMIT？** 因为如果事务最后回滚，通知就成了幽灵：用户永远找不到那条点赞，但被通知"被点赞了"。

---

# 第 14 章 校友圈与媒体

校友圈（Circle）类似微博/Twitter 的短动态系统，与博客（长文章）平行。

## 14.1 blog_circle_post 字段

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| user_id | 作者 |
| content | 文字内容（最长 2000 字） |
| content_type | 1 纯文本 / 2 图文 / 3 转发 |
| image_urls | JSON 数组，存图片 URL |
| location | 位置信息 |
| repost_id | 原动态 id（仅转发时） |
| view_count / like_count / comment_count / repost_count | 计数 |
| visibility | 0 公开 / 1 仅关注者 / 2 仅自己 |
| allow_comment | 是否允许评论（0/1） |
| allow_repost | 是否允许转发（0/1） |
| is_top | 是否置顶 |
| status | 1 正常 / 2 已删除（这里用 status 代替 is_deleted） |
| tags | JSON 数组 |
| create_time / update_time | |

💡 **为何 status 而不用 @TableLogic**？因为校友圈的"删除"不是简单标志，可能有"暂存""违规下架"等状态，故用 status 数值。

## 14.2 推荐流 vs 关注流

`getRecommendFeed`：

```java
LambdaQueryWrapper<CirclePost> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(CirclePost::getStatus, 1)
       .and(w -> w.eq(CirclePost::getVisibility, 0)        // 公开
                  .or(currentUserId != null, w2 ->
                      w2.eq(CirclePost::getUserId, currentUserId)
                        .ne(CirclePost::getVisibility, 0)))// 自己的非公开
       .orderByDesc(CirclePost::getIsTop)   // 置顶优先
       .orderByDesc(CirclePost::getCreateTime);
```

`getFollowingFeed`：

```java
List<UserVO> followings = followService.getFollowing(userId);
List<Long> ids = followings.stream().map(UserVO::getId).toList();

wrapper.eq(CirclePost::getStatus, 1)
       .in(CirclePost::getUserId, ids)
       .and(w -> w.eq(CirclePost::getVisibility, 0)
                  .or().eq(CirclePost::getVisibility, 1)
                  .or().eq(CirclePost::getVisibility, 2)
                       .eq(CirclePost::getUserId, userId));
```

💡 **关注流**只展示**我关注的人**的动态：先取关注列表，再用 `in` 拼出 SQL。空关注列表直接返回空集合（避免 IN () 报错）。

## 14.3 canViewPost 权限校验

```java
private boolean canViewPost(CirclePost post, Long currentUserId) {
    // 1. 作者本人总是可以看
    if (currentUserId != null && post.getUserId().equals(currentUserId)) return true;
    // 2. 公开动态人人可看
    if (post.getVisibility() == null || post.getVisibility() == 0) return true;
    // 3. 仅关注者可见——必须是登录用户且关注了博主
    if (post.getVisibility() == 1) {
        if (currentUserId == null) return false;
        return followService.isFollowing(currentUserId, post.getUserId());
    }
    // 4. 仅自己可见，但不是作者本人，拒绝
    return false;
}
```

⚠️ 进入 `getPostDetail` 等方法时务必先调 `canViewPost`，否则会泄露隐私动态。

## 14.4 转发动态（原动态隐藏处理）

```java
if (repostId != null) {
    CirclePost original = this.getById(repostId);
    if (original == null || original.getStatus() == 2) {
        throw new BusinessException(404, "原动态不存在");
    }
    if (original.getAllowRepost() == 0) {
        throw new BusinessException(403, "该动态禁止转发");
    }
    if (!canViewPost(original, userId)) {
        throw new BusinessException(403, "无权转发此动态");
    }
    if (original.getVisibility() == 2) {  // 原动态仅自己可见
        visibility = 1;                    // 自动降级到仅关注者
    }
    contentType = 3;
}
```

💡 一系列校验保证转发关系不被滥用：原动态必须存在 / 允许转发 / 我能看到。最后那条"原动态仅自己可见时，转发自动设为仅关注者"是个隐私升级——不可能让"自我可见"通过转发暴露给陌生人。

## 14.5 校友圈点赞/评论/搜索

技术细节与博客点赞、博客评论一模一样：细粒度锁、DuplicateKeyException 兜底、原子 +/-、事件机制。**复用既有模式，避免重复造轮子**——这是项目代码自始至终的纪律。

## 14.6 媒体上传 blog_media（500MB + 类型白名单 + 重命名）

```java
public Long upload(MultipartFile file, Long userId) {
    if (file.getSize() > 500L * 1024 * 1024) {
        throw new BusinessException(400, "文件不能超过 500MB");
    }
    String original = file.getOriginalFilename();
    String ext = getExtension(original).toLowerCase();
    if (!ALLOWED_EXT.contains(ext)) {  // jpg/png/mp4/...
        throw new BusinessException(400, "不支持的文件类型");
    }
    String newName = UUID.randomUUID() + "." + ext;
    Path target = Paths.get(uploadDir, newName);
    Files.copy(file.getInputStream(), target);

    BlogMedia m = new BlogMedia();
    m.setUserId(userId);
    m.setOriginalName(original);
    m.setStoredName(newName);
    m.setUrl("/media/" + newName);
    m.setSize(file.getSize());
    m.setMimeType(file.getContentType());
    blogMediaMapper.insert(m);
    return m.getId();
}
```

💡 **三道防线**：
1. **大小限制**：避免 DOS（用户疯狂上传超大文件）。
2. **后缀白名单**：拒绝 .exe / .sh / .php，避免在服务器上落地恶意可执行文件。
3. **UUID 重命名**：防止用户上传 `../../../etc/passwd` 这样的"路径穿越"攻击。

⚠️ 仅靠后缀不够——攻击者可以把 .php 文件改名为 .jpg。生产环境还需校验 magic number（文件头）。

## 14.7 文章绑定媒体 blog_post_media

中间表 `(post_id, media_id, sort_order)`：一篇文章可挂多张图，按 sort_order 排序。删除文章时只需移除关联，不删媒体本体——媒体可被多个动态/文章共享。

---

# 第 15 章 趋势 + 举报系统

## 15.1 blog_trending 热度统计

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| target_type | post / tag |
| target_id | 目标 ID |
| score | 热度分（计算结果） |
| period | 时间周期：daily / weekly |
| stat_time | 统计基准时间 |
| create_time | |

## 15.2 热度公式

真实项目的热度计算采用**固定权重加权累加**，公式如下：

```
score = view_count × 1 + like_count × 5 + comment_count × 10
```

三个常量的定义在 `TrendingServiceImpl.java` 中（第 43-45 行）：

```java
private static final int VIEW_WEIGHT = 1;
private static final int LIKE_WEIGHT = 5;
private static final int COMMENT_WEIGHT = 10;
```

**没有** `collect_count` 权重，也没有时间衰减函数 `decay()`。每天凌晨 0 点定时任务（`@Scheduled(cron = "0 0 0 * * ?")`）全量重新计算所有已发布文章的热度分，结果写入 `blog_trending` 表。查询接口只读这张表，零计算压力。

⚠️ 项目未使用收藏数权重和时间衰减。如需调整权重，需修改源码中这三个常量并重新部署。

## 15.3 热门文章/热门标签 API

```java
@GetMapping("/trending/posts")
public Result<List<PostListResponse>> hotPosts(
        @RequestParam(defaultValue = "daily") String period,
        @RequestParam(defaultValue = "10") int limit) {
    return Result.success(trendingService.getHotPosts(period, limit));
}
```

Service 内部按 score 降序，再去 `blog_post` 表查详情。

💡 **每天 0 点跑一次定时任务**重新计算所有文章热度，结果写入 `blog_trending`。读接口只查这张表，零计算。

## 15.4 举报模块：用户举报 + 重复检查 + 管理员封禁/下架

`blog_report` 字段：id、reporterId、targetType（post/comment/circle/user）、targetId、reason、status（0 待处理/1 已处理/2 已驳回）、handleResult、handleTime、handlerId、create_time。

举报核心代码：

```java
public Long report(ReportCreateRequest req, Long userId) {
    // 1. 24 小时内不能重复举报同一目标
    LambdaQueryWrapper<BlogReport> w = new LambdaQueryWrapper<>();
    w.eq(BlogReport::getReporterId, userId)
     .eq(BlogReport::getTargetType, req.getTargetType())
     .eq(BlogReport::getTargetId, req.getTargetId())
     .ge(BlogReport::getCreateTime, LocalDateTime.now().minusHours(24));
    if (this.count(w) > 0) {
        throw new BusinessException(400, "您已举报过此内容");
    }
    // 2. 入库
    BlogReport r = new BlogReport();
    BeanUtils.copyProperties(req, r);
    r.setReporterId(userId);
    r.setStatus(0);
    this.save(r);
    return r.getId();
}
```

管理员处理时：

```java
public void handleReport(Long reportId, Integer action, String result, Long adminId) {
    BlogReport r = this.getById(reportId);
    if (r == null) throw new BusinessException(404, "举报不存在");

    r.setStatus(action == 1 ? 1 : 2);
    r.setHandleResult(result);
    r.setHandlerId(adminId);
    r.setHandleTime(LocalDateTime.now());
    this.updateById(r);

    if (action == 1) {
        // 处置：根据 targetType 下架内容 / 封禁用户
        switch (r.getTargetType()) {
            case "post" -> blogPostMapper.updateStatus(r.getTargetId(), 2);
            case "comment" -> blogCommentMapper.deleteById(r.getTargetId());
            case "user" -> sysUserMapper.disableUser(r.getTargetId());
        }
    }
}
```

💡 **重复举报防护**：限制 24 小时内只能举报同一目标一次，避免一个用户疯狂刷量；与"通知不通知自己"同属于体验细节。

⚠️ 真正生产环境还需要：
- 举报内容 XSS 过滤（reason 字段同样要 sanitizePlainText）。
- 管理员操作日志（哪个 admin 在何时封了谁，便于事后追溯）。
- 严重举报阈值机制：同一内容被 N 个用户举报后自动隐藏（人工复核前先撤下）。

---

## 本章小结

走完六大模块，我们能回答下面这些问题了：

1. **如何设计一张实体表？** → 业务字段 + 冗余统计 + 时间戳 + 逻辑删除标记。
2. **如何防止用户枚举？** → 注册和登录的失败提示统一模糊化。
3. **如何防爆破？** → 失败次数原子累加 + 锁定时间窗口。
4. **如何防 XSS？** → Jsoup 白名单过滤，富文本宽松、纯文本严格。
5. **如何防并发写错乱？** → 细粒度锁 + 数据库唯一索引 + DuplicateKeyException 兜底。
6. **如何避免 N+1 查询？** → `selectBatchIds` 一次取齐 + Map O(1) 查找。
7. **如何让通知不污染主流程？** → ApplicationEventPublisher + `@TransactionalEventListener(AFTER_COMMIT) + @Async`。
8. **如何统计热度？** → 固定权重加权（view×1 + like×5 + comment×10）+ 每天凌晨定时任务全量预计算。

---

下一部分将进入 **前端实战**，把这些 API 接到一个用 HTML5 + JavaScript 写的论坛主页里——届时你就能从浏览器的"点击点赞"按钮，一路追到本章里的 `toggleLike` 方法，把整个全栈链路打通。
# 《Java Spring Boot 零基础完整学习手册》

## 第四部分 进阶与部署

> 基于真实项目"校园博客论坛系统 v1.34"的实战教材
> 项目地址：https://github.com/Xinghe-0203/Campus_Blog

---

读完前三部分，你应该已经能写出基本的 Spring Boot CRUD 接口，能登录、能发文章、能评论。但这远远不够。一个能上线、能扛住真实用户的系统，必须经过**安全加固、性能优化、充分测试、规范部署**这四关。

第四部分就是带你走完这四关。所有讲解都会引用本项目从 v1.10 到 v1.34 的真实修复案例 —— 这些"坑"是项目作者真踩过的，每一个都对应一次代码提交。

---

# 第 16 章 安全加固实战

## 16.1 OWASP Top 10 安全风险概览

OWASP（Open Web Application Security Project）每隔几年会发布一次"Web 应用十大安全风险榜单"。学习安全的第一步，是知道有哪些坑。下面是 2021 版的精简清单：

| 风险编号 | 名称 | 通俗解释 | 本项目对应防护 |
|----------|------|----------|----------------|
| A01 | 失效的访问控制 | 普通用户能改别人文章 | 越权校验（16.8 节） |
| A02 | 加密机制失效 | 密码明文存数据库 | BCrypt 加盐（16.4 节） |
| A03 | 注入 | SQL 注入、命令注入 | MyBatis #{} 参数化（16.2 节） |
| A04 | 不安全设计 | 没有限流、没有锁 | 登录限流（16.7 节） |
| A05 | 安全配置错误 | 默认密码、调试模式开 | .env 隔离（16.5 节） |
| A06 | 自带漏洞和过时组件 | 老版本依赖有 CVE | pom.xml 用最新 LTS |
| A07 | 身份认证失效 | Token 不能撤销 | JWT 黑名单（16.5 节） |
| A08 | 软件和数据完整性失效 | 反序列化漏洞 | 不接收不可信对象 |
| A09 | 安全日志和监控缺失 | 攻击了都不知道 | log.warn 关键操作 |
| A10 | 服务端请求伪造（SSRF） | 让服务器去访问内网 | URL 白名单 |

⚠️ **重要观念**：安全不是某个"模块"，而是渗透到每一行代码里的"思维方式"。本项目从 v1.10 开始就一直在做安全加固，到 v1.34 已经迭代了 20 多次。

---

## 16.2 SQL 注入防护

### 16.2.1 什么是 SQL 注入

假设有一段拼接 SQL 的代码：

```java
// 危险写法（不要这样写！）
String sql = "SELECT * FROM sys_user WHERE username = '" + username + "'";
```

如果攻击者把 `username` 传成 `' OR '1'='1`，最终拼出来的 SQL 是：

```sql
SELECT * FROM sys_user WHERE username = '' OR '1'='1'
```

`'1'='1'` 永远为真，这条 SQL 会把整张用户表查出来。更狠的攻击者甚至能传 `'; DROP TABLE sys_user; --`，直接把表删了。

### 16.2.2 MyBatis 的 #{} 与 ${}

本项目使用 MyBatis Plus，所有 SQL 参数都通过 `#{}` 占位符传递。**这是最重要的注入防护**。

| 写法 | 效果 | 安全性 |
|------|------|--------|
| `#{username}` | 编译为预编译语句的 `?` 占位符，参数值由 JDBC 驱动转义 | 安全 |
| `${username}` | 直接做字符串替换 | 危险 |

举个例子，下面这两条乍看一样：

```xml
<!-- 安全：参数化查询 -->
<select id="findByUsername">
  SELECT * FROM sys_user WHERE username = #{username}
</select>

<!-- 危险：字符串拼接 -->
<select id="findByUsername">
  SELECT * FROM sys_user WHERE username = '${username}'
</select>
```

第一条，无论 `username` 是什么字符（哪怕带单引号），都只会被当成"一个值"处理；第二条，攻击者完全能改变 SQL 结构。

### 16.2.3 项目中的实践

本项目的所有 Mapper 都继承自 `BaseMapper<T>`，标准 CRUD 方法（insert、updateById、selectById、selectList）都是 MyBatis Plus 内置的，全部使用预编译 SQL，零拼接风险。

⚠️ **铁律**：除了 `ORDER BY 字段名`、`表名` 这种结构性内容外，**任何用户输入都不能用 `${}`**。如果一定要做动态排序，务必先用白名单校验字段名。

---

## 16.3 XSS 跨站脚本防护（基于真实 HtmlSanitizer.java）

### 16.3.1 什么是 XSS

XSS（Cross-Site Scripting）的本质是**让浏览器去执行攻击者注入的脚本**。比如：

- 用户 A 发了一篇文章，标题里写了 `<script>alert('被攻击了')</script>`
- 这段标题被原样存进数据库
- 用户 B 打开这篇文章列表，浏览器看到 `<script>` 就会执行
- 攻击者可以用脚本偷 B 的 Cookie、改 B 的密码、转账……

XSS 在博客、评论这种富文本场景里最常见。

### 16.3.2 项目的解决方案：Jsoup 白名单过滤

本项目在 v1.10 引入 Jsoup 做 XSS 防护，核心代码就在 `HtmlSanitizer.java` 里：

```java
@Component
public class HtmlSanitizer {

    /**
     * 宽松白名单：允许部分 HTML 标签（用于文章内容等富文本）
     */
    private static final Safelist RELAXED_WHITELIST = Safelist.relaxed()
            .addTags("span", "div", "hr", "table", "thead", "tbody", "tr", "th", "td")
            .addAttributes("a", "href", "title", "target")
            .addAttributes("img", "src", "alt", "title", "width", "height")
            .addProtocols("img", "src", "http", "https")
            .addProtocols("a", "href", "http", "https", "mailto")
            .preserveRelativeLinks(false);

    /**
     * 严格白名单：完全清空所有 HTML 标签（用于评论）
     */
    private static final Safelist STRICT_WHITELIST = Safelist.none();

    public String sanitizeRichText(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return Jsoup.clean(html, RELAXED_WHITELIST);
    }

    public String sanitizePlainText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Jsoup.clean(text, STRICT_WHITELIST);
    }
}
```

**两套白名单的设计理念**：

1. **`RELAXED_WHITELIST`（宽松）**：允许文章正文出现 `<p>`、`<h1>`、`<img>` 这些常见富文本标签，但禁止 `<script>`、`<iframe>`、`onclick=...` 等危险元素。
2. **`STRICT_WHITELIST`（严格）**：用于评论、分类名等不需要富文本的场景，**所有** HTML 标签全部清掉。

### 16.3.3 关键细节：限制图片协议

注意这一行：

```java
.addProtocols("img", "src", "http", "https")
```

这是 v1.10 一次安全审查后专门加的。**Jsoup 默认允许 `data:` 协议**，攻击者可能写：

```html
<img src="data:image/svg+xml,<svg onload=alert(1)></svg>">
```

只允许 `http`、`https` 协议后，这种 bypass 就被堵死了。

### 16.3.4 实际使用

在文章 Service 里：

```java
@Autowired
private HtmlSanitizer htmlSanitizer;

public void createPost(BlogPost post) {
    // 文章内容是富文本，使用宽松白名单
    post.setTitle(htmlSanitizer.sanitizePlainText(post.getTitle()));
    post.setContent(htmlSanitizer.sanitizeRichText(post.getContent()));
    post.setSummary(htmlSanitizer.sanitizePlainText(post.getSummary()));
    blogPostMapper.insert(post);
}
```

⚠️ **过滤的位置**：必须在**入库前**过滤。如果只在前端展示时过滤，绕过非常容易（比如直接用 Postman 调接口）。**永远不要相信前端**。

---

## 16.4 密码安全

### 16.4.1 为什么不能明文存密码

数据库被脱库的事经常发生。如果你存的是明文，攻击者拿到数据库就拿到了所有用户的密码 —— 而且很多用户在多个网站用同一个密码，你还连累了别的网站。

### 16.4.2 BCrypt 加盐 + 12 轮

本项目在 `SecurityConfig` 中配置：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    // 强度 12，更安全
    return new BCryptPasswordEncoder(12);
}
```

**BCrypt 三大特性**：

1. **加盐**：每次加密都自动生成不同的随机盐，所以同一个密码"123456"两次加密结果不同。
2. **慢函数**：12 轮 BCrypt 大约需要 200~300 毫秒。攻击者就算拿到哈希，暴力破解也极慢。
3. **不可逆**：只能"验证"密码，不能"还原"密码。所以"忘记密码"功能是发邮件重置，不是把密码寄给你。

### 16.4.3 @JsonIgnore 防序列化泄露

光加密还不够，还得防止密码哈希被序列化到 JSON 返回给前端。看本项目 `SysUser` 实体：

```java
@JsonIgnore
@TableField("password")
private String password;
```

`@JsonIgnore` 是 Jackson 提供的注解，告诉 JSON 序列化器："这个字段永远不要输出"。即使你不小心 `return user`，密码也不会泄露。

⚠️ **更稳妥做法**：永远不要直接返回 `SysUser`，而是返回 `UserVO`（View Object，专门给前端看的）。本项目所有返回用户信息的接口都遵守这一规则。

---

## 16.5 JWT 安全

### 16.5.1 项目的环境变量隔离

JWT 密钥不能写死在代码里。本项目通过 `.env` 文件 + `DotenvConfig` 加载器实现密钥隔离：

```
# .env.example
JWT_SECRET=your_jwt_secret_key_here_minimum_32_characters
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

启动时 `EnvValidationConfig` 会先校验：

```java
if (isBlank(jwtSecret)) {
    log.error("   ✗ JWT_SECRET 未配置");
    hasError = true;
}
// ……
if (hasError) {
    throw new IllegalStateException("环境变量配置不完整，启动失败");
}
```

只要 `JWT_SECRET` 没配，应用直接启动失败。这种"快速失败"（fail-fast）思想能避免线上环境裸奔。

⚠️ **密钥长度**：JWT HS256 算法要求密钥至少 256 位，也就是 **32 个 ASCII 字符**。短了 jjwt 库会直接抛异常。

### 16.5.2 JWT 黑名单机制（撤销 Token）

JWT 的痛点是"无状态" —— Token 一旦签发，到期前就一直有效，**没法主动撤销**。但用户登出、修改密码、被封禁时，明明应该让旧 Token 立刻失效。

本项目用"黑名单"解决这个问题。`JwtUtils` 维护一个 ConcurrentMap，存放被撤销的 Token：

```java
// 用户登出
public void logout(String token) {
    jwtUtils.revokeToken(token);  // 加入黑名单
}

// 验证 Token 时
public boolean validateToken(String token) {
    if (jwtUtils.isBlacklisted(token)) {
        return false;
    }
    // ……
}
```

但黑名单会越来越大，所以本项目用 `JwtSchedulerConfig` 定时清理：

```java
@Component
@EnableScheduling
public class JwtSchedulerConfig {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 每小时清理一次过期 Token 黑名单
     */
    @Scheduled(fixedRate = 3600000) // 1 小时
    public void cleanExpiredTokens() {
        jwtUtils.cleanExpiredTokens();
    }
}
```

⚠️ **生产级建议**：内存黑名单在多机部署时不一致，建议升级到 Redis（自带 TTL 过期）。本项目已规划但尚未集成。

### 16.5.3 Refresh Token 轮换（v1.29 修复）

刷新 Token（refresh token）有效期更长（7 天），是个高价值目标，一旦泄露危害更大。所以本项目实现了**轮换（rotation）**：

> 用一次就废弃，每次刷新颁发**新的 access + 新的 refresh**，旧的 refresh 立即加入黑名单。

```java
public TokenPair refreshToken(String oldRefreshToken) {
    // 1. 校验旧 refresh token
    Long userId = jwtUtils.parseUserId(oldRefreshToken);

    // 2. 立刻把旧 refresh 加入黑名单（防止重放）
    jwtUtils.revokeToken(oldRefreshToken);

    // 3. 颁发新的 access + refresh
    String newAccessToken = jwtUtils.generateAccessToken(userId);
    String newRefreshToken = jwtUtils.generateRefreshToken(userId);

    return new TokenPair(newAccessToken, newRefreshToken);
}
```

### 16.5.4 先验签再查黑名单（v1.34 修复）

v1.34 修复了一个潜在 bypass：原本黑名单查询用的是 Token 字符串本身，攻击者可能伪造个看起来像 Token 的字符串去"查黑名单"，绕过逻辑。

修复方法是先做签名验证，验签通过的才查黑名单：

```java
public boolean validateToken(String token) {
    try {
        // 1. 先验证签名（伪造的 Token 在这里就被拒了）
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 2. 验签通过后再查黑名单
        if (isBlacklisted(token)) {
            return false;
        }
        return true;
    } catch (JwtException e) {
        return false;
    }
}
```

---

## 16.6 防止信息泄露

### 16.6.1 登录响应不返回敏感信息（v1.29 修复）

v1.29 之前，登录成功后返回的 JSON 包含了 email、role、phone 等字段。这些信息一旦在前端 JS 里，浏览器调试工具就能看到，普通用户能看到自己的没问题，但有些字段（如 `loginFailCount`）属于内部实现，不该暴露。

修复后只返回必要信息：

```java
public LoginResponseVO login(LoginRequest request) {
    // ……验证逻辑
    LoginResponseVO vo = new LoginResponseVO();
    vo.setUserId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setNickname(user.getNickname());
    vo.setAvatar(user.getAvatar());
    vo.setAccessToken(accessToken);
    vo.setRefreshToken(refreshToken);
    // 不再返回 email、role、phone 等
    return vo;
}
```

### 16.6.2 用户枚举防护

注册接口的常见漏洞：

- 用户名已存在 → 返回 "用户名已被注册"
- 用户名不存在 → 返回 "注册成功"

攻击者用脚本枚举一万个常见用户名，就能知道哪些已经存在 —— 这叫**用户枚举（user enumeration）**。

本项目的修复方案：注册失败一律返回通用错误：

```java
if (existingUser != null) {
    // 不暴露"用户名已存在"
    throw new BusinessException(400, "注册失败，请稍后重试");
}
```

登录接口同理：用户名不存在和密码错误，**返回完全相同的错误信息**："用户名或密码错误"。

### 16.6.3 全局异常兜底不暴露异常类名

`GlobalExceptionHandler.java` 处理所有未捕获异常：

```java
@ExceptionHandler(Exception.class)
public Result<?> handleException(Exception e) {
    log.error("系统异常", e);  // 服务器日志记录详细堆栈
    return Result.error("服务器繁忙，请稍后重试"); // 给用户的友好提示
}
```

⚠️ **绝对不要**这样写：

```java
return Result.error("系统错误：" + e.getMessage()); // 危险！
```

`e.getMessage()` 可能包含 SQL 语句、文件路径、数据库表名，攻击者从中可推断架构细节。

---

## 16.7 登录限流与账户锁定

### 16.7.1 暴力破解的危害

如果接口对同一账号无限次尝试登录，攻击者可以用脚本一秒尝试一千次，常用密码字典只需几分钟就能跑完。

### 16.7.2 项目的方案：5 次失败锁 15 分钟

`SysUser` 实体里有这两个字段：

```java
private Integer loginFailCount;        // 失败次数
private LocalDateTime lockedUntil;     // 锁定到期时间
```

登录逻辑（简化版）：

```java
public void login(String username, String password) {
    SysUser user = sysUserMapper.findByUsername(username);

    // 1. 是否在锁定期内？
    if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
        throw new BusinessException(423, "账号已锁定，请 15 分钟后再试");
    }

    // 2. 校验密码
    if (!passwordEncoder.matches(password, user.getPassword())) {
        // 失败次数 +1（原子更新，下一节讲）
        sysUserMapper.incrementLoginFailCount(user.getId());

        if (user.getLoginFailCount() + 1 >= 5) {
            // 锁定 15 分钟
            sysUserMapper.lockUser(user.getId(), LocalDateTime.now().plusMinutes(15));
        }
        throw new BusinessException(401, "用户名或密码错误");
    }

    // 3. 登录成功，清零失败次数
    sysUserMapper.resetLoginFailCount(user.getId());
}
```

### 16.7.3 原子更新防并发

如果两个请求同时来，分别读到 `loginFailCount=4`，分别 `+1` 后写回 `5`，就成了"丢失更新"。本项目用 SQL 直接 `count = count + 1` 解决：

```xml
<update id="incrementLoginFailCount">
    UPDATE sys_user
    SET login_fail_count = login_fail_count + 1
    WHERE id = #{userId}
</update>
```

数据库的 UPDATE 操作本身原子，不需要 Java 加锁。

⚠️ **设计取舍**：锁定到期时间用绝对时间（`lockedUntil`），不是计数器倒数。这样服务器重启不丢状态。

---

## 16.8 越权防护

### 16.8.1 越权的两种类型

- **水平越权**：用户 A 操作用户 B 的资源（A 删了 B 的文章）
- **垂直越权**：普通用户做了管理员才能做的事（普通用户调用 `/api/admin/banUser`）

### 16.8.2 项目的统一处理

每个修改/删除接口必须做"所有权检查"：

```java
public void deletePost(Long postId) {
    Long currentUserId = SecurityUtils.getCurrentUserIdOrNull();
    if (currentUserId == null) {
        throw new BusinessException(401, "请先登录");
    }

    BlogPost post = blogPostMapper.selectById(postId);
    if (post == null) {
        throw new BusinessException(404, "文章不存在");
    }

    // 关键：只有作者或管理员能删
    SysUser currentUser = sysUserMapper.selectById(currentUserId);
    if (!post.getUserId().equals(currentUserId) && !"ADMIN".equals(currentUser.getRole())) {
        throw new BusinessException(403, "无权操作");
    }

    blogPostMapper.deleteById(postId);
}
```

### 16.8.3 SecurityUtils 取当前用户

`SecurityUtils.getCurrentUserIdOrNull()` 从 SecurityContext 中提取当前登录用户 ID。这个方法**不抛异常，返回 null**，方便调用方按需处理（公共接口允许游客访问）。

⚠️ **审查清单**：本项目从 v1.20 开始建立了"敏感接口审查清单"，每次发版前对所有写接口逐一确认权限校验是否健全。

---

## 16.9 文件上传安全

### 16.9.1 三道关卡

文件上传是攻击重灾区，本项目设三道关：

1. **类型白名单**：只允许图片（jpg/png/gif/webp）和视频（mp4/avi）

```java
private static final Set<String> ALLOWED_IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

if (!ALLOWED_IMAGE_EXT.contains(ext.toLowerCase())) {
    throw new BusinessException(400, "不支持的文件类型");
}
```

2. **大小限制**（`application.yml`）：

```yaml
mybatis-plus:
  servlet:
    multipart:
      enabled: true
      max-file-size: 500MB
      max-request-size: 500MB
```

3. **重命名防路径穿越**：用 UUID 重新生成文件名，避免攻击者上传 `../../../etc/passwd` 这种恶意路径：

```java
String newName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
File target = new File(uploadDir, newName);
```

### 16.9.2 静态资源访问配置

`WebMvcConfig` 把上传目录映射成 HTTP 路径：

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String projectRoot = System.getProperty("user.dir");
    String uploadPath = Paths.get(projectRoot, uploadBasePath).toString();

    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadPath + "/");
}
```

⚠️ **生产环境最佳实践**：上传文件不放在应用服务器，而是放对象存储（OSS、S3、MinIO），独立域名，并配置不允许执行任何脚本（即便上传成功了也无法被解析为可执行）。

---

### 第 16 章 实战清单

- [ ] 所有 SQL 用 `#{}` 而不是 `${}`
- [ ] 入库前调用 `HtmlSanitizer` 过滤 HTML
- [ ] 密码用 `BCryptPasswordEncoder(12)` 加密
- [ ] 实体的密码字段加 `@JsonIgnore`
- [ ] JWT 密钥放 `.env`，启动校验
- [ ] 用户登出实现黑名单撤销
- [ ] Refresh Token 一次性轮换
- [ ] 登录失败 5 次锁 15 分钟
- [ ] 所有写接口校验所有权
- [ ] 文件上传校验类型 + 重命名

---

# 第 17 章 性能优化技巧

性能优化的第一原则：**不要过早优化，先测出瓶颈再动手**。但理解常见优化套路能帮你写出"开箱性能就不差"的代码。

## 17.1 数据库优化

### 17.1.1 索引设计

本项目数据库设计的索引清单：

| 表 | 索引字段 | 类型 | 原因 |
|----|----------|------|------|
| `sys_user` | username | UNIQUE | 登录查找 + 唯一约束 |
| `sys_user` | email | UNIQUE | 注册校验 + 唯一约束 |
| `blog_post` | user_id | NORMAL | 查"我的文章" |
| `blog_post` | category_id | NORMAL | 按分类筛选 |
| `blog_post` | create_time | NORMAL | 按时间排序 |
| `blog_like` | (user_id, post_id) | UNIQUE | 防重复点赞 + 复合索引 |
| `blog_post_tag` | (post_id, tag_id) | PRIMARY | 关联表主键 |

### 17.1.2 复合索引顺序很关键

复合索引 `(user_id, post_id)` 能加速：

```sql
WHERE user_id = ? AND post_id = ?     -- 全索引命中
WHERE user_id = ?                     -- 命中前缀
```

但**不能**加速：

```sql
WHERE post_id = ?  -- 用不到这个索引（最左前缀原则）
```

⚠️ **经验法则**：复合索引把"区分度高"和"经常单独查"的字段放前面。

### 17.1.3 别用 select *

```java
// 不好
List<BlogPost> posts = blogPostMapper.selectList(null);

// 更好（只查需要的字段）
LambdaQueryWrapper<BlogPost> wrapper = new LambdaQueryWrapper<>();
wrapper.select(BlogPost::getId, BlogPost::getTitle, BlogPost::getCreateTime);
```

文章正文 `content` 字段往往几 KB，列表页根本用不到，每次都查浪费带宽和内存。

---

## 17.2 N+1 查询问题

### 17.2.1 什么是 N+1

最经典的性能坑。假设你查 100 篇文章，然后挨个查作者：

```java
// 反面示例
List<BlogPost> posts = blogPostMapper.selectList(null);  // 1 条 SQL
for (BlogPost post : posts) {
    SysUser author = sysUserMapper.selectById(post.getUserId()); // 100 条 SQL
    post.setAuthor(author);
}
// 一共 1 + 100 = 101 条 SQL
```

### 17.2.2 解决：批量查询 + Map 组装

```java
List<BlogPost> posts = blogPostMapper.selectList(null);

// 1. 收集所有 userId
Set<Long> userIds = posts.stream()
        .map(BlogPost::getUserId)
        .collect(Collectors.toSet());

// 2. 一次查出所有作者
List<SysUser> authors = sysUserMapper.selectBatchIds(userIds);

// 3. 转 Map 方便查找
Map<Long, SysUser> authorMap = authors.stream()
        .collect(Collectors.toMap(SysUser::getId, u -> u));

// 4. 组装
for (BlogPost post : posts) {
    post.setAuthor(authorMap.get(post.getUserId()));
}
// 总共 2 条 SQL，无论文章多少
```

⚠️ **检测方法**：开启 SQL 日志（见 17.4），如果一个接口打印出几十上百条 SELECT，多半是 N+1。

---

## 17.3 分页优化

### 17.3.1 PaginationInnerInterceptor

MyBatis Plus 提供了分页插件：

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

使用方式：

```java
Page<BlogPost> page = new Page<>(1, 10); // 第 1 页，每页 10 条
Page<BlogPost> result = blogPostMapper.selectPage(page, null);
```

### 17.3.2 深分页问题

```sql
SELECT * FROM blog_post LIMIT 1000000, 10
```

MySQL 必须扫描前 100 万条才能跳过去取后 10 条。解决思路：**用主键过滤**

```sql
-- 已知上一页最后一条 id 是 1000000
SELECT * FROM blog_post WHERE id > 1000000 LIMIT 10
```

这种叫"游标分页"或"keyset pagination"，性能恒定。

---

## 17.4 缓存策略

### 17.4.1 Spring Cache 注解（已规划）

```java
@Cacheable(value = "post", key = "#postId")
public BlogPost getPostById(Long postId) {
    return blogPostMapper.selectById(postId);
}

@CacheEvict(value = "post", key = "#postId")
public void deletePost(Long postId) {
    blogPostMapper.deleteById(postId);
}
```

`@Cacheable`：先查缓存，没有就执行方法并缓存结果。
`@CacheEvict`：执行方法后清除缓存。

### 17.4.2 项目现状

本项目目前用 ConcurrentHashMap 做内存缓存（如黑名单、防刷指纹），尚未集成 Redis。**v1.31 规划接入 Redis**，作为：

- JWT 黑名单（自动 TTL）
- 热门文章列表缓存
- 限流计数器

---

## 17.5 减少重复计算

### 17.5.1 阅读量增加：CAS 替代 select-then-update（v1.14 修复）

v1.14 之前的代码（典型的 TOCTOU 漏洞，Time-of-check to time-of-use）：

```java
// 反面示例
BlogPost post = blogPostMapper.selectById(postId);
post.setViewCount(post.getViewCount() + 1);
blogPostMapper.updateById(post);
// 100 个并发请求来时，会丢失大量计数
```

修复后用 SQL 原子更新：

```xml
<update id="incrementViewCount">
    UPDATE blog_post
    SET view_count = view_count + 1
    WHERE id = #{postId}
</update>
```

### 17.5.2 防刷：ConcurrentHashMap 缓存指纹

阅读量也不能让同一用户狂刷。用 IP+userId 作为 key 缓存：

```java
private final Map<String, Long> viewFingerprint = new ConcurrentHashMap<>();

public void addView(Long postId, Long userId, String ip) {
    String key = postId + ":" + userId + ":" + ip;
    Long lastView = viewFingerprint.get(key);
    long now = System.currentTimeMillis();

    if (lastView == null || now - lastView > 60 * 1000) {  // 1 分钟内不重复计数
        blogPostMapper.incrementViewCount(postId);
        viewFingerprint.put(key, now);
    }
}
```

⚠️ ConcurrentHashMap 会无限增长，需要定期清理（项目有定时任务清理过期 key）。

---

## 17.6 异步处理

### 17.6.1 通知发送异步化

用户点赞文章时，需要给作者发通知。如果同步发，主流程就被拖慢：

```java
// 同步（不好）
public void likePost(Long postId, Long userId) {
    blogLikeMapper.insert(new BlogLike(userId, postId));
    notificationService.sendLikeNotification(postId, userId);  // 阻塞！
}
```

本项目用 `@TransactionalEventListener` 异步处理：

```java
@Service
public class BlogLikeService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void likePost(Long postId, Long userId) {
        blogLikeMapper.insert(new BlogLike(userId, postId));
        // 发布事件（事务提交后才会触发）
        eventPublisher.publishEvent(new LikeEvent(postId, userId));
    }
}

@Component
public class NotificationListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLike(LikeEvent event) {
        notificationService.sendLikeNotification(event.getPostId(), event.getUserId());
    }
}
```

**两个关键点**：

- `AFTER_COMMIT`：事务提交后才发通知。否则点赞插入失败时，通知却已经发了。
- `@Async`：放进线程池异步执行，不阻塞主流程。

记得在 main 类加 `@EnableAsync`。

---

## 17.7 锁的优化

### 17.7.1 细粒度锁

如果用全局锁，所有用户的所有点赞操作排队执行 —— 性能灾难。本项目按 `postId` 分锁：

```java
private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

private ReentrantLock getLock(Long postId) {
    return lockMap.computeIfAbsent(postId, k -> new ReentrantLock());
}

public void likePost(Long postId, Long userId) {
    ReentrantLock lock = getLock(postId);
    lock.lock();
    try {
        // 临界区
    } finally {
        lock.unlock();
    }
}
```

不同文章的并发互不影响。

### 17.7.2 防锁内存泄漏

ConcurrentHashMap 越来越大怎么办？v1.20 加了清理逻辑：每次 `getLock` 时检查锁是否长期空闲，空闲就清掉。或者简单粗暴：用 Guava 的 `Striped<Lock>`，固定数量的锁随机映射。

---

## 17.8 批量操作

```java
// 反面：一条一条插入
for (Long tagId : tagIds) {
    blogPostTagMapper.insert(new BlogPostTag(postId, tagId));
}
// 10 个标签 = 10 条 SQL，10 次网络往返

// 正面：批量插入
blogPostTagMapper.batchInsert(postId, tagIds);
```

```xml
<insert id="batchInsert">
    INSERT INTO blog_post_tag (post_id, tag_id) VALUES
    <foreach collection="tagIds" item="tagId" separator=",">
        (#{postId}, #{tagId})
    </foreach>
</insert>
```

一条 SQL 解决，性能提升数十倍。

---

## 17.9 只读事务的好处

```java
@Transactional(readOnly = true)
public BlogPost getPostById(Long postId) {
    return blogPostMapper.selectById(postId);
}
```

`readOnly = true` 告诉数据库和 Spring："这是只读，不要做脏检查、不要锁行"。MySQL 可以路由到从库。

⚠️ 写操作必须用 `@Transactional(rollbackFor = Exception.class)` 保证异常时回滚（默认只回滚 RuntimeException，但加 `rollbackFor = Exception.class` 更稳）。

---

### 第 17 章 实战清单

- [ ] 高频查询字段建索引
- [ ] 列表查询不带 select *
- [ ] 关联查询警惕 N+1
- [ ] 分页用 MyBatis Plus 的 Page
- [ ] 计数器用 SQL `+1` 而不是 read-modify-write
- [ ] 通知/邮件用 `@Async` + `@TransactionalEventListener`
- [ ] 锁尽量细粒度
- [ ] 批量插入用 foreach
- [ ] 只读方法加 `readOnly = true`

---

# 第 18 章 测试与调试

## 18.1 三层测试金字塔

```
        /\
       /E2E\          少量端到端测试（启动整个应用，模拟真实请求）
      /----\
     /  集成  \       中等量集成测试（多个组件配合）
    /---------\
   /   单元测试  \    大量单元测试（单个类/方法）
  /-------------\
```

| 类型 | 速度 | 覆盖范围 | 写多少 |
|------|------|----------|--------|
| 单元测试 | 毫秒级 | 单个方法 | 70% |
| 集成测试 | 秒级 | 多个组件 | 20% |
| 端到端测试 | 分钟级 | 整个系统 | 10% |

## 18.2 Spring Boot Test 基础

本项目自带的测试类 `EduProjectApplicationTests.java`：

```java
@SpringBootTest
class EduProjectApplicationTests {
    @Test
    void contextLoads() {
        // 这一个测试，能验证整个 Spring 容器启动正常
    }
}
```

`@SpringBootTest` 会启动整个 Spring 上下文，加载所有 Bean。这是"集成测试"的入门级。

### 18.2.1 模拟 HTTP 请求：MockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPostList_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/post/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

`MockMvc` 不会真起 HTTP 服务器，但能模拟整个 Spring MVC 流程。

## 18.3 单元测试 JUnit 5

```java
class HtmlSanitizerTest {

    private HtmlSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new HtmlSanitizer();
    }

    @Test
    void shouldRemoveScriptTag() {
        String dirty = "<p>hello</p><script>alert(1)</script>";
        String clean = sanitizer.sanitizeRichText(dirty);
        assertFalse(clean.contains("<script>"));
        assertTrue(clean.contains("<p>hello</p>"));
    }

    @Test
    void shouldHandleNull() {
        assertNull(sanitizer.sanitizeRichText(null));
    }
}
```

| 注解 | 作用 |
|------|------|
| `@Test` | 标记测试方法 |
| `@BeforeEach` | 每个测试前执行（初始化） |
| `@AfterEach` | 每个测试后执行（清理） |
| `@BeforeAll` | 整个测试类前执行一次 |
| `@DisplayName` | 给测试起人类可读名字 |

常用断言：

```java
assertEquals(expected, actual);
assertTrue(condition);
assertNull(value);
assertThrows(BusinessException.class, () -> service.somethingThatThrows());
```

## 18.4 接口测试方式

### 18.4.1 Knife4j

本项目集成了 Knife4j，访问 `http://localhost:8825/api/doc.html` 即可看到全部接口文档。**点击"调试"就能直接发请求**，不需要 Postman。

> 这是给团队、给前端、给测试的"自助点餐机"。

### 18.4.2 Postman

适合：

- 团队共享一套接口集合
- 写自动化测试用例
- 设置环境变量（dev / prod 切换）

### 18.4.3 curl

最快的命令行工具：

```bash
# 登录
curl -X POST http://localhost:8825/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'

# 带 token 访问
curl http://localhost:8825/api/post/list \
     -H "Authorization: Bearer eyJhbGc..."
```

## 18.5 调试技巧

### 18.5.1 IDEA 断点调试

1. 在代码行号旁点一下，红点出现 = 普通断点
2. 用 Debug 方式启动（小蜘蛛图标）
3. 请求触发后，程序停在断点，可以一步步看变量
4. F8 = 单步执行下一行；F7 = 进入方法内部；F9 = 继续直到下一个断点

**条件断点**：右键断点设置 `userId == 1L`，只有满足条件才停。

### 18.5.2 日志：SLF4J + Logback

Spring Boot 默认就用 SLF4J + Logback。本项目规范：

```java
private static final Logger log = LoggerFactory.getLogger(MyService.class);
// 或者用 Lombok：
@Slf4j

log.debug("调试信息: {}", obj);    // 开发环境打印
log.info("用户 {} 登录成功", username);
log.warn("用户 {} 登录失败次数: {}", username, count);
log.error("数据库异常", e);  // 异常对象单独传，会打印堆栈
```

⚠️ **必须用 `{}` 占位符而不是字符串拼接**：

```java
// 不好（即使 debug 关闭了，字符串也会被拼接）
log.debug("用户 " + user.toJson() + " 登录");

// 好（debug 关闭时根本不会执行 toJson()）
log.debug("用户 {} 登录", user);
```

### 18.5.3 日志级别

| 级别 | 用途 |
|------|------|
| TRACE | 最详细，几乎不用 |
| DEBUG | 开发调试用 |
| INFO | 正常业务流程关键节点 |
| WARN | 可疑但还能继续运行 |
| ERROR | 出错了，必须排查 |

生产环境通常 INFO 级别。

## 18.6 数据库调试

### 18.6.1 开启 SQL 日志

`application.yml` 配置：

```yaml
mybatis-plus:
  configuration:
    log-impl: ${MYBATIS_SQL_LOG:org.apache.ibatis.logging.stdout.StdOutImpl}
```

通过 `.env` 切换：

```
# 开启
MYBATIS_SQL_LOG=org.apache.ibatis.logging.stdout.StdOutImpl
# 关闭（生产）
MYBATIS_SQL_LOG=org.apache.ibatis.logging.nologging.NoLoggingImpl
```

开启后控制台会打出每一条 SQL + 参数 + 耗时，是排查 N+1 和慢查询的神器。

### 18.6.2 查看慢查询

MySQL 自身有慢查询日志：

```sql
SHOW VARIABLES LIKE 'slow_query_log%';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 超过 1 秒就记录
```

之后用 `EXPLAIN` 分析慢 SQL：

```sql
EXPLAIN SELECT * FROM blog_post WHERE user_id = 5;
```

重点看 `type`（最好是 ref/range，最差是 ALL）和 `key`（用了哪个索引）。

## 18.7 常见 Bug 排查

### 18.7.1 启动报错

| 报错 | 原因 | 解决 |
|------|------|------|
| `Port 8825 already in use` | 端口被占 | 改 SERVER_PORT 或杀进程 |
| `Failed to configure DataSource` | DB_PASSWORD 为空 | 检查 .env |
| `JWT secret cannot be null` | JWT_SECRET 没配 | 检查 .env |
| `ClassNotFoundException` | 依赖冲突 | mvn dependency:tree 查重复 |

### 18.7.2 运行时

- **NullPointerException**：用 IDEA 的"NullAway"插件预防，或用 Optional
- **SQLSyntaxErrorException**：往往是字段名写错、SQL 关键字冲突（如 `order` 是关键字）
- **DuplicateKeyException**：唯一索引冲突，正常处理（捕获 + 友好提示）

### 18.7.3 JWT 401 排查

按以下顺序检查：

1. 请求头是否带了 `Authorization: Bearer xxx`
2. token 是否过期（解码 https://jwt.io 看 exp）
3. token 是否在黑名单（用户登出过）
4. JWT_SECRET 是否变了（变了所有旧 token 都失效）
5. 时钟是否同步（服务器时间偏差大会判定过期）

---

### 第 18 章 实战清单

- [ ] 至少为核心 Service 写单元测试
- [ ] 用 Knife4j 走通每个接口
- [ ] 日志全部用 `{}` 占位符
- [ ] 生产关闭 SQL 日志
- [ ] 学会用 EXPLAIN 看执行计划
- [ ] 常用快捷键：F8 单步、F7 进入

---

# 第 19 章 部署上线

## 19.1 打包流程

在项目根目录执行：

```bash
mvn clean package
```

完成后会在 `target/` 目录下生成：

```
target/
└── edu_project-0.0.1-SNAPSHOT.jar  # 这就是部署用的包
```

这个 jar 是 Spring Boot 的 "fat jar"，包含了所有依赖 + 内嵌 Tomcat，单文件就能跑。

如果想跳过测试加速：

```bash
mvn clean package -DskipTests
```

## 19.2 服务器准备（Linux）

### 19.2.1 安装 JDK 21

Ubuntu / Debian：

```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version  # 验证
```

CentOS / RHEL：

```bash
sudo yum install java-21-openjdk-devel
```

### 19.2.2 安装 MySQL 8

```bash
sudo apt install mysql-server-8.0
sudo systemctl start mysql
sudo mysql_secure_installation
```

### 19.2.3 创建数据库

```sql
CREATE DATABASE campus_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'campus_blog'@'%' IDENTIFIED BY 'YourStrongPassword2026!';
GRANT ALL PRIVILEGES ON campus_blog.* TO 'campus_blog'@'%';
FLUSH PRIVILEGES;
```

然后导入项目自带的"数据库表.sql"建表。

## 19.3 环境变量配置

把 `.env.example` 复制为 `.env`：

```bash
cp .env.example .env
nano .env
```

生产环境必须修改的项：

```bash
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=campus_blog
DB_USERNAME=campus_blog
DB_PASSWORD=YourStrongPassword2026!         # 强密码！
JWT_SECRET=A_Very_Long_Random_String_64_chars_or_more_DON_T_use_default
JWT_EXPIRATION=86400000                      # 24 小时
JWT_REFRESH_EXPIRATION=604800000             # 7 天
SERVER_PORT=8825
CORS_ALLOWED_ORIGINS=https://campus-blog.com  # 具体域名！不要 *
```

⚠️ **铁律**：

- `.env` 必须在 `.gitignore` 中，永远不能提交
- JWT_SECRET 用 `openssl rand -base64 64` 生成
- 数据库密码必须强密码

## 19.4 启动应用

### 19.4.1 nohup 启动（简单）

```bash
nohup java -jar edu_project-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

参数解释：

- `nohup`：忽略挂起信号，关闭 ssh 后程序仍然跑
- `>`：标准输出重定向到 app.log
- `2>&1`：错误流也写到 app.log
- `&`：后台运行

查看日志：`tail -f app.log`
停止程序：`ps -ef | grep edu_project` 找到 PID 后 `kill <PID>`

### 19.4.2 systemd 托管（推荐）

创建 `/etc/systemd/system/campus-blog.service`：

```ini
[Unit]
Description=Campus Blog Forum
After=network.target mysql.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/campus-blog
EnvironmentFile=/opt/campus-blog/.env
ExecStart=/usr/bin/java -Xmx512m -jar /opt/campus-blog/edu_project-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=5
StandardOutput=append:/var/log/campus-blog/app.log
StandardError=append:/var/log/campus-blog/error.log

[Install]
WantedBy=multi-user.target
```

启用：

```bash
sudo systemctl daemon-reload
sudo systemctl enable campus-blog
sudo systemctl start campus-blog
sudo systemctl status campus-blog
```

systemd 优势：开机自启、崩溃自动重启、集中管理日志。

## 19.5 反向代理：Nginx

让用户访问 80/443 端口，由 Nginx 转发到 8825：

```nginx
server {
    listen 80;
    server_name campus-blog.com www.campus-blog.com;

    # HTTP 跳 HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name campus-blog.com;

    ssl_certificate     /etc/letsencrypt/live/campus-blog.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/campus-blog.com/privkey.pem;

    # 前端静态文件
    location / {
        root /var/www/campus-blog-frontend;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API
    location /api/ {
        proxy_pass         http://127.0.0.1:8825/api/;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;

        client_max_body_size 500m;  # 大文件上传
    }
}
```

### 19.5.1 HTTPS 证书

用 Let's Encrypt 免费证书：

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d campus-blog.com -d www.campus-blog.com
```

自动续期：

```bash
sudo certbot renew --dry-run
```

## 19.6 跨域配置（CORS）

本项目 CORS 通过环境变量配置：

```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:8080,http://127.0.0.1:8080}
```

⚠️ **生产环境绝对不能用 `*` 通配符**：

```bash
# 错误
CORS_ALLOWED_ORIGINS=*

# 正确
CORS_ALLOWED_ORIGINS=https://campus-blog.com,https://www.campus-blog.com
```

带 Cookie 的请求（`credentials: true`）配合 `*` 是浏览器禁止的；安全角度也禁止任意网站调你的接口。

## 19.7 监控与日志

### 19.7.1 日志切割

让 Logback 自动按日期切割：

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/app.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/app-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>30</maxHistory>           <!-- 保留 30 天 -->
        <totalSizeCap>10GB</totalSizeCap>     <!-- 总大小上限 -->
    </rollingPolicy>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger - %msg%n</pattern>
    </encoder>
</appender>
```

### 19.7.2 健康检查

加入依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

访问 `http://localhost:8825/api/actuator/health` 返回：

```json
{"status": "UP"}
```

可以接入监控系统（Prometheus + Grafana），自动发现服务挂掉。

## 19.8 数据库备份

每日全备份脚本 `backup.sh`：

```bash
#!/bin/bash
DATE=$(date +%Y%m%d)
BACKUP_DIR=/var/backup/mysql
mkdir -p $BACKUP_DIR

mysqldump -u campus_blog -p'YourStrongPassword2026!' campus_blog \
          --single-transaction \
          --routines \
          | gzip > $BACKUP_DIR/campus_blog_$DATE.sql.gz

# 保留最近 30 天
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
```

加入 crontab 每天凌晨 3 点跑：

```cron
0 3 * * * /opt/scripts/backup.sh
```

## 19.9 版本升级流程

1. 本地拉新代码、测试通过
2. `mvn clean package -DskipTests`
3. scp 把 jar 上传到服务器
4. `sudo systemctl stop campus-blog`
5. 备份旧 jar：`mv app.jar app.jar.bak`
6. 替换新 jar
7. `sudo systemctl start campus-blog`
8. `tail -f /var/log/campus-blog/app.log` 确认启动成功
9. 用 curl 跑下健康检查

⚠️ **重要：升级前一定先备份数据库**。如果新版本改了表结构，回滚需要数据库也能回。

---

## 19.10 Docker 容器化部署

项目支持 Docker 部署，适合小型项目快速上线。

### Dockerfile（项目根目录）

```dockerfile
# 阶段一：构建
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段二：运行
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/edu_project-*.jar app.jar

# 变量由 docker-compose 的 .env 注入
ENV JWT_SECRET=placeholder
ENV DB_HOST=db
ENV SERVER_PORT=8825

EXPOSE 8825
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  db:
    image: mysql:8.0
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    ports:
      - "3306:3306"
    networks:
      - blog-net

  app:
    build: .
    restart: always
    depends_on:
      db:
        condition: service_healthy
    env_file:
      - .env
    environment:
      DB_HOST: db
      DB_PORT: 3306
      DB_NAME: ${DB_NAME}
      DB_USERNAME: root
      DB_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    ports:
      - "8825:8825"
    networks:
      - blog-net

volumes:
  mysql_data:

networks:
  blog-net:
    driver: bridge
```

### 部署步骤

```bash
# 1. 复制环境变量模板
cp .env.example .env
# 编辑 .env，填入真实密码（生产环境务必修改）

# 2. 启动
docker compose up -d --build

# 3. 查看日志
docker compose logs -f app

# 4. 健康检查
curl http://localhost:8825/api/doc.html

# 5. 停止
docker compose down
```

⚠️ 生产环境使用 Docker 部署时，建议：
- `.env` 不要提交到 Git（已在 `.gitignore` 中）
- MySQL 数据通过 `volumes` 持久化，防止容器重启丢数据
- 考虑使用 Docker Swarm 或 Kubernetes 做多节点部署

---

### 第 19 章 实战清单

- [ ] mvn clean package 打包成功
- [ ] 服务器装好 JDK 21 + MySQL 8
- [ ] .env 配好且不泄露
- [ ] systemd 托管应用
- [ ] Nginx + HTTPS 反向代理
- [ ] CORS 限定具体域名
- [ ] 日志切割
- [ ] 数据库每日备份
- [ ] 升级前备份

---

# 附录 A：常见问题 FAQ

### Q1：启动报错 `JWT secret cannot be null`

**原因**：`.env` 文件没创建，或者没配 `JWT_SECRET`。

**解决**：

```bash
cp .env.example .env
# 编辑 .env，填入 JWT_SECRET（至少 32 字符）
```

如果用 IDEA 启动还是报错，可能是 IDEA 没读取 .env。`DotenvConfig.load()` 会自动从 `user.dir` 找 .env，确认 IDEA 的 Working Directory 设的是项目根目录。

### Q2：Lombok 不生效，`@Slf4j` 报错找不到 log

**原因**：IDEA 没装 Lombok 插件，或没开 annotation processing。

**解决**：

1. IDEA → Settings → Plugins，搜 Lombok 安装
2. Settings → Build → Compiler → Annotation Processors → Enable

### Q3：Connection refused，连不上数据库

按顺序排查：

1. MySQL 服务起没起：`systemctl status mysql`
2. 防火墙拦没拦：3306 端口
3. MySQL 监听地址：`bind-address` 改成 0.0.0.0
4. 用户授权：`GRANT ALL ON campus_blog.* TO 'campus_blog'@'%';`
5. .env 里 DB_HOST/DB_PORT/DB_USERNAME/DB_PASSWORD 是否对
6. 用 `mysql -h IP -P 3306 -u campus_blog -p` 在命令行试连

### Q4：Token expired

**正常情况**：默认 access token 24 小时过期。

**解决方案**：

- 调用 `/api/auth/refresh` 刷新（前端拦截 401 自动刷）
- 或重新登录

### Q5：上传大文件报 413 Request Entity Too Large

两层限制都要改：

1. Spring Boot：`application.yml` 里 max-file-size、max-request-size
2. Nginx：`client_max_body_size 500m;`

### Q6：跨域报错 CORS

错误信息形如 `No 'Access-Control-Allow-Origin' header is present`。

**排查**：

1. 后端是否配置了 CorsFilter（项目自带）
2. `CORS_ALLOWED_ORIGINS` 是否包含前端域名
3. 是否用了 `*` 配合 `withCredentials`（不允许）
4. 预检请求 OPTIONS 是否通过：浏览器 Network 面板看

---

# 附录 B：项目术语表

| 术语 | 全称 | 通俗解释 |
|------|------|----------|
| **IoC** | Inversion of Control | 控制反转：对象不再自己 new 依赖，由 Spring 容器注入 |
| **DI** | Dependency Injection | 依赖注入：IoC 的具体实现方式（@Autowired） |
| **AOP** | Aspect-Oriented Programming | 面向切面编程：把日志、事务等横切关注点抽出来 |
| **ORM** | Object-Relational Mapping | 对象关系映射：实体类 ↔ 数据库表 |
| **JWT** | JSON Web Token | 用 JSON + 签名表示的 Token，自包含，前后端分离常用 |
| **BCrypt** | - | 一种慢哈希算法，专为密码存储设计，自动加盐 |
| **CSRF** | Cross-Site Request Forgery | 跨站请求伪造：诱骗用户在已登录站点发请求 |
| **XSS** | Cross-Site Scripting | 跨站脚本：注入脚本被浏览器执行 |
| **CORS** | Cross-Origin Resource Sharing | 跨源资源共享：浏览器同源策略的放行机制 |
| **CRUD** | Create/Read/Update/Delete | 增删改查 |
| **DTO** | Data Transfer Object | 数据传输对象：接口入参/出参用 |
| **VO** | View Object | 视图对象：返回前端用，不暴露内部字段 |
| **Entity** | - | 实体类：直接对应数据库表 |
| **CRUD** | Create/Read/Update/Delete | 增删改查 |
| **TOCTOU** | Time-of-check to time-of-use | 检查时与使用时之间的并发漏洞 |
| **CAS** | Compare-And-Swap | 比较并交换，无锁原子操作 |
| **CDN** | Content Delivery Network | 内容分发网络，加速静态资源 |
| **SSL/TLS** | - | HTTPS 加密协议 |
| **REST** | Representational State Transfer | 一种 API 设计风格，URL 表示资源 |
| **Bean** | - | Spring 容器管理的对象 |
| **Bootstrap** | - | Spring Boot 启动过程 |

---

# 后记：你的成长之路

恭喜！你坚持读完了这本书。从环境搭建、Java 基础、Spring Boot 核心，到安全加固、性能优化、上线部署 —— 你已经走完了一个 Java 后端开发者最重要的入门旅程。

但这只是起点。

## 接下来学什么

按推荐顺序：

1. **Redis**：缓存、分布式锁、消息队列入门、限流
2. **消息队列**：RabbitMQ / Kafka，解决削峰填谷、系统解耦
3. **分布式基础**：CAP 理论、最终一致性、分布式事务（Seata）
4. **微服务**：Spring Cloud（Nacos、OpenFeign、Sentinel、Gateway）
5. **容器化**：Docker、Docker Compose、Kubernetes
6. **DevOps**：Jenkins / GitHub Actions 自动化部署
7. **数据库进阶**：分库分表（ShardingSphere）、读写分离
8. **JVM 调优**：垃圾回收、内存模型、性能诊断（Arthas、JProfiler）

## 推荐学习资源

- **Spring 官方文档**：https://spring.io/projects/spring-boot —— 任何中文教程都比不上官方文档权威
- **Spring Boot 示例**：https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples
- **Awesome Java**：https://github.com/akullpp/awesome-java —— 整理了海量优秀 Java 库
- **OWASP**：https://owasp.org/ —— 安全方面的圣经
- **MySQL 实战 45 讲**（极客时间）—— 把 MySQL 真正讲透了
- **GitHub Trending**：每天去看 Java 区，跟最新潮流

## 一些过来人的话

- **不要囤教程**：买了 100 个课程不如把 1 个项目做完。本书的项目就是最好的练习对象，把它跑起来、改起来、加新功能。
- **拥抱开源**：在 GitHub 上读优秀项目源码（如 mall、若依、jeecg-boot），看看大公司怎么写代码。
- **多写、多改、多重构**：第一个版本永远是丑的。看到 v1.10 → v1.34 一路修复痕迹了吗？真实项目就是这样长大的。
- **保持好奇**：碰到不懂的报错，别只复制百度，看完报错的英文 stack trace 和官方文档，你会比 90% 的人都强。
- **别怕错**：你写的每一行 bug，都是未来不再写 bug 的资本。

最后送你一句话，是本项目 `CLAUDE.md` 里的"开发荣耻"，希望你也能在工作中践行：

> 以瞎清接口为耻，以认真查询为荣。
> 以模糊执行为耻，以寻求确认为荣。
> 以创造接口为耻，以复用现有为荣。
> 以跳过验证为耻，以主动测试为荣。
> 以破坏架构为耻，以遵循规范为荣。
> 以假装理解为耻，以诚实无知为荣。
> 以盲目修改为耻，以谨慎重构为荣。
> 以忘记更新文档为耻，以及时更新文档为荣。

—— 祝你在 Java 的世界里走得长远，写出让自己骄傲的代码。

**全书完。**
