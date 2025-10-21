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
import com.example.revitech.entity.ChatReadStatus; // ★ Importを確認
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

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;

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

    /**
     * 既読情報を更新するメソッド
     */
    public void markRoomAsRead(Long userId, Long roomId) {
        // 1. 既読情報を取得 or 新規作成
        ChatReadStatus status = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
            .orElse(new ChatReadStatus(userId, roomId)); // orElse()のカッコはここで閉じる

        // 2. 最終既読日時を更新 (status オブジェクトに対して呼び出す)
        status.setLastReadAt(LocalDateTime.now()); // setLastReadAt は status に対して呼び出す

        // 3. データベースに保存
        chatReadStatusRepository.save(status);
        logger.info("User {} marked room {} as read.", userId, roomId);
    }

    /**
     * 未読件数付きのルームリストを取得するメソッド
     */
    public List<ChatRoomWithNotificationDto> getRoomsForUserWithNotifications(Long userId) {
        List<ChatRoom> rooms = this.getRoomsForUser(userId);

        return rooms.stream().map(room -> {

            // chatReadStatusRepository... は Optional<ChatReadStatus> を返す
            LocalDateTime lastReadAt = chatReadStatusRepository.findByUserIdAndRoomId(userId, room.getId())
                // Optionalの中身(ChatReadStatus)に対して getLastReadAt を呼び出す正しい書き方
                .map(ChatReadStatus::getLastReadAt)
                // もしOptionalが空なら、デフォルトの日時を使う
                .orElse(LocalDateTime.of(1970, 1, 1, 0, 0));

            long unreadCount = chatMessageRepository.countByRoomIdAndCreatedAtAfter(room.getId(), lastReadAt);

            LocalDateTime lastMessageTimestamp = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.getId())
                .map(msg -> msg.getCreatedAt())
                .orElse(room.getCreatedAt() != null ? room.getCreatedAt() : LocalDateTime.of(1970, 1, 1, 0, 0));

            return new ChatRoomWithNotificationDto(room, unreadCount, lastMessageTimestamp);
        }).collect(Collectors.toList());
    }

    // --- 以下、他のメソッド (変更なし) ---
    public boolean isUserMemberOfRoom(Long userId, Long roomId) {
        return chatMemberRepository.existsByUserIdAndRoomId(userId, roomId);
    }
    public List<UserSearchDto> getRoomMembers(Long roomId) {
        List<ChatMember> members = chatMemberRepository.findByRoomId(roomId);
        if (members.isEmpty()) return List.of();
        List<Long> userIds = members.stream().map(ChatMember::getUserId).collect(Collectors.toList());
        List<Users> users = usersRepository.findAllById(userIds);
        return users.stream().map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail())).collect(Collectors.toList());
    }
    public ChatRoom createGroupRoom(Long creatorId, String name, List<Long> memberIds) {
        ChatRoom group = new ChatRoom();
        group.setName(name);
        group.setType("GROUP");
        group.setCreatarUserId(creatorId); // 'creatar' スペルを使用
        ChatRoom savedGroup = chatRoomRepository.save(group);
        chatMemberRepository.save(new ChatMember(savedGroup.getId(), creatorId));
        memberIds.stream().distinct().filter(id -> !id.equals(creatorId))
            .forEach(id -> usersService.findById(id).ifPresent(user ->
                chatMemberRepository.save(new ChatMember(savedGroup.getId(), id))
            ));
        return savedGroup;
    }
    public ChatRoom getOrCreateDmRoom(Long userId1, Long userId2) {
        return chatRoomRepository.findExistingDmRoom(userId1, userId2).orElseGet(() -> {
            ChatRoom dmRoom = new ChatRoom();
            dmRoom.setType("DM");
            dmRoom.setCreatarUserId(userId1); // 'creatar' スペルを使用
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
            .map(ChatMember::getRoomId).collect(Collectors.toList());
        return roomIds.isEmpty() ? List.of() : chatRoomRepository.findAllById(roomIds);
    }
}