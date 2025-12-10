
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

------------------diff-------------------
comm -3 <(sort "$1") <(sort "$2") | wc -l
-----------------------------------------

------------------find-------------------

find . -type f -mtime -$1 -mtime +$2 -size -$3 -ls

-----------------------------------------

これ逆だったらすまん！
課題内容見れないからわかんない！！でもコード自体はあってる！！
