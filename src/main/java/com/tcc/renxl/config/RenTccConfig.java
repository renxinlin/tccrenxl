package com.tcc.renxl.config;

import com.tcc.renxl.TransactionManager;
import com.tcc.renxl.interceptors.TransactionInteceptor;
import com.tcc.renxl.recover.DefaultManualRecovery;
import com.tcc.renxl.recover.ManualRecovery;
import com.tcc.renxl.recover.RecoverScheduledJob;
import com.tcc.renxl.repository.RedisTransactionRecoverRepository;
import com.tcc.renxl.repository.RedisTransactionRepository;
import com.tcc.renxl.repository.TransactionRecoverRepository;
import com.tcc.renxl.repository.TransactionRepository;
import com.tcc.renxl.util.IdUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RenTccConfig {
    @Bean
    @ConditionalOnMissingBean(TransactionInteceptor.class)
    public TransactionInteceptor getTransactionInteceptor(RecoverScheduledJob recoverScheduledJob, TransactionManager transactionManager){
        TransactionInteceptor transactionInteceptor = new TransactionInteceptor();
        transactionInteceptor.setTransactionManager(transactionManager);
        transactionInteceptor.setRecoverInterface(recoverScheduledJob);
        return transactionInteceptor;
    }


    @Bean
    @ConditionalOnMissingBean(RecoverScheduledJob.class)
    public RecoverScheduledJob getRecoverScheduledJob(TransactionRecoverRepository transactionRecoverRepository, ManualRecovery manualRecovery){
        RecoverScheduledJob recoverScheduledJob = new RecoverScheduledJob();
        recoverScheduledJob.setManualRecoveryNumber(100);
        recoverScheduledJob.setManualRecoveryMinute(60*5);
        recoverScheduledJob.setSleepTime(1);
        recoverScheduledJob.setManualRecoveryService(manualRecovery);
        recoverScheduledJob.setTransactionRecoverRepository(transactionRecoverRepository);
        recoverScheduledJob.setTransactionNum(100);
        return recoverScheduledJob;
    }


    @Bean
    @ConditionalOnMissingBean(TransactionRecoverRepository.class)
    public TransactionRecoverRepository getTransactionRecoverRepository(StringRedisTemplate stringRedisTemplate){
        RedisTransactionRecoverRepository transactionRecoverRepository = new RedisTransactionRecoverRepository();
        transactionRecoverRepository.setStringRedisTemplate(stringRedisTemplate);
        return transactionRecoverRepository;
    }

    @Bean
    @ConditionalOnMissingBean(TransactionRepository.class)
    public TransactionRepository getTransactionRepository(StringRedisTemplate stringRedisTemplate){
        RedisTransactionRepository redisTransactionRepository = new RedisTransactionRepository();
        redisTransactionRepository.setStringRedisTemplate(stringRedisTemplate);
    return redisTransactionRepository;
    }


    /**
     * TODO 检测接口是不是支持
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ManualRecovery.class)
    public ManualRecovery getManualRecovery(){
        ManualRecovery manualRecovery = new DefaultManualRecovery();
        return manualRecovery;
    }

    @Bean
    @ConditionalOnMissingBean(IdUtil.class)
    public IdUtil getIdUtil(){
        return new IdUtil();
    }
}
