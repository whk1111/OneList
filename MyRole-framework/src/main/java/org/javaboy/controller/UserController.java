package org.javaboy.controller;


import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.javaboy.Utils.JWTUtils;
import org.javaboy.domain.Role;
import org.javaboy.domain.User;
import org.javaboy.service.Impl.UserServiceImpl;
import org.javaboy.service.RoleService;
import org.javaboy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */

@Api(description = "用户管理")
@RestController
public class UserController {




    //ToDo
    /***
     * 1.任务回收站机制
     */

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @ApiOperation("登陆")
    @PostMapping("/user/login")
    public Map<String,Object> verify(@RequestBody Map<String, Object> params){
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        Map<String,Object> map = new HashMap<>();
        BaseMapper<User> baseMapper = userService.getBaseMapper();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("username", username);
        wrapper.eq("password",password);
        if(username== null || password == null || username.length() ==0 || password.length() ==0){
            map.put("state",false);
            map.put("msg","用户名或密码为空");
            return map;
        }
        User user = baseMapper.selectOne(wrapper);
        if(user == null) {
           map.put("state",false);
           map.put("msg","没有查到相关用户");
           return map;
        }//没有查到user 就是表名验证失败
        Map<String,String> map1 = new HashMap<>();
        map1.put("username",user.getUsername());
        map1.put("id",user.getId()+"");
        map.put("state",true);
        map.put("userId",user.getId());
        map.put("msg","查询成功");
        map.put("token",JWTUtils.getToken(map1));
        return map;
    }




    @ApiOperation("查看个人信息")
    @GetMapping("/user/myinformation")
    public Map<String,Object>  myInformation(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());

        //业务操作
        Map<String ,Object> map = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(new User());
        queryWrapper.select(new Predicate<TableFieldInfo>() {
            @Override
            public boolean test(TableFieldInfo tableFieldInfo) {
                return !"password".equals(tableFieldInfo.getColumn());
            }
        });
        queryWrapper.eq("id",userId);
        BaseMapper<User> baseMapper = userService.getBaseMapper();
        User user = baseMapper.selectOne(queryWrapper);
        if(user == null) {
            map.put("state",false);
            map.put("msg","没有查到信息");
            return map;
        }//没有查到user 就是表名验证失败
        map.put("state",true);
        map.put("msg","查询成功");

        //ToDO
        //录入user的值
        //为了不显示password的值
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id",user.getId());
        map1.put("username",user.getUsername());
        map1.put("nickname",user.getNickname());
        map1.put("img",user.getImg());
        map1.put("intro",user.getIntro());
        map.put("user",map1);
        return map;
    }






    /**
     * 查询已完成的任务
     * @param
     * @return
     */
    @ApiOperation("查询当天已完成任务")
    @GetMapping("/user/daylist/state/finsh")
    public Map<String,Object>  getMyFinshDailyPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());


        Map<String ,Object> map = new HashMap<>();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.eq("state",1);
            wrapper.orderByAsc("priority","end_time");
            List<Role> roles = roleService.list(wrapper);
            Date date = new Date();
            int day = date.getDay();
            for(int i=0 ;i <roles.size();i++){
                if(roles.get(i).getStartTime().getDay() != day  || roles.get(i).getEndTime().getDay() != day){
                    roles.remove(roles.get((i)));
                }
            }
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到任务");
            return map;
        }
    }

    @ApiOperation("查询当天不重要不紧急任务")
    @GetMapping("/user/daylist/state/NotIAndNotS")
    public Map<String,Object>  getMyNotIAndNotSDailyPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());



        Map<String ,Object> map = new HashMap<>();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.eq("priority",4);
            wrapper.orderByAsc("state","end_time");
            List<Role> roles = roleService.list(wrapper);
            Date date = new Date();
            int day = date.getDay();
            for(int i=0 ;i <roles.size();i++){
                if(roles.get(i).getStartTime().getDay() != day  || roles.get(i).getEndTime().getDay() != day){
                    roles.remove(roles.get((i)));
                }
            }
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到任务");
            return map;
        }
    }


    @ApiOperation("查询当天不重要紧急任务")
    @GetMapping("/user/daylist/state/NotIAndS")
    public Map<String,Object>  getMyIASDailyPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());


        Map<String ,Object> map = new HashMap<>();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.eq("priority",3);
            wrapper.orderByAsc("state","end_time");
            List<Role> roles = roleService.list(wrapper);
            Date date = new Date();
            int day = date.getDay();
            for(int i=0 ;i <roles.size();i++){
                if(roles.get(i).getStartTime().getDay() != day  || roles.get(i).getEndTime().getDay() != day){
                    roles.remove(roles.get((i)));
                }
            }
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到任务");
            return map;
        }
    }


    @ApiOperation("查询当天重要不紧急任务")
    @GetMapping("/user/daylist/state/IAndNotS")
    public Map<String,Object>  getMyIANotSDailyPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());



        Map<String ,Object> map = new HashMap<>();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.eq("priority",2);
            wrapper.orderByAsc("state","end_time");
            List<Role> roles = roleService.list(wrapper);
            Date date = new Date();
            int day = date.getDay();
            for(int i=0 ;i <roles.size();i++){
                if(roles.get(i).getStartTime().getDay() != day  || roles.get(i).getEndTime().getDay() != day){
                    roles.remove(roles.get((i)));
                }
            }
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到任务");
            return map;
        }
    }
    @ApiOperation("查询当天重要紧急任务")
    @GetMapping("/user/daylist/state/IAndS")
    public Map<String,Object>  getMyIAndSDailyPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());




        Map<String ,Object> map = new HashMap<>();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.eq("priority",1);
            wrapper.orderByAsc("state","end_time");
            List<Role> roles = roleService.list(wrapper);
            Date date = new Date();
            int day = date.getDay();
            for(int i=0 ;i <roles.size();i++){
                if(roles.get(i).getStartTime().getDay() != day  || roles.get(i).getEndTime().getDay() != day){
                    roles.remove(roles.get((i)));
                }
            }
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到任务");
            return map;
        }
    }







    /***
     *
     * @param
     * @return
     * 得到当天规划的任务
     */

    @ApiOperation("查询当天所有任务，默认按紧急优先级排列，优先级高的在上")
    @GetMapping("/user/daylist")
    public Map<String ,Object> getMyDailyPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());




        Map<String ,Object> map = new HashMap<>();
        Date date = new Date();
        int day = date.getDay();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.orderByAsc("state","priority","end_time");
            List<Role> roles = roleService.list(wrapper);
            System.out.println(roles);
            for(int i=0 ;i <roles.size();i++){
                if(roles.get(i).getStartTime().getDay() != day  || roles.get(i).getEndTime().getDay() != day){
                    roles.remove(roles.get((i)));
                }
            }
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到当天的任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到当天的任务");
            return map;
        }

    }

    /**
     * 得到我的所有任务,包括之前
     */
    @ApiOperation("查询所有任务（包括之前的任务），默认按紧急优先级排列，优先级高的在上")
    @GetMapping("/user/allList")
    public Map<String ,Object> getMyAllPlan(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());




        Map<String ,Object> map = new HashMap<>();
        try{
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.orderByAsc("state","priority","end_time");
            List<Role> roles = roleService.list(wrapper);
            if(roles.size() == 0){
                map.put("state",false);
                map.put("msg","没有查到任务");
                return map;
            }
            map.put("state",true);
            map.put("msg","查询成功");
            map.put("lists",roles);
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","没有查到任务");
            return map;
        }


    }



    /**
     * 注册用户
     * @param user
     * @return
     */
    @ApiOperation("注册用户")
    @PostMapping("/user/add")
    public Map<String ,Object>  addUser(@RequestBody  User user){



        Map<String ,Object> map = new HashMap<>();

        if(user.getUsername() .length() ==0 || user.getPassword().length() ==0){
            map.put("state",false);
            map.put("msg","账号密码不能为空");
            return map;
        }
        BaseMapper<User> baseMapper = userService.getBaseMapper();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("username", user.getUsername());
        User user1 = baseMapper.selectOne(wrapper);
        if(user1!=null) {
            map.put("state",false);
            map.put("msg","已存在该用户名");
            return map;
        }
        boolean save = userService.save(user);
        if(save){
            map.put("state",true);
            map.put("msg","注册成功");
            return map;
        }
        map.put("state",false);
        map.put("msg","注册失败");
        return map;
    }

    /***
     *
     * 修改用户信息
     * @param user
     * @return
     */
    @ApiOperation("修改用户信息")
    @PutMapping("/user/update")
    public Map<String, Object> updateInformation(HttpServletRequest request,@RequestBody  User user){

        Map<String ,Object> map = new HashMap<>();
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());
        user.setId(userId);

        BaseMapper<User> baseMapper = userService.getBaseMapper();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("username", user.getUsername());
        User user1 = baseMapper.selectOne(wrapper);
        if(user1!=null) {
            map.put("state", false);
            map.put("msg", "已存在该用户名");
            return map;
        }
        boolean update = userService.updateById(user);
        if(update){
            map.put("state",true);
            map.put("msg","修改成功");
            return map;
        }
        map.put("state",false);
        map.put("msg","修改失败");
        return map;
    }













    /**
     * 注销用户
     */
    @ApiOperation("注销用户")
    @DeleteMapping("/user/delete")
    public Map<String ,Object>  DelUser(HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());




        boolean del = userService.removeById(userId);
        Map<String ,Object> map = new HashMap<>();
        if(del){
            map.put("state",true);
            map.put("msg","删除成功");
            return map;
        }
        map.put("state",false);
        map.put("msg","删除失败");
        return map;
    }


    /**
     * 再次验证密码
     */
    @ApiOperation("再次验证密码")
    @PostMapping("/user/safe")
    public Map<String, Object> checkPassword(@RequestBody Map<String, Object> params, HttpServletRequest request){
        //解析token得到userId
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        Claim userID = verify.getClaim("id");
        Integer userId =  Integer.parseInt(userID.asString());
        String password = (String) params.get("password");

        QueryWrapper<User> wrapper =new QueryWrapper<>();
        wrapper.select("password");
        wrapper.eq("id" ,userId);
        Map<String, Object> map = new HashMap<>();
        try {
            User one = userService.getOne(wrapper);
            if(password.equals(one.getPassword())){
                map.put("state",true);
                map.put("msg","验证成功");
                return  map;
            }
            map.put("state",false);
            map.put("msg","验证失败");
            return  map;
        }catch (Exception e){
            map.put("state",false);
            map.put("msg","验证失败");
            return  map;
        }




    }

}
