package com.example.revitech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.ChatRoomWithNotificationDto;
import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.ChatReadStatus;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.ChatReadStatusRepository;
import com.example.revitech.repository.ChatRoomRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UsersRepository usersRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatMemberRepository chatMemberRepository, UsersRepository usersRepository, ChatReadStatusRepository chatReadStatusRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.usersRepository = usersRepository;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatRoomWithNotificationDto> getRoomsForUserWithNotifications(Integer userId) {
        // ★ 修正: findByUserId を使用
        List<ChatMember> memberships = chatMemberRepository.findByUserId(userId);

        return memberships.stream().map(member -> {
            ChatRoom room = chatRoomRepository.findById(member.getRoomId()).orElseThrow();
            
            // ★ 修正: findByUserIdAndRoomId を使用
            Optional<ChatReadStatus> readStatusOpt = chatReadStatusRepository.findByUserIdAndRoomId(userId, room.getRoomId());
            
            long unreadCount = readStatusOpt
                .map(status -> chatMessageRepository.countByRoomIdAndCreatedAtAfter(room.getRoomId(), status.getLastReadAt()))
                .orElseGet(() -> chatMessageRepository.countByRoomIdAndCreatedAtAfter(room.getRoomId(), LocalDateTime.MIN));

            LocalDateTime lastMessageTimestamp = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getRoomId())
                .map(ChatMessage::getCreatedAt).orElse(room.getCreatedAt());

            return new ChatRoomWithNotificationDto(room, unreadCount, lastMessageTimestamp);
        }).collect(Collectors.toList());
    }

    public ChatRoom createGroupRoom(Integer creatorId, String name, List<Integer> memberIds) {
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType(2);
        group.setCreatorId(creatorId); // ★ 修正: setCreatorId を使用
        ChatRoom savedGroup = chatRoomRepository.save(group);

        // ★ 修正: ChatMember のコンストラクタ引数を修正
        chatMemberRepository.save(new ChatMember(savedGroup.getRoomId(), creatorId));
        
        memberIds.forEach(memberId -> {
            if (!memberId.equals(creatorId)) {
                 chatMemberRepository.save(new ChatMember(savedGroup.getRoomId(), memberId));
            }
        });

        return savedGroup;
    }

    public ChatRoom getOrCreateDmRoom(Integer userId1, Integer userId2) {
        return chatRoomRepository.findDmRoomBetweenUsers(userId1, userId2).orElseGet(() -> {
            Users user2 = usersRepository.findById(userId2).orElseThrow();

            ChatRoom dmRoom = new ChatRoom();
            dmRoom.setType(1);
            dmRoom.setCreatorId(userId1); // ★ 修正: setCreatorId を使用
            dmRoom.setName(user2.getName());
            ChatRoom savedRoom = chatRoomRepository.save(dmRoom);

            // ★ 修正: ChatMember のコンストラクタ引数を修正
            chatMemberRepository.save(new ChatMember(savedRoom.getRoomId(), userId1));
            chatMemberRepository.save(new ChatMember(savedRoom.getRoomId(), userId2));
            return savedRoom;
        });
    }

    public List<UserSearchDto> getRoomMembers(Integer roomId) {
        List<ChatMember> members = chatMemberRepository.findByRoomId(roomId);
        return members.stream()
            .map(member -> {
                // ★ 修正: member.getUserId() を使用
                Users user = usersRepository.findById(member.getUserId()).orElseThrow();
                return new UserSearchDto(user.getUsersId(), user.getName(), user.getEmail());
            })
            .collect(Collectors.toList());
    }

    public void markRoomAsRead(Integer userId, Integer roomId) {
        // ★ 修正: findByUserIdAndRoomId と ChatReadStatusコンストラクタ引数を修正
        ChatReadStatus status = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
            .orElse(new ChatReadStatus(userId, roomId));
        status.setLastReadAt(LocalDateTime.now());
        chatReadStatusRepository.save(status);
    }

    public Optional<ChatRoom> getRoomById(Integer roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public boolean isUserMemberOfRoom(Integer userId, Integer roomId) {
        // ★ 修正: existsByUserIdAndRoomId を使用
        return chatMemberRepository.existsByUserIdAndRoomId(userId, roomId);
    }
}