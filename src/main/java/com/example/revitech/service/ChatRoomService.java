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
import com.example.revitech.entity.ChatMemberId;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.ChatReadStatus;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.ChatReadStatusRepository;
import com.example.revitech.repository.ChatRoomRepository;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UsersService usersService;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatMemberRepository chatMemberRepository,
                           ChatReadStatusRepository chatReadStatusRepository, ChatMessageRepository chatMessageRepository,
                           UsersService usersService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.usersService = usersService;
    }

    public ChatRoom getOrCreateDmRoom(Integer userId1, Integer userId2) {
        Integer u1 = Math.min(userId1, userId2);
        Integer u2 = Math.max(userId1, userId2);

        return chatRoomRepository.findDmRoomBetweenUsers(u1, u2).orElseGet(() -> {
            // ★★★ 修正: usersService.findById を使用する ★★★
            Users user2 = usersService.findById(userId2).orElseThrow();
            
            ChatRoom newDmRoom = new ChatRoom();
            newDmRoom.setType(1);
            newDmRoom.setCreatorId(userId1);
            newDmRoom.setName(user2.getName());
            ChatRoom savedRoom = chatRoomRepository.save(newDmRoom);

            ChatMemberId memberId1 = new ChatMemberId(savedRoom.getRoomId(), userId1);
            ChatMemberId memberId2 = new ChatMemberId(savedRoom.getRoomId(), userId2);

            chatMemberRepository.save(new ChatMember(memberId1));
            chatMemberRepository.save(new ChatMember(memberId2));

            return savedRoom;
        });
    }

    public List<UserSearchDto> getRoomMembers(Integer roomId) {
        List<ChatMember> members = chatMemberRepository.findById_RoomId(roomId);
        return members.stream()
            .map(member -> {
                // ★★★ 修正: usersService.findById を使用する ★★★
                Users user = usersService.findById(member.getId().getUserId()).orElseThrow();
                return new UserSearchDto(user.getUsersId(), user.getName(), user.getEmail());
            })
            .collect(Collectors.toList());
    }

    // --- 以下の既存メソッドは変更ありません ---

    public List<ChatRoomWithNotificationDto> getRoomsForUserWithNotifications(Integer userId) {
        List<ChatMember> memberships = chatMemberRepository.findById_UserId(userId);
        return memberships.stream().map(member -> {
            ChatRoom room = chatRoomRepository.findById(member.getId().getRoomId()).orElseThrow();
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
        group.setCreatorId(creatorId);
        ChatRoom savedGroup = chatRoomRepository.save(group);
        
        chatMemberRepository.save(new ChatMember(new ChatMemberId(savedGroup.getRoomId(), creatorId)));
        memberIds.forEach(memberId -> {
            if (!memberId.equals(creatorId)) {
                 chatMemberRepository.save(new ChatMember(new ChatMemberId(savedGroup.getRoomId(), memberId)));
            }
        });
        return savedGroup;
    }

    public void markRoomAsRead(Integer userId, Integer roomId) {
        ChatReadStatus status = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
            .orElse(new ChatReadStatus(userId, roomId));
        status.setLastReadAt(LocalDateTime.now());
        chatReadStatusRepository.save(status);
    }

    public Optional<ChatRoom> getRoomById(Integer roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public boolean isUserMemberOfRoom(Integer userId, Integer roomId) {
        return chatMemberRepository.existsById_UserIdAndId_RoomId(userId, roomId);
    }
}