package com.leadingsoft.demo.gateway.service.base;

import org.springframework.session.ExpiringSession;

public interface SessionService {

    ExpiringSession getUserSession(String userNo);

    void removeUserSession(String userNo);

    void updateUserSessionMap(String userNo, String sessionId);
}
