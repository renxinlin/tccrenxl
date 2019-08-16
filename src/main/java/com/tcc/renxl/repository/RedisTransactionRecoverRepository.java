package com.tcc.renxl.repository;

import com.alibaba.fastjson.JSONObject;
import com.tcc.renxl.RecoverTransactionInfo;
import com.tcc.renxl.TransactionInfo;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据分布式事务的指出:进程崩溃后要可以保障事务状态
 * 需要存储事务信息
 * 同时存储执行的源信息
 */
@Data
public class RedisTransactionRecoverRepository implements TransactionRecoverRepository {
    private static final String RECOVER_KEY_HASH = "tcc:renTransaction:recover";
    private static final String RECOVER_KEY_lIST = "tcc:renTransaction:recover:key";
    private static final String EMPTY_STR = "";
    // 一个列表最多可以包含 232 - 1 个元素 (4294967295, 每个列表超过40亿个元素)。
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public long save(RecoverTransactionInfo transaction) {
        // 通过list+hash 避免锁操作
        stringRedisTemplate.opsForList().rightPush(RECOVER_KEY_lIST,transaction.getTramsactionId());
        stringRedisTemplate.opsForHash().put(RECOVER_KEY_HASH,transaction.getTramsactionId(),JSONObject.toJSONString(transaction));

        return 0L;
    }

    @Override
    public long delete(RecoverTransactionInfo transaction) {
        // list结构在已经在get时候被弹出
         return stringRedisTemplate.opsForHash().delete(RECOVER_KEY_HASH,transaction.getTramsactionId());
    }

    @Override
    public TransactionInfo select(Long transactionId) {
        return null;
    }

    /**
     * 当存储选择redis时候这里的获取的总数不一定准备；redis特性导致
     * @param failureNum 获取指定的条数
     * @return
     */
    @Override
    public List<RecoverTransactionInfo> getRecoverTransactions(Long failureNum) {
        /**
         */
        List<RecoverTransactionInfo> recoverTransactionInfos = new ArrayList<>();
        for(int i=0;i<failureNum;i++){
            // 通过2种redis结构避免锁操作 同时解决hscan的count不准确问题
            String tramsactionId = stringRedisTemplate.opsForList().leftPop(RECOVER_KEY_lIST); // 移除列表的第一个元素;
            if(tramsactionId ==null || EMPTY_STR.equals(tramsactionId)){
                break;
            }
            String recoverTransactionInfoStr = (String) stringRedisTemplate.opsForHash().get(RECOVER_KEY_HASH, tramsactionId);
            RecoverTransactionInfo recoverTransactionInfo = JSONObject.parseObject(recoverTransactionInfoStr, RecoverTransactionInfo.class);
            recoverTransactionInfos.add(recoverTransactionInfo);
        }
        if(recoverTransactionInfos.size() == 0){
            return null;
        }
        return recoverTransactionInfos;
    }
}
