package com.example.mqtt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence


private lateinit var instance: MainActivity
var broker = "tcp://141.94.203.13:1883"
// TLS/SSL
// String broker = "ssl://broker.emqx.io:8883";
var username = ""

var password = ""
var clientid = "Client"
val content = "Louis le bg";
val client = MqttClient(broker, clientid, MemoryPersistence())

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this;
        val encodedPayload : ByteArray
        try {
            val options = MqttConnectOptions()
            options.userName = username
            options.password = password.toCharArray()
            client.connect(options)
            encodedPayload = content.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            message.qos = 2
            message.isRetained = false
            client.publish("zoubidac", message);
            client.subscribe("zoubidac", 1);
            client.subscribe("humidity", 1);
            Log.d("MQTT", "MEssage Publish")
            receiveMessages();
        } catch (e: Exception) {
            // Give Callback on error here
        } catch (e: MqttException) {
            // Give Callback on error here
        }
    }
}

fun receiveMessages() {
    client.setCallback(object : MqttCallback {
        override fun connectionLost(cause: Throwable) {
            //connectionStatus = false
            // Give your callback on failure here
        }
        override fun messageArrived(topic: String, message: MqttMessage) {
            try {
                val data = String(message.payload, charset("UTF-8"))
                if (topic == "humidity") {
                    Log.d("Il fait ", data + " °C");
                } else {
                    Log.d(topic, data);
                }
                // Give your callback on message received here
            } catch (e: Exception) {
                // Give your callback on error here
            }
        }
        override fun deliveryComplete(token: IMqttDeliveryToken) {
            // Acknowledgement on delivery complete
        }
    })
}
