package com.slade66;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.HashMap;
import java.util.Optional;

/**
 * 场景题：用户地址信息提取
 * <p>
 * 题目描述：
 * 假设你需要从用户信息中提取地址中的街道名称。
 * 如果用户不存在则或者用户没有设置地址，则返回对应的告警信息。
 * 请你使用 Optional 的链式调用来完成该任务。
 */
public class OptionalTest {

    private static HashMap<Integer, User> userDb = new HashMap<>();

    static {
        userDb.put(1, new User("lyz", Optional.ofNullable(new Address("szpt"))));
        userDb.put(2, new User("lxd", Optional.ofNullable(null)));
    }

    Optional<User> findUserById(int id) {
        return Optional.ofNullable(userDb.get(id));
    }

    String getUserAddress(int id) {
        return findUserById(id)
                .map(user -> user.getAddress()
                        .map(Address::getCollege)
                        .orElse("未设置地址"))
                .orElse("用户不存在");
    }

    @Test
    public void exam() {
        System.out.println(getUserAddress(1));
        System.out.println(getUserAddress(2));
        System.out.println(getUserAddress(3));
    }

}

@Data
@AllArgsConstructor
class User {
    private String name;
    private Optional<Address> address;
}

@Data
@AllArgsConstructor
class Address {
    private String college;
}
