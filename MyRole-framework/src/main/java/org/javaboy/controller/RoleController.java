package org.javaboy.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.javaboy.Utils.JWTUtils;
import org.javaboy.domain.Role;
import org.javaboy.service.RoleService;
import org.javaboy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import java.util.*;
import com.aliyuncs.sts.model.v20150401.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */

@RestController
@Api(description = "任务管理")
public class RoleController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    /**
     * 新增任务
     * @param role
     * @return
     */
    @ApiOperation("新增任务")
    @PostMapping("/list/add")
    public Map<String,Object> CreatNewRole(HttpServletRequest request, @RequestBody Role role){


        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());

        Map<String ,Object> map = new HashMap<>();
        role.setUserId(userId);
        //默认状态是未完成的任务

        if(role.getState() == null){
            //开启默认设置
            role.setState(0);
        }
        if(role.getRecycled() == null){
            //默认未被回收

            role.setRecycled(0);
        }

        //默认都是现在的时间
        if(role.getStartTime() == null){
            Date date= new Date();
            role.setStartTime(date);
        }
        if(role.getEndTime() == null) {
            Date date = new Date();
            role.setEndTime(date);
        }
        try {
            Integer id = roleService.insertSelective(role);
            map.put("state",true);
            map.put("msg","创建成功");
            map.put("任务Id",role.getId());
            return map;
        }catch (Exception e){
            map.put("state",false);
            map.put("msg","创建失败");
            return map;
        }

    }
    @ApiOperation("搜索任务")
    @PostMapping("/search")
    public Map<String, Object> search(HttpServletRequest request, @RequestBody  Map<String, Object> params){


        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());

        String keyWord = (String) params.get("keyWord");
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.like("name",keyWord);
        wrapper.or();
        wrapper.like("description",keyWord);
        Map<String, Object> map = new HashMap<>();
        try{
            List<Role> list = roleService.list(wrapper);
            if(list.size()==0 || list == null){
                map.put("state",false);
                map.put("msg","没有查询相关数据");
                map.put("searchValue",0);
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("list",list);
            map.put("searchValue",1);
            return map;

        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","查询失败");
            map.put("searchValue",0);
            return map;
        }




    }




    @ApiOperation("查询我的已被回收的任务")
    @GetMapping("/recycledList")
    public Map<String, Object> getMyRecycledRoles(HttpServletRequest request ){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());

        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.eq("recycled",1);
        try {
            List<Role> list = roleService.list(wrapper);
            if (list == null || list.size()==0){
                map.put("state",false);
                map.put("msg","当前没有被回收的任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("list",list);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","查询失败");
            return map;
        }



    }




    @ApiOperation("修改任务信息")
    @PutMapping("/list/update")
    public Map<String,Object> updateRole(@RequestBody Role role){
        boolean update = roleService.updateById(role);
        Map<String ,Object> map = new HashMap<>();
        if(update){
            map.put("state",true);
            map.put("msg","修改成功");
            return map;
        }
        map.put("state",false);
        map.put("msg","修改失败");
        return map;
    }

    @ApiOperation("删除任务")
    @DeleteMapping("/list/delete")
    public Map<String ,Object>  DelUser(@RequestBody  Map<String, Object> params ,HttpServletRequest request) {
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());



        Integer taskId = (Integer) params.get("id");
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("id", taskId);
        Map<String, Object> map = new HashMap<>();
        try {
            Role role = roleService.getOne(wrapper);

            //是否有权删除
            if(role.getUserId() != userId){
                map.put("state",false);
                map.put("msg","您无权删除");
                return map;
            }
            //开始删除操作
            boolean del = roleService.removeById(taskId);
            if (del) {
                map.put("state", true);
                map.put("msg", "删除成功");
                return map;
            }
            map.put("state", false);
            map.put("msg", "删除失败");
            return map;

        }catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "删除失败");
            return map;
        }

    }



    @ApiOperation("清空我的回收站")
    @DeleteMapping("/list/cleanRecycled")
    public Map<String, Object> cleanMyRecycledRoles(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());

        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        //
        wrapper.eq("user_id",userId);
        wrapper.eq("recycled",1);
        Map<String, Object> map = new HashMap<>();
        try {
            boolean del = roleService.remove(wrapper);
            if (del) {
                map.put("state", true);
                map.put("msg", "删除成功");
                return map;
            }
            map.put("state", false);
            map.put("msg", "删除失败");
            return map;
        }catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", "删除失败");
            return map;
        }

    }



    @GetMapping("/sts")
    public Map<String, Object> getSts(){

        String accessKey_id = "accessKey_id"; //自己的acessKey_id
        String accessKey_secret = "accessKey_secret"; //填自己的
        String roleArn = "roleArn";// 填自己的

        //构建一个阿里云客户端，用于发起请求。
        //设置调用者（RAM用户或RAM角色）的AccessKey ID和AccessKey Secret。
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey_id, accessKey_secret);
        IAcsClient client = new DefaultAcsClient(profile);

        //构造请求，设置参数。
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRegionId("cn-hangzhou");
        request.setRoleArn(roleArn);
        request.setRoleSessionName("roleSessionName");//自己的


        Map<String, Object> map = new HashMap<>();

        //发起请求，并得到响应。
        try {
            AssumeRoleResponse response = client.getAcsResponse(request);
            map.put("data",new Gson().toJson(response));
            return map;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            map.put("ErrCode:",e.getErrCode());
            map.put("rrMsg:",e.getErrMsg());
            map.put("RequestId:",e.getRequestId());
        }
        return map;
    }





}
