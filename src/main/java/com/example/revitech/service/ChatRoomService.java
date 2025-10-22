package com.example.revitech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.revitech.repository.ChatMessageRepository; // ★ 修正済みのメソッドを持つ Repository
import com.example.revitech.repository.ChatReadStatusRepository;
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
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository; // ★ 修正済みのメソッドを持つ Repository

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ChatMemberRepository chatMemberRepository,
                           UsersRepository usersRepository,
                           UsersService usersService,
                           ChatReadStatusRepository chatReadStatusRepository,
                           ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    /** 既読情報を更新 */
    public void markRoomAsRead(Long userId, Long roomId) {
        ChatReadStatus status = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
            .orElse(new ChatReadStatus(userId, roomId));
        status.setLastReadAt(LocalDateTime.now());
        chatReadStatusRepository.save(status);
        logger.info("ユーザーID: {} がルームID: {} を既読にしました。", userId, roomId);
    }

    /** 通知付きルームリスト取得 */
    public List<ChatRoomWithNotificationDto> getRoomsForUserWithNotifications(Long userId) {
        List<ChatRoom> rooms = this.getRoomsForUser(userId);

        return rooms.stream().map(room -> {
            LocalDateTime lastReadAt = chatReadStatusRepository.findByUserIdAndRoomId(userId, room.getId())
                .map(ChatReadStatus::getLastReadAt)
                .orElse(LocalDateTime.of(1970, 1, 1, 0, 0));

            // ★★★ Repository のメソッド名を CreatedAt (キャメルケース) に修正 ★★★
            long unreadCount = chatMessageRepository.countByRoomIdAndCreatedAtAfter(room.getId(), lastReadAt);

            // ★★★ Repository のメソッド名を CreatedAt (キャメルケース) に修正 ★★★
            // ★★★ Entity の Getter 名も getCreatedAt に修正 (ChatMessage と ChatRoom 両方) ★★★
            LocalDateTime lastMessageTimestamp = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getId())
                .map(ChatMessage::getCreatedAt) // ChatMessage の getCreatedAt() を使用
                .orElse(room.getCreatedAt() != null ? room.getCreatedAt() : LocalDateTime.of(1970, 1, 1, 0, 0)); // ChatRoom の getCreatedAt() を使用

            return new ChatRoomWithNotificationDto(room, unreadCount, lastMessageTimestamp);
        }).collect(Collectors.toList());
    }

    /** メンバーシップチェック */
    public boolean isUserMemberOfRoom(Long userId, Long roomId) {
        return chatMemberRepository.existsByIdUserIdAndIdRoomId(userId, roomId); // 複合主キー対応
    }

    /** ルームメンバー取得 */
    public List<UserSearchDto> getRoomMembers(Long roomId) {
        List<ChatMember> members = chatMemberRepository.findByIdRoomId(roomId); // 複合主キー対応
        if (members.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = members.stream().map(ChatMember::getUserId).collect(Collectors.toList());
        List<Users> users = usersRepository.findAllById(userIds);
        return users.stream().map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail())).collect(Collectors.toList());
    }

    /** グループ作成 */
    public ChatRoom createGroupRoom(Long creatorId, String name, List<Long> memberIds) {
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType(2); // 例: 2 = GROUP
        group.setCreatarUserId(creatorId); // DBの 'creatar_user_id' に対応
        // ★ ChatRoom の createdAt もキャメルケースに修正した場合
        // group.setCreatedAt(LocalDateTime.now()); // @CreationTimestampがあれば不要
        ChatRoom savedGroup = chatRoomRepository.save(group);
        Long savedRoomId = savedGroup.getId();

        chatMemberRepository.save(new ChatMember(savedRoomId, creatorId)); // 複合主キー対応

        memberIds.stream()
            .distinct()
            .filter(memberId -> !memberId.equals(creatorId))
            .forEach(memberId -> usersService.findById(memberId).ifPresent(user ->
                chatMemberRepository.save(new ChatMember(savedRoomId, memberId)) // 複合主キー対応
            ));

        return savedGroup;
    }

    /** DM取得/作成 */
    public ChatRoom getOrCreateDmRoom(Long userId1, Long userId2) {
        return chatRoomRepository.findExistingDmRoom(userId1, userId2).orElseGet(() -> {
            logger.info("DMルームが見つかりません。ユーザー {} と {} の間に新規作成します。", userId1, userId2);
            ChatRoom dmRoom = new ChatRoom();
            dmRoom.setType(1); // 例: 1 = DM
            dmRoom.setName(null);
            dmRoom.setCreatarUserId(userId1); // DBの 'creatar_user_id' に対応
             // ★ ChatRoom の createdAt もキャメルケースに修正した場合
            // dmRoom.setCreatedAt(LocalDateTime.now()); // @CreationTimestampがあれば不要
            ChatRoom savedRoom = chatRoomRepository.save(dmRoom);
            Long savedRoomId = savedRoom.getId();

            chatMemberRepository.save(new ChatMember(savedRoomId, userId1)); // 複合主キー対応
            chatMemberRepository.save(new ChatMember(savedRoomId, userId2)); // 複合主キー対応
            logger.info("新規DMルーム作成完了 ID: {}", savedRoomId);
            return savedRoom;
        });
    }

    /** IDでルーム取得 */
    public Optional<ChatRoom> getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    /** 全ルーム取得 */
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    /** ユーザー所属ルーム取得 */
    public List<ChatRoom> getRoomsForUser(Long userId) {
        List<ChatMember> memberships = chatMemberRepository.findByIdUserId(userId); // 複合主キー対応

        List<Long> roomIds = memberships.stream()
                .map(ChatMember::getRoomId)
                .collect(Collectors.toList());
        if (roomIds.isEmpty()) {
            return List.of();
        }
        return chatRoomRepository.findAllById(roomIds);
    }
}