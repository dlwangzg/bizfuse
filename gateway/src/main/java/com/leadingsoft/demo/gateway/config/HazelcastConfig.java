package com.leadingsoft.demo.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
//@ConditionalOnProperty("cluster.session.enabled")
@EnableHazelcastHttpSession
public class HazelcastConfig {

    @Value("${hazelcast.network.port:63751}")
    private int port;
    @Value("${hazelcast.network.portCount:10}")
    private int portCount;
    @Value("${hazelcast.network.interfaces}")
    private String interfaces;
    @Value("${hazelcast.network.joinMembers}")
    private String members;

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance() {
        final Config cfg = new Config();

        final NetworkConfig netConfig = cfg.getNetworkConfig();
        netConfig.setReuseAddress(true);
        netConfig.setPort(this.port).setPortAutoIncrement(false).setPortCount(this.portCount);
        for (final String interface1 : this.interfaces.split(",")) {
            netConfig.getInterfaces().addInterface(interface1.trim());
        }

        final JoinConfig join = netConfig.getJoin();
        join.getMulticastConfig().setEnabled(false);
        for (final String joinMember : this.members.split(",")) {
            join.getTcpIpConfig().addMember(joinMember.trim());
        }
        join.getTcpIpConfig().setEnabled(true);

        cfg.getMapConfig("captchaCache").setTimeToLiveSeconds(120);

        return Hazelcast.newHazelcastInstance(cfg);
    }
}
