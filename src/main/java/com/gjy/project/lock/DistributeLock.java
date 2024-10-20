package com.gjy.project.lock;

import java.util.concurrent.TimeUnit;

public interface DistributeLock {
    /**
     * 尝试获取分布式锁，获取到锁后执行 runable.run() 方法，然后释放锁，返回true
     * @param lockName
     * @param runnable
     * @return
     */
    boolean tryLockRun(String lockName, Runnable runnable);
    /**
     * 尝试在指定的时间内获取分布式锁，获取到锁后技行 runnable.run()方法，然后经放锁，返回true:否则返网false:此方法最多阳寒时长由waitTime指定
     * <ul>
     * <li>获取锁的过程会阻塞，阻塞时长由waitTime 指定</li>
     * <li>锁会自动续期</li>
     * </ul>
     */
    boolean tryLockRun(String lockName, Runnable runnable,long time, TimeUnit unit);
    /**
     * 尝试在指定的时间内获取分布式锁，获取到锁后技行 runnable.run()方法，然后释放锁，返回true;否则返回false;此方法最多阻塞时长由waitTime指定
     * <ul>
     * <li>获取锁的过程会阻塞，阳塞时长由 waitTime 指定</li>
     * <li>超过了 leaseTime:若锁还未释放，则自动释放</li>
     * </ul>
     */
    boolean tryLockRun(String lockName, Runnable runnable,long waitTime, long time, TimeUnit unit);
    /**
     * 此方法会一直等待，直到获取锁后，获取到锁后执行runnable.run()方法，然后释放锁
     *
     * 获取锁的过程会阻塞，直到成功获取锁
     *             锁会自动续期
     * @param lockName
     * @param runnable
     */
    void lockRun(String lockName, Runnable runnable);
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
    void lockRun(String lockName, Runnable runnable,long leaseTime, TimeUnit unit);


}
