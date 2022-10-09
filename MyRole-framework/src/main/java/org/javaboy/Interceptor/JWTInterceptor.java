package org.javaboy.Interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaboy.Utils.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */

/**
 * 验证JWT的拦截器
 */
public class JWTInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String,Object> map = new HashMap<>();
        String token = request.getHeader("token");
        try {
            JWTUtils.verify(token);
            return true; // 放行请求
        }catch (TokenExpiredException e){
            e.printStackTrace();
            map.put("msg","token过期");
        }
        catch (Exception e){
            e.printStackTrace();
            map.put("msg","token无效");
        }
        map.put("state",false);//设置状态

        //转换map -->json
        String json = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
