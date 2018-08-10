package me.seriouszyx.cache;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class LoginCache {

    private static LoginCache instance = new LoginCache();

    private Map<String, String> loginUserSession = new HashMap<>();
    private Map<String, HttpSession> loginSession = new HashMap<>();

    private LoginCache() {

    }

    public static LoginCache getInstance() {
        return instance;
    }

    /**
     * 通过登录名获取对应登录用户的sessionId
     * @param username
     * @return
     */
    public String getSessionIdByUsername(String username){
        return loginUserSession.get(username);
    }

    /**
     * 通过sessionId获取对应的session对象
     * @param sessionId
     * @return
     */
    public HttpSession getSessionBySessionId(String sessionId){
        return loginSession.get(sessionId);
    }

    /**
     * 存储登录名与对应的登录sessionID至缓存对象
     * @param username
     * @param sessionId
     */
    public void setSessionIdByUserName(String username,String sessionId){
        loginUserSession.put(username, sessionId);
    }

    /**
     * 存储sessionId与对应的session对象至缓存对象
     * @param sessionId
     * @param session
     */
    public void setSessionBySessionId(String sessionId,HttpSession session){
        loginSession.put(sessionId, session);
    }

}
