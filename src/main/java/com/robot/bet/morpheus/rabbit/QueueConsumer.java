package com.robot.bet.morpheus.rabbit;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.robot.bet.morpheus.controller.BetsBola;
import com.robot.bet.morpheus.entity.Aposta;


@Component
public class QueueConsumer {

    @Autowired
    private QueueSender queueSender;

	@RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload String fileBody) {
        GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        List<Aposta> apostas = gson.fromJson(fileBody, new TypeToken<List<Aposta>>() {}.getType());
        
        if(apostas!=null && apostas.size()>1) {
        	BetsBola bb = new BetsBola();
        	bb.runBet(apostas);
            String message = gson.toJson(apostas);
            queueSender.send(message);
        }
    }

}