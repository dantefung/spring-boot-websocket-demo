/**
 * Copyright (C), 2018-2019, 独立开发者DanteFung
 * FileName: User
 * Author:   admin
 * Date:     2019-08-20 22:33
 * Description: User model
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.websocketdemo.entity;

/**
 * @author Dante Fung

 * @create 2019-08-20 22:33

 * @desc User model

 * @since 1.0.0
 **/
public class User {

    private String userName;

    private int age;

    private int userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
