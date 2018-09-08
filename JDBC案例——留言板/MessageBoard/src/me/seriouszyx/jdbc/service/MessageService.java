package me.seriouszyx.jdbc.service;

import me.seriouszyx.jdbc.bean.Message;
import me.seriouszyx.jdbc.dao.MessageDAO;

import java.util.Date;
import java.util.List;

/**
 *  消息Service
 */
public class MessageService {

    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public List<Message> getMessages(int page, int pageSize) {
        return messageDAO.getMessages(page, pageSize);
    }

    public int countMessage() {
        return messageDAO.countMessages();
    }

    public boolean addMessage(Message message) {
        message.setCreateTime(new Date());
        return messageDAO.save(message);
    }
}
