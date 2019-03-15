package com.alibaba.fescar.samples.tcc.dubbo.starter;

import com.alibaba.fescar.common.util.StringUtils;
import com.alibaba.fescar.samples.tcc.dubbo.ApplicationKeeper;
import com.alibaba.fescar.samples.tcc.dubbo.action.ResultHolder;
import com.alibaba.fescar.samples.tcc.dubbo.service.TccTransactionService;
import org.apache.curator.test.TestingServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zhangsen
 */
public class DubboTccTransactionStarter {

    static AbstractApplicationContext applicationContext = null;

    static TccTransactionService tccTransactionService = null;

    private static TestingServer server;

    public static void main(String[] args) throws Exception {

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                new String[] {"spring/fescar-tcc.xml", "spring/fescar-dubbo-reference.xml"});

        tccTransactionService = (TccTransactionService) applicationContext.getBean("tccTransactionService"   );

        //分布式事务提交demo
        transactionCommitDemo();

        //分布式事务回滚demo
        transactionRollbackDemo();

        new ApplicationKeeper(applicationContext).keep();
    }

    private static void transactionCommitDemo() throws InterruptedException {
        String txId = tccTransactionService.doTransactionCommit();
        System.out.println(txId);
        Assert.isTrue(StringUtils.isNotBlank(txId), "事务开启失败");

        System.out.println("transaction commit demo finish.");
    }

    private static void transactionRollbackDemo() throws InterruptedException {
        Map map = new HashMap();
        try{
            tccTransactionService.doTransactionRollback(map);
            Assert.isTrue(false, "分布式事务未回滚");
        }catch (Throwable t) {
            Assert.isTrue(true, "分布式事务异常回滚");
        }
        String txId = (String) map.get("xid");
        System.out.println(txId);

        System.out.println("transaction rollback demo finish.");
    }

    private static void mockZKServer() throws Exception {
        //Mock zk server，作为 dubbo 配置中心
        server = new TestingServer(2181, true);
        server.start();
    }

}
