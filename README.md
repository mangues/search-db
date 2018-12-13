## 源码地址
https://github.com/mangues/search-db

## 1.0.0版本
1. 处理一些已知bug
2. 增加代码层对返回数据填充字典表信息的 dictSearchHandler，解决分页等封装类的返回值处理

## 0.0.3版本:
1. @DictSearch 删除 合并到 @SearchDb上
2. 支持controller 参数检索注解


## 安装方式

>具体案例可以查看 demo分支


### maven
```
<dependency>
  <groupId>top.mangues</groupId>
  <artifactId>searchdb-spring-boot-starter</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```

### Gradle
```
compile 'top.mangues:searchdb-spring-boot-starter:1.0.0-RELEASE'
```


### 配置，mybatis 插件

mybatis-config.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <plugins>
        <!-- sqldb拦截器 -->
        <plugin interceptor="com.mangues.searchdb.mybatis.SearchInterceptor">
            <property name="dialect" value="mysql"/>
        </plugin>

    </plugins>
</configuration>
```


或者 springboot
```
    @Bean
    public SearchInterceptor searchInterceptor() {
        SearchInterceptor searchInterceptor = new SearchInterceptor();
        return searchInterceptor;
    }
```


## 使用方法

#### 以下图片随机网上截取

![image](https://github.com/mangues/search-db/raw/master/image/WX20181124-220832@2x.png)


### 一、简单使用

user表
```
id	name	password
1	张三	123456
2	李思	123456
```


1. 正常查询所有user数据
```
@GetMapping("/list")
@ApiOperation(value = "获取用户列表")
public Object orderList() {
    return iUserService.list();
}
```

2. 加上字段name检索，只需要添加注解 @SearchDb，搜索类UserSearch，UserSearch必须继承接口SearchBean，
    或者参数加上注解@SearchParam(column="必须指定")

```
@GetMapping("/list")
@ApiOperation(value = "获取用户列表")
@SearchDb
public Object orderList(UserSearch userSearch,@RequestParam @SearchParam(column = "name",symbol = SearchParamEnum.like) String username) {
    return iUserService.list();
}
```

> @SearchParam参数：

> column: 数据库需要检索字段，不写默认变量名下划线形式userName->user_name

> symbol: 检索类型：=、like、in、between and。目前这几种，后期加别的

```
@Data
public class UserSearch implements SearchBean {
   @SearchParam(column = "password",symbol = SearchParamEnum.like)
   private String password;
}

```


### 二、其他使用

#### 1. 检索时间格式化
order_info 表

```
id	user_id	order_num	create_at	    state
1	1	ED123456	2018-12-19 22:23:23	-1
2	2	ED234566	2018-12-20 22:23:23	2
3	1	DF345677	2018-12-21 22:23:23	-2
```


> 数据库格式是 2018-12-19 22:23:23 形式，可是查询需要按日查询

dateFormat 后接数据库 FORMAT_DATE 函数的格式化 字符串

```
@SearchParam(column = "create_at",symbol = SearchParamEnum.between_and,dateFormat = "%Y-%m-%d")
@ApiModelProperty(value = "订单时间范围 2018-12-11,2018-12-24")
private String createDate;
```

#### 2.between and 用法

必须字符串参数，"," 分隔前后两个检索参数
```
@SearchParam(column = "create_at",symbol = SearchParamEnum.between_and,dateFormat = "%Y-%m-%d")
@ApiModelProperty(value = "订单时间范围 2018-12-11,2018-12-24")
private String createDate;
```

#### 3.枚举变量检索支持

必须实现 Enum 接口的string 方法，返回值就是对应的枚举在数据库中的存储值，此处是code

```
public enum OrderStateEnum implements Enum {
    REFUND(-2,"已经退单"),
    NOFINISH(-1,"订单未完成锁定中"),
    NOSETTLEMWNT(0,"未结算"),
    SETTLEMWNT(1,"已结算");
    Integer code;
    String msg;

    private OrderStateEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public String string() {
        return code+"";
    }

```


### 3.外键查询
@SearchDb

> 自动查询填充主表外键所对应的从表数据
```
 比如 select * from order_info 结果集中的user_id 是外键，此注解可以自动完善user_id 对应的user数据
```

例子：

@DictSearch开启功能，resultClass 对应返回list 或者 object 类型
```
@GetMapping("/list")
@ApiOperation(value = "获取订单列表")
@SearchDb(resultClass = OrderInfo.class)
public Object orderList(OrderSearch orderSearch) {
    return orderInfoService.list();
}
```

或者利用DictSearchHandler 解决分页数据返回值不是List 或者 Object类型，是其他封装类。

```
 @Autowired
 private DictSearchHandler dictSearchHandler;

@GetMapping("/list")
@ApiOperation(value = "获取订单列表")
public Object orderList(OrderSearch orderSearch) {
    Page<Order> page = new PageFactory<Order>().pageFactory();
    Page pageInfo = companyRobotService.selectPage(page, null);
    Object wrap = dictSearchHandler.wrap(pageInfo.getRecords(), Order.class);
    pageInfo.setRecords((List)wrap);
    return pageInfo;
}
```

@DictParam描述外键所需要的配置

1. dictTable：从表
2. columns: 所要显示的从表字段
3. dictId：从表外键字段 默认id


```
public class OrderInfo
    @DictParam(dictTable = "user",columns = {"name","password"},dictId="id")
    private Integer userId;
    ...
}
```

结果：  自动填充的 userIdDictMap
```
[
  {
    "userIdDictMap": {
      "password": "123456",
      "name": "张三",
      "id": 1
    },
    "orderNum": "ED123456",
    "state": -1
    "userId": 1,
    "createAt": "2018-12-19T22:23:23"
  },
]
```







### 四、增强@SearchParam外键检索


> 支持外键检索
```
 比如 select * from order_info 只能检索user_id字段，
 此注解可以利用 user_id 对应的从表 user 中的字段进行检索
```

例子
```
 @SearchParam(column = "name",symbol = SearchParamEnum.like,
                isDictColumn = true,dictColumn = "user_id",dictTable = "user")
 private String userName;
```
增加：
1. isDictColumn 此字段否外键
2. dictColumn： 对应的主表外键字段
3. dictTable:   从表
