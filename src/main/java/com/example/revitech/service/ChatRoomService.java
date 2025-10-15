package com.example.revitech.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.ChatRoomRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class ChatRoomService {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ChatMemberRepository chatMemberRepository,
                           UsersRepository usersRepository,
                           UsersService usersService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    public boolean isUserMemberOfRoom(Long userId, Long roomId) {
        return chatMemberRepository.existsByUserIdAndRoomId(userId, roomId);
    }

    public List<UserSearchDto> getRoomMembers(Long roomId) {
        logger.info("STEP 1: メンバーリスト取得処理を開始 (roomId: {})", roomId);
        List<ChatMember> members = chatMemberRepository.findByRoomId(roomId);
        logger.info("STEP 2: chat_membersテーブルから {} 件の所属情報を発見", members.size());

        if (members.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = members.stream().map(ChatMember::getUserId).collect(Collectors.toList());
        logger.info("STEP 3: 抽出したユーザーIDリスト: {}", userIds);

        List<Users> users = usersRepository.findAllById(userIds);
        logger.info("STEP 4: Usersテーブルから {} 件のユーザー情報を発見", users.size());

        return users.stream().map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail())).collect(Collectors.toList());
    }

    public ChatRoom createGroupRoom(Long creatorId, String name, List<Long> memberIds) {
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType("GROUP");
        group.setCreatarUserId(creatorId);
        ChatRoom savedGroup = chatRoomRepository.save(group);

        chatMemberRepository.save(new ChatMember(savedGroup.getId(), creatorId));
        
        memberIds.stream()
            .distinct()
            .filter(memberId -> !memberId.equals(creatorId))
            .forEach(memberId -> usersService.findById(memberId).ifPresent(user -> 
                chatMemberRepository.save(new ChatMember(savedGroup.getId(), memberId))
            ));
        
        return savedGroup;
    }

    public ChatRoom getOrCreateDmRoom(Long userId1, Long userId2) {
        return chatRoomRepository.findExistingDmRoom(userId1, userId2).orElseGet(() -> {
            logger.info("No existing DM room found. Creating new DM room.");
            ChatRoom dmRoom = new ChatRoom();
            dmRoom.setType("DM");
            dmRoom.setCreatarUserId(userId1); 
            ChatRoom savedRoom = chatRoomRepository.save(dmRoom);
            chatMemberRepository.save(new ChatMember(savedRoom.getId(), userId1));
            chatMemberRepository.save(new ChatMember(savedRoom.getId(), userId2));
            return savedRoom;
        });
    }
    
    public Optional<ChatRoom> getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    public List<ChatRoom> getRoomsForUser(Long userId) {
        List<Long> roomIds = chatMemberRepository.findByUserId(userId).stream()
                .map(ChatMember::getRoomId)
                .collect(Collectors.toList());
        return roomIds.isEmpty() ? List.of() : chatRoomRepository.findAllById(roomIds);
    }
}