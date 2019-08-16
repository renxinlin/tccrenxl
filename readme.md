# 基于TCC理论的分布式事务框架
    优势1：在原有的tcc理论基础上增加try之前的嗅探功能
    并且可以通过开关自定义嗅探,同时用户可以自己实现嗅探功能【自定义拓展】
    优势2: 可以自定义始终失败的事务的手工处理代码：解决传统tcc无法自定义失败过多的事务无法处理的问题
    
    
    
# 业务使用方式
      1 增加扫描包 
      2 配置redis信息
      3 注入stringRedisTemplate
      4 在业务方法添加注解
      例子:
          @TransactionRen(cancel = "test2_2",confirm ="test2_1" )
          public RestResult test2(Product product){
      5 自定义 提交和回滚方法[不需要加注解][方法参数需同加注解的方法]
      例子:
         public RestResult test2(Product product){
            
          }
      
          public RestResult test2_1(Product product){
           }
           
       备注：目前只支持使用redis存储事务信息
       
       
# 作者联系
    email :2284075845@qq.com
    微信公众号: 阿新小栈
    
# todo 完善中 