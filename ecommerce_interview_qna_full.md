# 电商系统面试题全集

## 第一类：基础认知题（必会）
1. 项目整体架构
Q1: 请描述这个电商系统的整体架构，包括前后端、数据库的分层设计
A: 三层架构：Controller（接收请求校验）、Service（核心业务逻辑处理）、Repository（数据持久化）。数据库采用MySQL存储结构化数据，可能引入Redis做缓存。前后端分离设计。

Q2: 项目使用了哪些主要技术栈？各有什么作用？
A: Spring Boot（快速开发基础框架）、Spring Data JPA（持久层ORM映射）、MySQL（主存储）、Redis（缓存）、TypeScript/CSS/HTML（前端UI）。

Q3: 项目中数据流是怎样流转的？以一个用户下单为例说明
A: 前端发送请求 -> Controller 接收并调用 Service -> Service 校验库存、生成订单、使用 Repository 落库 -> 异步发送消息/触发后续流程 -> 返回结果。

2. Spring Boot 基础
Q4: Spring Boot 的核心特性有哪些？为什么选择它？
A: 自动装配、起步依赖、内嵌Tomcat。选择它可以大幅度减少配置样板代码。

Q5: 什么是依赖注入？你在这个项目中是如何使用的？
A: IOC的一部分，由Spring容器负责对象的创建和装配。项目中通常通过构造器或@Autowired将Service注入Controller，将Repository注入Service。

Q6: @Component、@Service、@Controller、@Repository 四个注解有什么区别？
A: 本质都是@Component。@Controller处理HTTP请求；@Service处理业务逻辑；@Repository用于DAO层并转换数据库异常。

3. REST API 设计
Q7: 项目中的 REST API 如何设计的？遵循了什么规则？
A: 遵循资源导向，使用名词表示资源路径，使用HTTP动词表示操作。

Q8: HTTP 的 GET、POST、PUT、DELETE 方法各应该用在什么场景？
A: GET查询资源；POST创建资源；PUT全量更新资源；DELETE删除资源。

Q9: 一个标准的 REST API 请求和响应应该包含哪些信息？
A: 请求：URL、方法、Header、Body。响应：状态码、Header、Body（标准格式的错误码、提示信息和数据内容）。

4. 数据库基础
Q10: 关系型数据库中主键和外键的作用是什么？
A: 主键唯一标识一行记录；外键建立表与表之间的约束和关联，保证参照完整性。

Q11: 什么是 ORM？JPA 和 Hibernate 的关系是什么？
A: 对象关系映射。JPA是Java官方提供的ORM规范接口，Hibernate是JPA的具体实现。

Q12: 项目中用到的是哪种 ORM 框架？有什么优势？
A: 使用的是 Spring Data JPA 封装的 Hibernate。优势是无需手写简单SQL，提高开发效率，面向对象编程。

5. 项目业务逻辑
Q13: 电商系统的核心业务流程有哪些？
A: 商品浏览获取 -> 添加到购物车 -> 订单结算(Checkout) -> 支付与订单状态流转 -> 物流与履约。

Q14: 项目中的购物车是如何存储的？
A: 可存在Redis中（hash结构便于更新具体商品数量）或数据库中以确保持久化，或者Session/本地存储（未登录状态）。

Q15: 订单的生命周期有几个阶段？分别是什么？
A: 待支付（Created）、已支付（Paid）、已发货（Shipped）、已完成（Completed）、已取消（Cancelled/Closed）。

## 第二类：中阶理解题（重点）
6. JPA/Hibernate 深层理解
Q16: @Entity 和 @Table 注解的区别和用途是什么？
A: @Entity 标识其为一个实体类；@Table 用于指定其映射的数据库表名及索引等表级别约束。

Q17: @OneToMany 和 @ManyToOne 注解如何使用？
A: 一对多（如订单包含多个订单项）用@OneToMany；多对一（订单项对应一个订单）用@ManyToOne。

Q18: 什么是懒加载和急加载？它们的优缺点是什么？
A: 懒加载（LAZY）需要时才查询关联对象，省内存但可能引发N+1或LazyInitializationException；急加载（EAGER）查询主记录时带出关联数据，简单但可能加载无用数据影响性能。

Q19: JPA 中的 CascadeType 有哪些类型？都代表什么意思？
A: PERSIST（级联保存），MERGE（级联更新），REMOVE（级联删除），REFRESH（级联刷新），ALL（以上全部）。

7. 数据库事务与隔离级别
Q20: 什么是数据库事务的 ACID 特性？分别解释
A: Atomicity(原子性)，Consistency(一致性)，Isolation(隔离性)，Durability(持久性)。

Q21: 数据库的隔离级别有哪些？从低到高排序？
A: 读未提交(Read Uncommitted) -> 读已提交(Read Committed) -> 可重复读(Repeatable Read) -> 串行化(Serializable)。

Q22: 这个项目为什么使用 SERIALIZABLE 隔离级别？（重点题）
A: 主要是为了绝对防止高并发下的库存超卖、数据不一致问题。虽然性能有损，但能解决不可重复读和幻读。

Q23: 脏读、不可重复读、幻读分别是什么问题？
A: 脏读：读未提交的数据；不可重复读：同事务两次读某行数据不同（被改）；幻读：同事务两次范围查询记录数不同（新增/删除）。

8. 悲观锁与乐观锁
Q24: 什么是悲观锁？什么是乐观锁？各有什么优缺点？
A: 悲观锁假设并发总是冲突，靠DB锁住数据(如FOR UPDATE)；乐观锁假设不冲突，靠版本号(Version)重试。悲观锁安全但易死锁/性能低，乐观锁性能高但冲突多时重试开销大。

Q25: 项目中为什么在 checkout 时要用悲观锁？
A: 保证扣库存操作的原子性和排他性，避免多个线程同时读取同一库存余量然后扣减导致超卖。

Q26: @Lock(LockModeType.PESSIMISTIC_WRITE) 的作用是什么？
A: 在JPA中添加排他悲观写锁，生成的SQL相当于 "SELECT ... FOR UPDATE"。

Q27: 如何在 JPA 中实现乐观锁？版本号机制是什么？
A: 实体类中加一个整型字段并标注 @Version。更新时检查版本号是否匹配，匹配则更新且版本+1，否则抛出乐观锁异常。

9. @Transactional 注解深层应用
Q28: @Transactional 注解的作用是什么？
A: 声明式事务管理，保证方法内的数据库操作具有事务的ACID特性。

Q29: @Transactional 默认的属性值有哪些？
A: 传播机制为 REQUIRED，隔离级别为底层数据库默认（MySQL为RR），对RuntimeException和Error回滚。

Q30: propagation（传播机制）有哪些类型？项目中应该使用哪个？
A: REQUIRED（默认，加入或新建），REQUIRES_NEW（总新建），NESTED等。通常普通业务方法用 REQUIRED，独立审计日志用 REQUIRES_NEW。

Q31: rollbackFor 属性的作用是什么？
A: 指定哪些异常类型会导致事务回滚。默认只对运行时异常回滚，通常写为 rollbackFor = Exception.class 使其对受检异常也回滚。

Q32: 什么时候会导致事务失效？请举3个常见的例子
A: 1. 同类内方法自调用（绕过AOP代理）；2. 方法非public；3. 抛出被catch吞掉且未配置rollbackFor的异常。

10. 并发控制
Q33: 在高并发场景下，为什么库存会被超卖？
A: 典型的 "读-改-写" (Read-Modify-Write) 竞态条件。多线程读到同一个剩余值并校验通过。

Q34: 项目中如何防止库存超卖的？
A: 使用数据库悲观锁或者分布式锁。

Q35: 悲观锁相比于乐观锁，性能如何？为什么还要用？
A: 性能较差（排队），但在高并发且冲突率极高的秒杀/扣库存场景中，乐观锁会产生大量无效重试（导致CPU飙高），此时悲观锁能保证确定性。

Q36: MySQL 的 SELECT...FOR UPDATE 语句如何工作？
A: 在InnoDB下，命中索引时加行锁或间隙锁，未命中索引则可能退化为表锁。

11. 异步处理
Q37: 项目中订单履行为什么采用异步处理？
A: 提升系统吞吐量与接口响应速度。耗时的通知、物流准备放在后台异步处理。

Q38: @Async 注解如何工作？它需要什么前置条件？
A: 通过Spring AOP代理拦截，提交给线程池执行。需要 @EnableAsync 开启，且不能是同类内部调用。

Q39: 异步处理中，如何处理异常？
A: 返回 Future 的可以捕获，无返回值的可通过实现 AsyncUncaughtExceptionHandler 处理异常，或内部try-catch并记录日志/补偿。

Q40: ThreadPoolExecutor 的核心参数有哪些？各代表什么意思？
A: corePoolSize(核心线程数)，maximumPoolSize(最大线程数)，keepAliveTime(空闲存活时间)，workQueue(阻塞队列)，handler(拒绝策略)。

12. Service 层设计
Q41: 为什么要分离 Controller、Service、Repository 三层？
A: 解耦、各司其职。Controller负责暴露API，Service负责核心复用逻辑，Repository负责数据存储。

Q42: Service 层中应该放置什么样的代码？
A: 业务规则的校验、对象的转换（DTO到Entity）、调用多个Repository的组合逻辑、事务的边界控制。

Q43: 在项目的 OrderService 中，checkout 方法做了哪些核心工作？
A: 校验库存、锁定库存、计算总价、创建订单、扣除库存、保存订单记录。

## 第三类：高阶深度题（体现水平）
13. 系统设计与权衡
Q44: 假设这个项目要支持日均 100 万订单，现在的设计需要如何改进？
A: 引入微服务架构拆分、分库分表、引入Redis做库存预扣减、RabbitMQ做订单削峰填谷。

Q45: 库存超卖问题在秒杀场景下的解决方案？
A: Redis Lua脚本预扣减库存 + 消息队列异步创建订单 + 数据库最终落库。

Q46: 订单表和产品表的关系如何设计才能保证查询效率？
A: 订单快照化，即订单明细表中直接冗余保存购买时产品的名称、价格等信息，避免频繁Join。

Q47: 如何设计一个分布式锁来防止库存超卖？
A: 使用Redis的 setnx 配合过期时间，或者Redisson框架（带看门狗机制自动续期），或者使用Zookeeper的临时顺序节点。

14. 数据库性能优化
Q48: 项目中有哪些地方容易产生 N+1 查询问题？如何解决？
A: JPA一对多懒加载遍历时产生。通过在@Query中使用 "JOIN FETCH" 一次性查出，或者使用EntityGraph解决。

Q49: 为什么不同的查询会使用不同的 @Query 语句？
A: 复杂多表关联、自定义统计、覆盖索引查询或避免返回全量字段时，JPQL/Native SQL比方法名推导更灵活高效。

Q50: 在 JPA 中，如何进行分页查询？分页的原理是什么？
A: 传入 Pageable 参数，返回 Page 对象。原理是JPA底层会发起两次查询：一次查 count() 计算总数，一次通过 LIMIT offset, size 查数据。

Q51: 项目中哪些字段应该建立索引？为什么？
A: 订单号(主键索引或唯一索引)、用户ID(常用于查询某用户的订单)、创建时间(按时间范围查询)。提高查询速度。

15. 事务处理的高级问题
Q52: 在 checkout 方法中，为什么必须使用 SERIALIZABLE 隔离级别？用具体例子说明
A: 若不仅是单纯扣库存，还涉及时效性约束（例如某用户每分钟只能买一次），SERIALIZABLE能防止两次读间的插入行为（幻读）。但在单纯扣库存中也可以降级使用行锁+RR级别。

Q53: 什么是幻读？SERIALIZABLE 如何防止幻读？
A: 事务A范围查询，事务B插入并提交，事务A再次查询发现多了几条。SERIALIZABLE会施加间隙锁（Next-Key Lock）锁住范围，阻止其他事务插入。

Q54: 如果改用乐观锁实现 checkout 会怎样？优缺点对比
A: 优点是不阻塞数据库连接，系统响应更轻量；缺点是高并发下会有大量事务抛出 OptimisticLockException，需自行处理重试机制导致CPU开销大且容易失败。

Q55: 死锁是什么？项目中如何避免死锁？
A: 多个事务互相持有对方所需的锁。避免方法：规定所有事务必须按照相同的顺序访问表/行数据；尽量缩短事务持有锁的时间；避免大事务。

16. 异步与事务的交互
Q56: 订单异步处理时，如果数据库事务还未提交，后台线程读到的是什么数据？
A: 在RC/RR级别下，读不到刚生成的订单数据（不可见），会导致异步线程查不到订单而报错。

Q57: 订单履行线程如何确保数据一致性？
A: 使用 TransactionSynchronizationManager.registerSynchronization，在 `afterCommit` 钩子中再触发异步任务，或者使用事务消息（Outbox Pattern）。

Q58: 如果异步处理失败了，怎么办？如何实现重试机制？
A: Spring Retry 或基于消息队列(RabbitMQ/Kafka)的死信队列和延迟重试机制，确保最终一致性。

17. 前后端交互
Q59: 前端发送商品搜索请求后，后端是如何处理的？
A: Controller接收关键词和分页参数 -> 调用Service构建查询条件（如JPA Specification或ES查询） -> 查出结果映射为DTO返回给前端。

Q60: 项目如何处理库存实时更新后，前端展示的库存数不一致问题？
A: 允许一定的不一致（页面缓存），下单时做严格校验。如果是秒杀场景可引入WebSocket或前端定时轮询获取最新剩余量。

Q61: Swagger 文档在项目中的作用是什么？
A: 提供实时、在线的API接口文档，方便前后端分离协同开发和接口调试。

18. 测试相关
Q62: 如何为 OrderService.checkout() 方法编写单元测试？
A: 使用 JUnit5 和 Mockito。Mock ProductRepository 和 OrderRepository。验证扣库存方法是否被调用，订单对象是否按预期生成。

Q63: 在测试中如何模拟数据库事务和锁的行为？
A: 单元测试中Mock掉锁。若做集成测试，使用 @DataJpaTest 或者 Testcontainers 启动真实数据库，并使用多线程并发调用的方式测试并发安全性。

Q64: 集成测试和单元测试的区别是什么？
A: 单元测试针对单一类或方法，Mock掉外部依赖；集成测试会加载Spring上下文、连接真实/内存数据库验证组件间交互。

19. 项目扩展性设计
Q65: 如果要加入积分系统，应该如何扩展现有代码？
A: 采用事件驱动架构(Event-Driven)。Checkout成功后发布 OrderCreatedEvent，积分模块监听该事件异步增加积分。

Q66: 如果要支持多仓库配置，库存扣减逻辑如何改进？
A: 建立 [仓库-商品-库存] 维度的库存表。下单时根据用户的地理位置策略选择合适的仓库扣减，若主仓不足可能需跨仓调拨。

Q67: 如何添加用户认证（登录）功能？需要修改哪些部分？
A: 引入 Spring Security + JWT。添加用户实体、认证过滤器，配置接口权限，Controller层获取当前登录用户信息（@AuthenticationPrincipal）。

Q68: 如何记录订单的完整审计日志？
A: 利用 JPA @EntityListeners (AuditingEntityListener) 或 AOP 拦截特定方法，或 CDC（Canal）监听数据库 Binlog。

20. 常见问题排查
Q69: 如果发现订单没有正确扣减库存，可能的原因有哪些？
A: 事务未生效（AOP失效）、并发扣减时未使用任何锁机制导致丢失更新、异常被吞噬导致没走到扣减逻辑。

Q70: 如果异步订单处理线程卡死了会怎样？如何监控？
A: 导致线程池耗尽，后续异步任务被拒绝(Rejected)。可以通过 Actuator 暴露线程池指标，并结合 Prometheus + Grafana 监控队列堆积。

Q71: 如果数据库连接池耗尽会导致什么问题？
A: 接口大面积超时，HikariCP 抛出 ConnectionTimeoutException。

Q72: 什么情况下 @Transactional 的事务会回滚？
A: 默认只有在抛出 RuntimeException 和 Error 时触发。可通过 rollbackFor 改变规则。

## 第四类：代码实现题（手写相关）
21. 编码能力
Q73: 请编写一个订单查询接口，需要支持分页、排序、日期过滤
A: Controller 接收 `Pageable` 和 `Date`，Service层利用 `Specification<Order>` 拼接 WHERE 动态条件，调用 `repository.findAll(spec, pageable)`。

Q74: 实现一个库存检查和扣减的方法，保证原子性
A: `@Transactional` 结合 Repository中的 `@Modifying @Query("update Product p set p.stock = p.stock - :num where p.id = :id and p.stock >= :num")`，返回受影响行数。

Q75: 为产品搜索功能编写一个模糊查询的 @Query 语句
A: `@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")`

Q76: 编写一个使用悲观锁的库存检查方法
A:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p WHERE p.id = :id")
Product findByIdWithPessimisticLock(@Param("id") Long id);
```

Q77: 实现一个异步订单处理的方法，并添加重试逻辑
A: 使用 `@Async` 和 `@Retryable(value = Exception.class, maxAttempts = 3)` 注解标记方法。

22. 问题解决
Q78: 项目中某个用户反馈下了订单但没有扣库存，如何诊断和修复？
A: 查日志确认请求链路，检查该产品的并发下单量，检查代码中锁/事务是否失效。修复方案：加入 `@Lock` 或者使用数据库 `update ... where stock >= xxx`。

Q79: 系统在并发订单时变得很慢，可能的原因有哪些？
A: 悲观锁导致行锁竞争激烈（大量排队）；数据库连接池过小；慢SQL拖慢了整个事务。

Q80: 如何在不停止服务的情况下，修复一个数据一致性 bug？
A: 发布热修复补丁；或者通过跑后台批处理脚本扫描不一致的数据，做补偿扣减或修复（补偿机制）。

## 第五类：开放式题（综合评估）
23. 系统架构设计
Q81: 如果要将这个项目改成微服务架构，应该如何拆分？
A: 按领域驱动设计(DDD)拆分为：用户服务、商品服务、订单服务、库存服务、支付服务。引入Gateway和注册中心。

Q82: 设计一个完整的支付流程，如何保证订单和支付的一致性？
A: 用户发起支付 -> 生成支付单调用第三方 -> 第三方异步回调 -> 后端接收回调更新支付状态并发布消息 -> 订单状态变更为已支付。防重放和幂等校验。

Q83: 如果订单支持退货退款，核心逻辑应该如何设计？
A: 状态机引入 Refunded 状态。先校验是否发货，申请退货单 -> 仓库确认收货 -> 调支付接口退款 -> 加回库存 -> 完成。