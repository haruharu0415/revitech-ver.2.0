package com.example.revitech.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMemberRepository;
import com.example.revitech.repository.ChatRoomRepository;

@Service
@Transactional
public class ChatRoomService {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UsersService usersService;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ChatMemberRepository chatMemberRepository,
                           UsersService usersService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.usersService = usersService;
    }

    /**
     * ★★★ ここが今回の主な修正箇所です ★★★
     * 特定のルームIDのメンバーを取得するメソッドに、詳細なログを追加しました。
     * @param roomId ルームID
     * @return メンバーのUserSearchDtoリスト
     */
    public List<UserSearchDto> getRoomMembers(Long roomId) {
        // ★ どのルームのメンバーを取得しようとしているかログに出力
        logger.info("STEP 1: Fetching members for roomId: {}", roomId);
        
        // 1. ルームIDに基づいてChatMemberテーブルから所属情報を取得
        List<ChatMember> members = chatMemberRepository.findByRoomId(roomId);
        // ★ 見つかった所属情報の件数をログに出力
        logger.info("STEP 2: Found {} member entries in ChatMember table for roomId: {}", members.size(), roomId);

        // もしこの時点でメンバーが見つからなければ、ここで処理を終了
        if (members.isEmpty()) {
            logger.warn("STEP 2-1: No members found for roomId: {}. Returning empty list.", roomId);
            return List.of();
        }

        // 2. 所属情報からユーザーIDのリストを抽出
        List<Long> userIds = members.stream()
                .map(ChatMember::getUserId)
                .collect(Collectors.toList());
        // ★ 抽出したユーザーIDのリストをログに出力
        logger.info("STEP 3: Extracted userIds from member entries: {}", userIds);

        // 3. ユーザーIDのリストを使って、Usersテーブルから完全なユーザー情報を取得
        List<Users> users = usersService.findAllById(userIds);
        // ★ Usersテーブルから見つかったユーザーの件数をログに出力
        logger.info("STEP 4: Found {} user entities in Users table for the extracted userIds.", users.size());
        
        if (users.isEmpty()) {
            logger.error("STEP 4-1: CRITICAL! Found member entries but could not find corresponding users in Users table for userIds: {}", userIds);
        }

        // 4. 取得したUsersエンティティを、画面表示用のUserSearchDtoに変換して返す
        return users.stream()
                .map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    // --- 以下のメソッドはユーザー様提供のコードから変更ありません ---

    public ChatRoom getOrCreateDmRoom(Long userId1, Long userId2) {
        logger.info("Attempting to get or create DM room between {} and {}", userId1, userId2);
        Optional<ChatRoom> existingRoom = chatRoomRepository.findExistingDmRoom(userId1, userId2);
        if (existingRoom.isPresent()) {
            logger.info("Existing DM room found: {}", existingRoom.get().getId());
            return existingRoom.get();
        }
        logger.info("No existing DM room found. Creating new DM room.");
        ChatRoom dmRoom = new ChatRoom();
        dmRoom.setName(null);
        dmRoom.setType("DM");
        dmRoom.setCreatarUserId(userId1);
        ChatRoom savedRoom = chatRoomRepository.save(dmRoom);
        try {
            chatMemberRepository.save(new ChatMember(savedRoom.getId(), userId1));
            chatMemberRepository.save(new ChatMember(savedRoom.getId(), userId2));
            logger.info("New DM room created and members added successfully: {}", savedRoom.getId());
            return savedRoom;
        } catch (Exception e) {
            logger.error("Failed to create DM room or add members: {}", e.getMessage(), e);
            throw new RuntimeException("DM Room creation failed due to database error.", e);
        }
    }

    public ChatRoom createGroupRoom(Long creatorId, String name, List<Long> memberIds) {
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType("GROUP");
        group.setCreatarUserId(creatorId);
        ChatRoom savedGroup = chatRoomRepository.save(group);
        chatMemberRepository.save(new ChatMember(savedGroup.getId(), creatorId));
        List<Long> distinctMemberIds = memberIds.stream()
            .distinct()
            .filter(memberId -> !memberId.equals(creatorId))
            .collect(Collectors.toList());
        for (Long memberId : distinctMemberIds) {
            usersService.findById(memberId).ifPresent(user -> 
                chatMemberRepository.save(new ChatMember(savedGroup.getId(), memberId))
            );
        }
        return savedGroup;
    }

    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    public Optional<ChatRoom> getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    public List<ChatRoom> getRoomsForUser(Long userId) {
        List<ChatMember> memberships = chatMemberRepository.findByUserId(userId);
        List<Long> roomIds = memberships.stream()
                .map(ChatMember::getRoomId)
                .collect(Collectors.toList());
        if (roomIds.isEmpty()) {
            return List.of();
        }
        return chatRoomRepository.findAllById(roomIds);
    }
}