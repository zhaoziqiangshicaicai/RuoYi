package com.ruoyi.web.controller.tool;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.util.RedisUtil;
import com.ruoyi.system.domain.SysUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: springbootdemo
 * @Date: 2019/1/25 15:03
 * @Author: Mr.Zheng
 * @Description:
 */
@RequestMapping("/no/redis")
@RestController
public class RedisController extends BaseController {

    private static int ExpireTime = 60;   // redis中存储的过期时间60s

    @Resource
    private RedisUtil redisUtil;

    @RequestMapping("set")
    public AjaxResult redisset(String key, String value){
        SysUser user = new SysUser();
        user.setEmail("1110009876@qq.com");
        user.setUserName("小明");
        user.setPhonenumber("13809688765");
        redisUtil.set(key,user);
        return success();
    }

    @RequestMapping("get")
    public AjaxResult redisget(String key){
        redisUtil.get(key);
        return success();
    }

    @RequestMapping("expire")
    public AjaxResult expire(String key){
        redisUtil.expire(key,ExpireTime);
        return success();
    }
}