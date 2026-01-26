package com.example.revitech.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.ChatGroup;
import com.example.revitech.entity.GroupMessage;
import com.example.revitech.repository.ChatGroupRepository;
import com.example.revitech.repository.GroupMessageRepository;

@Service
@Transactional
public class GroupChatService {

    private final GroupMessageRepository messageRepository;
    private final ChatGroupRepository groupRepository;

    public GroupChatService(GroupMessageRepository messageRepository, ChatGroupRepository groupRepository) {
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
    }

    // メッセージ一覧取得
    public List<GroupMessage> getMessages(Integer groupId) {
        return messageRepository.findByGroupIdOrderByCreatedAtAsc(groupId);
    }

    // メッセージ送信
    public void sendMessage(Integer groupId, Integer senderId, String content) {
        GroupMessage msg = new GroupMessage();
        msg.setGroupId(groupId);
        msg.setUserId(senderId);
        msg.setContent(content);
        messageRepository.save(msg);
    }
    
    // グループ情報の取得
    public ChatGroup getGroup(Integer groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
    }
}