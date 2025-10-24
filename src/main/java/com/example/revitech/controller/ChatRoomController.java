package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;
// import java.util.UUID; // ★ UUID は使わない

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users; // ★ id は Long
import com.example.revitech.service.ChatRoomService; // ★ メソッド引数は Long
import com.example.revitech.service.UsersService; // ★ findById(Long)

@Controller
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    // チャットルーム入室処理
    @GetMapping("/chat/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        Users currentUser = userOpt.get();
        // ★ ユーザーIDを Long 型で取得 ★
        Long currentUserId = currentUser.getId();

        // メンバーシップチェック
        // ★ isUserMemberOfRoom の第一引数に Long を渡す ★
        if (!chatRoomService.isUserMemberOfRoom(currentUserId, roomId)) {
            return "redirect:/group?error=access_denied";
        }

        // 既読にする処理
        // ★ markRoomAsRead の第一引数に Long を渡す ★
        chatRoomService.markRoomAsRead(currentUserId, roomId);

        Optional<ChatRoom> roomOpt = chatRoomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            return "redirect:/group?error=not_found";
        }
        ChatRoom currentRoom = roomOpt.get();

        model.addAttribute("userId", currentUserId); // ★ Long
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("roomId", currentRoom.getId()); // Long
        model.addAttribute("roomName", currentRoom.getName());

        return "group-chat";
    }

    // グループ作成処理
    @PostMapping("/chat-room/group/create")
    // ★ memberIds の型を List<Long> に戻す ★
    public String createGroup(@RequestParam("name") String name, @RequestParam("memberIds") List<Long> memberIds) {
          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
          Optional<Users> creatorOpt = usersService.findByEmail(auth.getName());
          if (creatorOpt.isEmpty()) {
              return "redirect:/login";
        }
          Users creator = creatorOpt.get();
          Long creatorId = creator.getId(); // ★ 作成者ID (Long)

          // ★ createGroupRoom に Long を渡す ★
          ChatRoom group = chatRoomService.createGroupRoom(creatorId, name, memberIds);

          return "redirect:/chat/room/" + group.getId();
    }
}