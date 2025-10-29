
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


送信されたテキスト
#!/bin/bash

if [ $# -eq 0 ]; then
    echo "使用法: $0 <ログファイル1> [ログファイル2] ..."
    exit 1
fi

for file in "$@"
do
    echo "$file"
    
    first_line=$(head -n 1 "$file")
    last_line=$(tail -n 1 "$file")
    
    start_date=$(echo "$first_line" | sed -n 's/.*\[\([0-9]\{2\}\)\/\([A-Za-z]\{3\}\)\/\([0-9]\{4\}\).*/\1-\2-\3/p')
    end_date=$(echo "$last_line" | sed -n 's/.*\[\([0-9]\{2\}\)\/\([A-Za-z]\{3\}\)\/\([0-9]\{4\}\).*/\1-\2-\3/p')
    
    printf "    from %s\n" "$start_date"
    printf "    to %s\n" "$end_date"
done
