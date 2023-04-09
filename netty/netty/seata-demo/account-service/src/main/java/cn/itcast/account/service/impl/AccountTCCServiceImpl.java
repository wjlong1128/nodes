package cn.itcast.account.service.impl;

import cn.itcast.account.entity.AccountFreeze;
import cn.itcast.account.mapper.AccountFreezeMapper;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountTCCServiceImpl implements AccountTCCService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountFreezeMapper freezeMapper;


    @Override
    // 加上事务注解防止扣减余额失败导致扣错
    @Transactional
    public void deduct(String userId, int money) {
        // 获取事务id
        String xid = RootContext.getXID();
        // 悬挂的问题
        if (freezeMapper.selectById(xid) != null) {
            // 代表有冻结的记录 执行过了 直接结束任务
            return;
        }
        // 1. 扣减可用余额
        accountMapper.deduct(userId,money);
        // 2. 记录冻结金额
        AccountFreeze freeze = new AccountFreeze();
        freeze.setXid(xid);
        freeze.setUserId(userId);
        freeze.setFreezeMoney(money);
        freeze.setState(AccountFreeze.State.TRY);

        freezeMapper.insert(freeze);
    }

    @Override
    public boolean confirm(BusinessActionContext context) {
        // 1,。 获取事务id 根据id删除冻结记录
        String xid = context.getXid();
        int count = freezeMapper.deleteById(xid);
        // 证明是否删除成功
        return count == 1;
    }

    /**
     *  恢复冻结的钱 这些可以直接从数据库获取
     *  也可以从上下文对象获取
     * @param context
     * @return
     */
    @Override
    public boolean cancel(BusinessActionContext context) {
        AccountFreeze freeze = freezeMapper.selectById(context.getXid());

        // 空回滚的判断
        if (freeze == null) {
            // 获取id通过注解修饰的字符串
            String userId = context.getActionContext("userId").toString();
            // 证明try没有执行 此时插入一条数据证明回滚过
            freeze = new AccountFreeze();
            freeze.setXid(context.getXid());
            freeze.setUserId(userId);
            freeze.setFreezeMoney(0);
            freeze.setState(AccountFreeze.State.CANCEL);
            freezeMapper.insert(freeze);
            return true;
        }
        // 判断幂等性 是否执行过
        if (freeze.getState()==AccountFreeze.State.CANCEL){
            return true;
        }
        // 恢复钱
        accountMapper.refund(freeze.getUserId(),freeze.getFreezeMoney());
        // 将状态改为cancel 冻结金额清零
        freeze.setState(AccountFreeze.State.CANCEL);
        freeze.setFreezeMoney(0);

        int count = freezeMapper.updateById(freeze);
        return count == 1;
    }
}
