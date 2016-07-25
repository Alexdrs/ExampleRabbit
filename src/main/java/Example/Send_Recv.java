package Example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;

public class Send_Recv {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = "myQueue";
        String routingKey = "myKey";
        String exchangeName = "myExchange";
        channel.queueDeclare(queueName, true, false, false, null); //1. Создали очередь
        channel.exchangeDeclare(exchangeName, "direct", true); //2. Создали узел
        channel.queueBind(queueName, exchangeName, routingKey);//3. Связали узел с очередью

        System.out.println("Введите текст сообщения:");
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();

        channel.basicPublish("", queueName, null, message.getBytes("UTF-8")); //4. Отправили сообщение в узел
        System.out.println(" [x] Сообщение '" + message + "'" + " отправлено!");

        System.out.println(" [*] Ожидание 5 секунд");   // Подождать 5 секунд
        Thread th = Thread.currentThread();
        th.sleep(5000);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override //6. Прочитать сообщение из очереди и вывести сообщение в стандартный поток
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Получено сообщение: '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}