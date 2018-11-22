package com.leadingsoft.demo.gateway.service.base.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.leadingsoft.demo.gateway.service.base.SessionService;

@Component
public class SessionServiceImpl implements SessionService {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private SessionRepository<ExpiringSession> sessionRepository;

    private IMap<String, String> userSessions;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        this.executor.scheduleAtFixedRate(() -> {
            final Set<String> allSessionKeys = this.getUserSessions().keySet();
            if (allSessionKeys.size() < 1000) {
                return;
            }
            final List<String> invalidSessions = allSessionKeys.stream().filter(userNo -> {
                final String sessionId = this.getUserSessions().get(userNo);
                final ExpiringSession session = this.sessionRepository.getSession(sessionId);
                if (session == null) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
            invalidSessions.forEach(userNo -> {
                this.getUserSessions().remove(userNo);
            });
        }, 1, 1, TimeUnit.DAYS);
    }

    @PreDestroy
    public void destroy() {
        this.executor.shutdown();
    }

    @Override
    public ExpiringSession getUserSession(final String userNo) {
        final String sessionId = this.userSessions.get(userNo);
        if (sessionId == null) {
            return null;
        }
        return this.sessionRepository.getSession(sessionId);
    }

    @Override
    public void removeUserSession(final String userNo) {
        if (!StringUtils.hasText(userNo)) {
            return;
        }
        final String sessionId = this.userSessions.get(userNo);
        if (sessionId == null) {
            return;
        }
        this.sessionRepository.delete(sessionId);
    }

    @Override
    public void updateUserSessionMap(final String userNo, final String sessionId) {
        this.getUserSessions().put(userNo, sessionId);
    }

    public IMap<String, String> getUserSessions() {
        if (this.userSessions == null) {
            this.userSessions = this.hazelcastInstance.getMap("UserSession");
        }
        return this.userSessions;
    }
}
