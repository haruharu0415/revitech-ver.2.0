
# origin のリモート追跡ブランチを更新
git fetch --prune

git remote -v

git remote set-url origin https://github.com/24yn0129/revitech.git

git remote -v

git push -u origin master

# pushのやり方！

git branch 

*(自分の名前になってたらok）
なってない場合は

git branch 自分の名前

git checkout 自分の名前

git branch

git add .

git commit -m "ここにコメント"

git push -u origin 自分の名前

もしくは

git pull origin 自分の名前

# sql

DECLARE @UserIDToDelete INT = 23; 

-- -----------------------------------------------------

PRINT '--- 関連テーブルのデータを削除します ---';

DELETE FROM chat_read_status WHERE room_id IN (SELECT room_id FROM chat_rooms WHERE users_id = @UserIDToDelete);

DELETE FROM chat_messages WHERE room_id IN (SELECT room_id FROM chat_rooms WHERE users_id = @UserIDToDelete);

DELETE FROM chat_members WHERE room_id IN (SELECT room_id FROM chat_rooms WHERE users_id = @UserIDToDelete);

DELETE FROM chat_read_status WHERE users_id = @UserIDToDelete;

DELETE FROM chat_messages WHERE users_id = @UserIDToDelete;

DELETE FROM chat_members WHERE users_id = @UserIDToDelete;

DELETE FROM teacher_comments WHERE users_id = @UserIDToDelete;

DELETE FROM teacher_reviews WHERE users_id = @UserIDToDelete OR teacher_user_id = @UserIDToDelete OR student_user_id = @UserIDToDelete;

DELETE FROM teacher_subject WHERE users_id = @UserIDToDelete;

DELETE FROM enrollments WHERE users_id = @UserIDToDelete;

DELETE FROM student_profiles WHERE users_id = @UserIDToDelete;

DELETE FROM teacher_profiles WHERE users_id = @UserIDToDelete;

DELETE FROM news WHERE users_id = @UserIDToDelete;

DELETE FROM chat_rooms WHERE users_id = @UserIDToDelete;

PRINT '--- 関連データの削除が完了しました ---';

DELETE FROM Users

WHERE users_id = @UserIDToDelete;

PRINT '--- 完了しました (ID: ' + CAST(@UserIDToDelete AS VARCHAR) + ' を安全に削除しました) ---';

GO
