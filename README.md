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

2. 加上字段name检索，只需要添加注解 @SearchDb，搜索类UserSearch，UserSearch必须继承接口SearchBean

```
@GetMapping("/list")
@ApiOperation(value = "获取用户列表")
@SearchDb
public Object orderList(UserSearch userSearch) {
    return iUserService.list();
}
```

> @SearchParam参数：

> column: 数据库需要检索字段，不写默认变量名下划线形式userName->user_name

> symbol: 检索类型：=、like、in、between and。目前这几种，后期加别的

```
@Data
public class UserSearch implements SearchBean {
   @SearchParam(column = "name",symbol = SearchParamEnum.like)
   private String username;
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