package com.example.revitech.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatRoom;
import com.example.revitech.service.ChatRoomService;

@Controller
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    // ★【削除/非推奨】DMの画面表示ロジックはHomeController.dmViewに統合されたため、
    // このクラスからDM関連の画面表示メソッド（/dm または /chat/room/dm/）は削除します。

    // グループ作成（リダイレクト先は変更なし）
    @PostMapping("/chat-room/group/create")
    public String createGroup(@RequestParam("creatorId") Long creatorId,
                              @RequestParam("name") String name,
                              @RequestParam("memberIds") List<Long> memberIds,
                              Model model) {
        ChatRoom group = chatRoomService.createGroupRoom(creatorId, name, memberIds);
        model.addAttribute("room", group);
        return "redirect:/chat/room/" + group.getId();
    }

    // ルーム画面表示（グループチャット詳細として group.html を流用）
    @GetMapping("/chat/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model) {
        // ChatRoomServiceからroomを取得してmodelに追加する処理が必要ですが、
        // 現状はroomIdのみを渡すシンプルな実装とします。
        model.addAttribute("roomId", roomId);
        return "group"; 
    }

    // 画面用の全ルーム一覧（グループチャット一覧として group.html を流用）
    @GetMapping("/chat/rooms")
    public String getChatRoomsPage(Model model) {
        return "group"; // グループ一覧画面に遷移
    }
}