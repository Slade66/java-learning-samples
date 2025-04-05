package com.slade66;

/**
 * 模拟 CAS 算法
 */
class CASExample {

    private volatile int value; // 主内存中的共享变量

    // 模拟 CAS：比较并交换
    public boolean compareAndSwap(int expectedValue, int newValue) {
        // 假设这是个原子操作
        if (value == expectedValue) {
            value = newValue;
            return true; // 更新成功
        } else {
            return false; // 更新失败
        }
    }

    // 调用流程示意
    public void updateValue() {
        int oldValue = value;          // 1. 先从主内存读取
        int newValue = oldValue + 1;   // 2. 本地计算新值

        // 3. 写回前检查当前值是否还和旧值一致
        while (!compareAndSwap(oldValue, newValue)) {
            // 如果失败了，重新读取、计算，再尝试
            oldValue = value;
            newValue = oldValue + 1;
        }
    }

}
