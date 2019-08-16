package com.tcc.renxl.repository;

import com.alibaba.fastjson.JSONObject;
import com.tcc.renxl.TransactionInfo;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 根据分布式事务的指出:进程崩溃后要可以保障事务状态
 * 需要存储事务信息
 * 同时存储执行的源信息
 */
@Data
public class RedisTransactionRepository implements TransactionRepository {

    private static final String TRANSACTION_KEY = "tcc:renTransaction:key";
    // 一个列表最多可以包含 232 - 1 个元素 (4294967295, 每个列表超过40亿个元素)。
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public long save(TransactionInfo transaction) {
        stringRedisTemplate.opsForHash().put(TRANSACTION_KEY,transaction.getTramsactionId(), JSONObject.toJSONString(transaction));
        return 0;
    }

    @Override
    public long delete(TransactionInfo transaction) {
        Long delete = stringRedisTemplate.opsForHash().delete(TRANSACTION_KEY, transaction.getTramsactionId());

        return delete;
    }

    @Override
    public TransactionInfo select(Long transactionId) {
        String transactionStr = (String) stringRedisTemplate.opsForHash().get(TRANSACTION_KEY, transactionId);
        TransactionInfo transactionInfo = JSONObject.parseObject(transactionStr, TransactionInfo.class);
        return transactionInfo;
    }
}
