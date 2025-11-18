package org.example.framework.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * 自定义JobFactory，让Quartz的Job支持Spring依赖注入
 * 
 * 问题：Quartz默认通过反射创建Job实例，不经过Spring容器，导致@Autowired失效
 * 解决：重写createJobInstance方法，使用Spring容器创建Job实例并注入依赖
 */
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // 1. 调用父类方法创建Job实例
        Object jobInstance = super.createJobInstance(bundle);
        
        // 2. 使用Spring容器对Job实例进行依赖注入
        beanFactory.autowireBean(jobInstance);
        
        // 3. 返回注入后的Job实例
        return jobInstance;
    }
}
