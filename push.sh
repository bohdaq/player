git push origin master
ssh hamelemon 'cd ~/; source .bash_profile; cd /home/bogdan/player; git stash; git pull; git stash apply;play deps;play restart;'
