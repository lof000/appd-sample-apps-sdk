package com.javainuse;

/*
 * 
 * Codigo exemplo para usar o sdk
 * Referencia
 * https://docs.appdynamics.com/appd/22.x/latest/en/application-monitoring/install-app-server-agents/java-agent/use-the-java-agent-api-and-instrumentation-sdk/java-agent-api-user-guide
 * 
 */

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//#AppDynamics Imports
import com.appdynamics.agent.api.AppdynamicsAgent;
import com.appdynamics.agent.api.EntryTypes;
import com.appdynamics.agent.api.Transaction;

@Component
public class BTPrePostFilter
        implements GlobalFilter, Ordered {

    Transaction transaction = null;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {

        try{
            System.out.println("=============== Global Pre Filter - Transaction Start...");
            // AppDynamics ---- MARCAR INICIO DA TRANSACAO
            transaction = AppdynamicsAgent.startTransaction("TesteCharon", null, EntryTypes.POJO, false);
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        //AppDynamics ----- MARCAR FINAL DA TRANSACAO
                        System.out.println("=============== Global post Filter - Transaction end...r");
                        transaction.end();
                    }));
        }finally{
            //AppDynamics --- CLEANUP
            if (transaction != null) {
                transaction.end();
             }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
