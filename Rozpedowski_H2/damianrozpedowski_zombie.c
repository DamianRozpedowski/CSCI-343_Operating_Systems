#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main(){
    if (fork() == 0){
        printf("I am the child with pid %d and my parent has ppid %d", getpid(), getpid());
        sleep(1);
        exit(0);
    } else if (fork() > 0){
        printf("I am the parent and my id is %d.\n", getpid());
        sleep(30);
    } 
}