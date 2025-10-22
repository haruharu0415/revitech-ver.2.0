package com.example.revitech.dto; // パッケージ名は適宜変更

// option.html や edit_profile.html で使うためのDTO
public class UserProfileDto {
    private Long id;
    private String name;
    private String iconUrl; // アイコン画像のURL

    // コンストラクタ
    public UserProfileDto(Long id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    // デフォルトコンストラクタ (念のため)
    public UserProfileDto() {}

    // Getters (Setterは必須ではないことが多い)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getIconUrl() { return iconUrl; }

    // Setter (必要なら追加)
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}