package com.example.revitech.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 【追加】ロギングのためのインポート
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

    // 【追加】ロガーの定義
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
     * 2人のユーザー間のDMルーム取得または作成
     * @param userId1 ユーザー1のID
     * @param userId2 ユーザー2のID
     * @return 既存または新規のChatRoom
     */
    public ChatRoom getOrCreateDmRoom(Long userId1, Long userId2) {
        
        logger.info("Attempting to get or create DM room between {} and {}", userId1, userId2);

        // 1. 既存のDMルームを検索
        Optional<ChatRoom> existingRoom = chatRoomRepository.findExistingDmRoom(userId1, userId2);
        if (existingRoom.isPresent()) {
            logger.info("Existing DM room found: {}", existingRoom.get().getId());
            return existingRoom.get();
        }

        // 2. 存在しない場合は新規作成
        logger.info("No existing DM room found. Creating new DM room.");
        ChatRoom dmRoom = new ChatRoom();
        // DMルーム名は設定しないか、ユーザー名に基づいて設定します（ここでは空のまま）
        dmRoom.setName(null); 
        dmRoom.setType("DM");
        // DMには特定の作成者は設定しないことが多いですが、ここでは userId1 を暫定的に設定
        dmRoom.setCreatarUserId(userId1); 
        
        ChatRoom savedRoom = chatRoomRepository.save(dmRoom);

        // 3. メンバーを追加
        try {
            chatMemberRepository.save(new ChatMember(savedRoom.getId(), userId1));
            chatMemberRepository.save(new ChatMember(savedRoom.getId(), userId2));
            logger.info("New DM room created and members added successfully: {}", savedRoom.getId());
            return savedRoom;
        } catch (Exception e) {
            logger.error("Failed to create DM room or add members: {}", e.getMessage(), e);
            throw new RuntimeException("DM Room creation failed due to database error. Check logs for SQL exception.", e);
        }
    }
    
    /**
     * グループチャットルームを新規作成する
     * @param creatorId 作成者ユーザーID
     * @param name グループ名
     * @param memberIds 招待メンバーIDのリスト
     * @return 作成されたChatRoom
     */
    public ChatRoom createGroupRoom(Long creatorId, String name, List<Long> memberIds) {
        
        // 1. ChatRoomの作成
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType("GROUP");
        group.setCreatarUserId(creatorId);
        ChatRoom savedGroup = chatRoomRepository.save(group);

        // 2. 作成者を追加 (creatorIdは有効なIDとしてChatRoomControllerで取得済み)
        chatMemberRepository.save(new ChatMember(savedGroup.getId(), creatorId));
        
        // 3. 他のメンバーを追加 (ここでユーザーIDの存在チェックを行う)
        
        // 重複を除去し、作成者IDをスキップ
        List<Long> distinctMemberIds = memberIds.stream()
            .distinct()
            .filter(memberId -> !memberId.equals(creatorId))
            .collect(Collectors.toList());
            
        for (Long memberId : distinctMemberIds) {
            // UsersServiceを使ってIDが有効かチェックし、存在するメンバーのみ追加
            Optional<Users> user = usersService.findById(memberId);
            if (user.isPresent()) { 
                chatMemberRepository.save(new ChatMember(savedGroup.getId(), memberId));
            } else {
                // 無効なIDが渡された場合はログに記録する (処理は続行)
                logger.warn("Invalid memberId provided for group creation: {}", memberId);
            }
        }
        
        return savedGroup;
    }
    
    /**
     * 特定のルームIDのメンバーを検索用DTOとして取得する (★新規追加★)
     * @param roomId ルームID
     * @return メンバーのUserSearchDtoリスト
     */
    public List<UserSearchDto> getRoomMembers(Long roomId) { // メソッド名を修正
        // 1. ルームIDに基づいてChatMemberを取得
        List<ChatMember> members = chatMemberRepository.findByRoomId(roomId);

        // 2. メンバーのユーザーIDリストを抽出
        List<Long> userIds = members.stream()
                .map(ChatMember::getUserId)
                .collect(Collectors.toList());

        // 3. ユーザーIDリストに基づいてUsersエンティティを取得
        List<Users> users = usersService.findAllById(userIds);

        // 4. UsersエンティティをUserSearchDtoに変換
        return users.stream()
                .map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * 全チャットルームを取得する (管理者用など)
     * @return 全ChatRoomのリスト
     */
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }
    
    /**
     * ルームIDでチャットルームを取得する
     * @param roomId ルームID
     * @return ChatRoom (Optional)
     */
    public Optional<ChatRoom> getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    /**
     * 特定のユーザーが参加しているルームを取得する
     * @param userId ユーザーID
     * @return 参加しているChatRoomのリスト
     */
    public List<ChatRoom> getRoomsForUser(Long userId) {
        List<ChatMember> members = chatMemberRepository.findByUserId(userId);
        List<Long> roomIds = members.stream()
            .map(ChatMember::getRoomId)
            .collect(Collectors.toList());
        return chatRoomRepository.findAllById(roomIds);
    }
}