package com.gjy.project.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RedisDistributeLock implements DistributeLock{

    private Logger logger = LoggerFactory.getLogger(RedisDistributeLock.class);

    private RedissonClient redissonClient;

    private DistributeLockProperties distributeLockProperties;

    public RedisDistributeLock(RedissonClient redissonClient, DistributeLockProperties distributeLockProperties) {
        this.redissonClient = redissonClient;
        this.distributeLockProperties = distributeLockProperties;
    }


    /**
     * 尝试获取分布式锁，获取到锁后执行 runnable.run()方法，然后释放锁，返回trve;若无法获取锁，则立即返回false，不会阻寒
     * @param lockKey
     * @param runnable
     * @return
     */
    @Override
    public boolean tryLockRun(String lockKey, Runnable runnable) {
        Objects.requireNonNull(lockKey);
        Objects.requireNonNull(runnable);
        String redisKey =this.getLockKey(lockKey);
        RLock lock = redissonClient.getLock(redisKey);
        boolean flag = false;
        try {
            flag = lock.tryLock();
            if (logger.isDebugEnabled()){
                logger.debug("get lock success, lockKey:{}, redisKey:{}");
            }
            if (!flag){
                return false;
            }
            runnable.run();
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
        return true;
    }

    /**
     * 尝试在指定的时间内获取分布式锁，获取到锁后技行 runnable.run()方法，然后经放锁，返回true:否则返网false:此方法最多阳寒时长由waitTime指定
     * <ul>
     * <li>获取锁的过程会阻塞，阻塞时长由waitTime 指定</li>
     * <li>锁会自动续期</li>
     * </ul>
     * @param lockKey
     * @param runnable
     * @param waitTime
     * @param unit
     * @return
     */
    @Override
    public boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, TimeUnit unit) {
        Objects.requireNonNull(lockKey);
        Objects.requireNonNull(runnable);
        if (waitTime <= 0){
            throw new IllegalArgumentException("waitTime > 0");
        }
        String redisKey =this.getLockKey(lockKey);
        RLock lock = redissonClient.getLock(redisKey);
        boolean flag = false;
        try {
            flag = lock.tryLock(waitTime, unit);
            if (logger.isDebugEnabled()){
                logger.debug("get lock success, lockKey:{}, redisKey:{}");
            }
            if (!flag){
                return false;
            }
            runnable.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
        return true;
    }

    /**
     * 尝试在指定的时间内获取分布式锁，获取到锁后技行 runnable.run()方法，然后释放锁，返回true;否则返回false;此方法最多阻塞时长由waitTime指定
     * <ul>
     * <li>获取锁的过程会阻塞，阳塞时长由 waitTime 指定</li>
     * <li>超过了 leaseTime:若锁还未释放，则自动释放</li>
     * </ul>
     * @param lockKey
     * @param runnable
     * @param waitTime
     * @param leaseTime
     * @param unit
     * @return
     */
    @Override
    public boolean tryLockRun(String lockKey, Runnable runnable, long waitTime, long leaseTime, TimeUnit unit) {
        Objects.requireNonNull(lockKey);
        Objects.requireNonNull(runnable);
        if (waitTime <= 0) {
            throw new IllegalArgumentException("waitTime > 0");
        }
        if (leaseTime <= 0) {
            throw new IllegalArgumentException("leaseTime > 0");
        }
        String redisKey = this.getLockKey(lockKey);
        RLock lock = redissonClient.getLock(redisKey);
        boolean flag = false;
        try {
            flag = lock.tryLock(waitTime, leaseTime, unit);
            if (logger.isDebugEnabled()) {
                logger.debug("get lock success, lockKey:{}, redisKey:{}");
            }
            if (!flag) {
                return false;
            }
            runnable.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (flag) {
                lock.unlock();
            }
            return true;
        }
    }


    /**
     * 此方法会一直等待，直到获取锁后，获取到锁后执行runnable.run()方法，然后释放锁
     *
     * 获取锁的过程会阻塞，直到成功获取锁
     *             锁会自动续期
     * @param lockName
     * @param runnable
     */
    @Override
    public void lockRun(String lockName, Runnable runnable) {
        Objects.requireNonNull(lockName);
        Objects.requireNonNull(runnable);
        String redisKey = this.getLockKey(lockName);
        RLock lock = redissonClient.getLock(redisKey);
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }




    /**
     * 此方法会一直等待，直到获取锁后，获取到锁后执行runnable.run()方法，然后释放锁
     * <ul>
     * <li>获取锁的过程会阻塞，直到成功获取锁</li>
     * <li>超过了 leaseTime，若锁还未释放，则自动释放</li>
     * </ul>
     * @param lockName
     * @param runnable
     * @param leaseTime
     * @param unit
     */
    @Override
    public void lockRun(String lockName, Runnable runnable, long leaseTime, TimeUnit unit) {
        Objects.requireNonNull(lockName);
        Objects.requireNonNull(runnable);
        if (leaseTime <= 0) {
            throw new IllegalArgumentException("leaseTime > 0");
        }
        String redisKey = this.getLockKey(lockName);
        RLock lock = redissonClient.getLock(redisKey);
        lock.lock(leaseTime, unit);
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }

    }


    private String getLockKey(String lockName) {
        return distributeLockProperties.getName() + "_" + lockName;
    }
}
