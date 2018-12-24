# little-rpc

#### 项目介绍
轻型rpc实现,基于netty
支持断线重连(netty实现)
支持负载均衡(参考dubbo):直连,随机,轮询,加权随机,加权轮询
支持多种序列化:fst,hessian,hessian2,jdk,jackson,kryo,protostuff
#### 软件架构
软件架构说明
服务发现:zookeeper
网络通信:netty

一次请求流程:
client->服务提供者列表->路由->构造请求对象>序列化->传输->服务端接受,反序列化,反射调用->构造返回对象,序列化->传输到客户端->客户端反序列化->获取结果->请求结束


#### 无注册中心模式
```

//server端配置
	@Bean(destroyMethod="stop")
	public RpcSpringProviderHandler config() throws Exception {
		RpcSpringProviderHandler rpcSpringProviderHandler=new RpcSpringProviderHandler();
		//设置序列化对象
		rpcSpringProviderHandler.setSerializerName(String.valueOf(SerializerEnum.FST));
		//设置端口
		rpcSpringProviderHandler.setPort(8081);
		//服务端多网卡时指定网卡前缀人如:192.168.10
		rpcSpringProviderHandler.setNetPrefix(netPrefix);
		//设置服务端ip:NetPrefix 设置时无需设置
		rpcSpringProviderHandler.setIp(ip);
		return rpcSpringProviderHandler;
	}


//服务实现类
@RpcService(version="1",weight=100,token="123456")
@Component
public class MathServiceImpl implements MathService {

	@Override
	public Integer add(Integer a, Integer b) {
		return a+b;
	}
	}


client 端配置:
	service: 服务接口
	version: 服务版本
	route: 负载均衡方式,参见RouteEnum,默认 RoundRoute
	可选:DirectRoute(直连),RandomRoute(随机),RoundRoute(轮询),
	RandomWeightRoute(加权随机),RoundWeightRoute(加权轮询),
	用户亦可自定义负载均衡实现类:全限定类名 如: com.taoyuanx.littlerpc.route.xxxRoute 继承抽象Route类
	timeout :超时时间
	token: token
	weight: 权重   英文','分割
	serializer: 序列化方式,参见SerializerEnum,默认HESSIAN2    英文','分割
	可选:FST,HESSIAN,HESSIAN2,JACKSON,JDK,KRYO,PROTOSTUFF
	用户亦可自定义序列化实现类:全限定类名,如: com.taoyuanx.littlerpc.ser.xxxSer 实现Serializer接口
	address:服务端ip:port    英文','分割 如 192.168.30.100:8888,192.168.30.101:8889
	
示例配置:
<bean  class="com.taoyuanx.littlerpc.client.TargetBeanFactory" >
<property name="service" value="com.taoyuanx.demo.service.DemoService"/>
<property name="address" value="127.0.0.1:8888"/>
<property name="serializer" value="FST"/>
<property name="version" value="1"/>
<property name="token" value="123456"/>
</bean>


使用:
@Autowired
MathService mathService;
```
#### zookeeper注册中心模式

```

//server端配置
	@Bean(destroyMethod="stop")
	public RpcSpringProviderHandler config() throws Exception {
		RpcSpringProviderHandler rpcSpringProviderHandler=new RpcSpringProviderHandler();
		Map<String, Object> param=new HashMap<>();
		param.put(ZkServiceRegistry.SESSIONTIMEOUT, 10000);
		param.put(ZkServiceRegistry.ZKADDRESS, "192.168.91.201:2181");
		rpcSpringProviderHandler.setServiceRegistryClass(ZkServiceRegistry.class);
		rpcSpringProviderHandler.setServiceRegistryParam(param);
		return rpcSpringProviderHandler;
	}


//服务实现类
@RpcService(version="1",weight=100,token="123456")
@Component
public class MathServiceImpl implements MathService {

	@Override
	public Integer add(Integer a, Integer b) {
		return a+b;
	}
	}



client 端配置:

	@Bean
	public SpringClientHandler config() throws Exception {
		SpringClientHandler clientHandler = new SpringClientHandler();
		//设置调用工厂
		InvokerFactory invokerFactory = InvokerFactory.getInstance();
		invokerFactory.setClientFactory(NettyClientFactory.getInstance());
		clientHandler.setInvokerFactory(invokerFactory);
		//设置 zkregistry,及其配置
		ServiceRegistry registry=new ZkServiceRegistry();
		Map<String, Object> param=new HashMap<>();
		param.put(ZkServiceRegistry.SESSIONTIMEOUT, 10000);
		param.put(ZkServiceRegistry.ZKADDRESS, "192.168.91.201:2181");
		param.put(ZkServiceRegistry.INVOKERFACTORY, invokerFactory);
		clientHandler.setRegistry(registry);
		clientHandler.setRegistryParam(param);
		return clientHandler;
	}

使用:
@RpcReference(version="1", route="RoundRoute",timeout=1000)
MathService mathService;
	version: 服务版本
	route: 负载均衡方式,参见RouteEnum,默认 RoundRoute
	可选:DirectRoute(直连),RandomRoute(随机),RoundRoute(轮询),
	RandomWeightRoute(加权随机),RoundWeightRoute(加权轮询),
	用户亦可自定义负载均衡实现类:全限定类名 如: com.taoyuanx.littlerpc.route.xxxRoute 继承抽象Route类
	timeout :超时时间
```
#### 仓库地址

**git地址:**[https://github.com/dushitaoyuan/little-rpc](https://github.com/dushitaoyuan/little-rpc)



**码云地址:**[https://gitee.com/taoyuanx/little-rpc](https://gitee.com/taoyuanx/little-rpc) 
